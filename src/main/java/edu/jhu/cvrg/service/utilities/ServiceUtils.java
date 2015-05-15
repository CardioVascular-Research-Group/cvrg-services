package edu.jhu.cvrg.service.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.liferay.portal.kernel.repository.model.FileEntrySoap;
import edu.jhu.cvrg.liferay.portal.service.ServiceContext;
import edu.jhu.cvrg.liferay.portlet.documentlibrary.service.http.DLAppServiceSoap;
import edu.jhu.cvrg.liferay.portlet.documentlibrary.service.http.DLAppServiceSoapServiceLocator;
import edu.jhu.cvrg.liferay.portlet.documentlibrary.service.http.Portlet_DL_DLAppServiceSoapBindingStub;
import edu.jhu.cvrg.waveform.utility.ServiceProperties;

public class ServiceUtils {

	public static final String SERVER_TEMP_ANALYSIS_FOLDER = ServiceProperties.getInstance().getProperty(ServiceProperties.TEMP_FOLDER)+"/a";
	public static final String SERVER_TEMP_CONVERSION_FOLDER = ServiceProperties.getInstance().getProperty(ServiceProperties.TEMP_FOLDER)+"/c";
	public static final String SERVER_TEMP_VISUALIZE_FOLDER = ServiceProperties.getInstance().getProperty(ServiceProperties.TEMP_FOLDER)+"/v";
	
	private static String sep = File.separator;
	
	private static final int LIFERAY_WORKFLOW_STATUS_APPROVED = 1;
	
	private static final Logger log = Logger.getLogger(ServiceUtils.class);
	
	public synchronized static Long sendToLiferay(long groupId, long folderId, long userId, String outputPath, String fileName, long fileSize, InputStream fis){
		
		log.debug(" +++++ tranferring " + fileName + " to Liferay");
		
		Long ret = null;
		
		DLAppServiceSoapServiceLocator locator = new DLAppServiceSoapServiceLocator();
		
		try {
			
			ServiceProperties props = ServiceProperties.getInstance();
			
			DLAppServiceSoap service = locator.getPortlet_DL_DLAppService(new URL(props.getProperty(ServiceProperties.LIFERAY_FILES_ENDPOINT_URL)));
			
			((Portlet_DL_DLAppServiceSoapBindingStub)service).setUsername(props.getProperty(ServiceProperties.LIFERAY_WS_USER));
			((Portlet_DL_DLAppServiceSoapBindingStub)service).setPassword(props.getProperty(ServiceProperties.LIFERAY_WS_PASSWORD));
			
			byte[] bytes = new byte[Long.valueOf(fileSize).intValue()];
			fis.read(bytes);
			fis.close();
			
			ServiceContext svc = new ServiceContext();
			svc.setScopeGroupId(groupId);
			svc.setAddGroupPermissions(true);
			
			svc.setUserId(userId);
			svc.setGuestOrUserId(userId);
			svc.setWorkflowAction(LIFERAY_WORKFLOW_STATUS_APPROVED);
			
			
			FileEntrySoap file = service.addFileEntry(groupId, folderId, fileName, "", fileName, "", "1.0", bytes, svc);
			
			
			ret = file.getFileEntryId();
			
			deleteFile(outputPath, fileName);
			
			log.debug(" +++++ Done tranferring ");
			
		} catch (Exception e) {
			log.error("Error on sendToLiferay: "+e.getMessage());
		}

		return ret;
	}
	
	public synchronized static boolean deleteFolderFromLiferay(long groupId, long folderId, long userId){
		
		log.debug(" +++++ removing folder " + folderId + " from Liferay");
		
		boolean ret = false;
		
		DLAppServiceSoapServiceLocator locator = new DLAppServiceSoapServiceLocator();
		
		try {
			
			ServiceProperties props = ServiceProperties.getInstance();
			
			DLAppServiceSoap service = locator.getPortlet_DL_DLAppService(new URL(props.getProperty(ServiceProperties.LIFERAY_FILES_ENDPOINT_URL)));
			
			((Portlet_DL_DLAppServiceSoapBindingStub)service).setUsername(props.getProperty(ServiceProperties.LIFERAY_WS_USER));
			((Portlet_DL_DLAppServiceSoapBindingStub)service).setPassword(props.getProperty(ServiceProperties.LIFERAY_WS_PASSWORD));
			
			ServiceContext svc = new ServiceContext();
			svc.setScopeGroupId(groupId);
			
			svc.setUserId(userId);
			svc.setGuestOrUserId(userId);
			
			service.deleteFolder(folderId);
			
			log.debug(" +++++ Deleted ");
			ret = true;
		} catch (Exception e) {
			log.error("Error on deleteFolderFromLiferay: "+e.getMessage());
		}

		return ret;
	}
	
	public static void deleteFile(String inputPath, String inputFilename) {
		deleteFile(inputPath + sep + inputFilename);
	}

	public static void deleteFile(String fullPathFileName) {
		File targetFile = new File(fullPathFileName);
		if(targetFile.exists()){
			targetFile.delete();
		}else{
			log.error("Cannot find file to delete: " + fullPathFileName);
		}
	}
	

	public static Map<String, OMElement> extractParams(OMElement e){
		Map<String, OMElement> paramMap = new HashMap<String, OMElement>();  
		for (Iterator<?> iterator = e.getChildren(); iterator.hasNext();) {
			Object type = (Object) iterator.next();
			if(type instanceof OMElement){
				OMElement node = (OMElement)type;
				paramMap.put(node.getLocalName(), node);
			}
		}
		return paramMap;
	}
	
	public static void createTempLocalFile(Map<String, OMElement> mapWServiceParam, String tagName, String inputPath, String inputFilename) {
		OMElement fileNode = mapWServiceParam.get("file_"+tagName);
		if(fileNode != null){
			OMText binaryNode = (OMText) fileNode.getFirstOMChild();
			DataHandler contentDH = (DataHandler) binaryNode.getDataHandler();
			
			File targetDirectory = new File(inputPath);
			
			File targetFile = new File(inputPath + sep + inputFilename);
			
			try {
				targetDirectory.mkdirs();
				
				InputStream fileToSave = contentDH.getInputStream();
				
				OutputStream fOutStream = new FileOutputStream(targetFile);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = fileToSave.read(bytes)) != -1) {
					fOutStream.write(bytes, 0, read);
				}

				fileToSave.close();
				fOutStream.flush();
				fOutStream.close();
				
			} catch (IOException e) {
				log.error("Error on createTempLocalFile: "+e.getMessage());
			}finally{
				log.info("File created? " + targetFile.exists());
			}
		}
	}
	
	public static String extractPath(String sHeaderPathName){
		String sFilePath="";
		int iIndexLastSlash = sHeaderPathName.lastIndexOf("/");
		
		sFilePath = sHeaderPathName.substring(0,iIndexLastSlash+1);
		
//		log.info("extractPath() from: '" + sHeaderPathName + "' : '" + sFilePath + "'");
		
		return sFilePath;
	}
	
	public static String extractName(String sFilePathName){
		String sFileName="";
		int iIndexLastSlash = sFilePathName.lastIndexOf("/");
		
		sFileName = sFilePathName.substring(iIndexLastSlash+1);

//		log.info("extractName() from: '" + sFilePathName + "' : '" + sFileName + "'");

		return sFileName;
	}
	
	/** Converts the array of output (relative) filenames to a single OMElement whose sub-elements are the filenames.
	 * 
	 * @param asFileNames - array of (relative) file path/name strings.
	 * @return - a single OMElement containing the path/names.
	 */
	public static OMElement makeOutputOMElement(List<String> asFileNames, String sParentOMEName, String sChildOMEName, OMFactory omFactory, OMNamespace omNs){
//		log.info("makeOutputOMElement()" + asFileNames.size() + " file names");
		OMElement omeArray = null;
		if (asFileNames != null) {
			try {
				omeArray = omFactory.createOMElement(sParentOMEName, omNs); 
				
				for(int i=0; i<asFileNames.size();i++){
					addOMEChild(sChildOMEName, asFileNames.get(i), omeArray,omFactory,omNs);					
				}
			}catch(Exception e){
				log.error(e.getMessage());
			}
		}
		return omeArray;
	}

	
	public static OMElement makeOutputOMElement(Set<?> asFileNames, String sParentOMEName, String sChildOMEName, OMFactory omFactory, OMNamespace omNs){
		
		OMElement omeArray = null;
		if (asFileNames != null) {
			try {
				omeArray = omFactory.createOMElement(sParentOMEName, omNs); 
				
				for (Object o : asFileNames) {
					
					OMElement omeItem = omFactory.createOMElement(sChildOMEName, omNs);
					
					java.lang.reflect.Method[] methods = o.getClass().getMethods();
					
					for (java.lang.reflect.Method method : methods) {
						if(method.getName().startsWith("get") && method.getParameterTypes().length == 0 && !method.getName().equals("getClass")){
							
							String name = method.getName().substring(3);
							Object value = method.invoke(o, null);
							
							addOMEChild(name, value.toString(), omeItem,omFactory,omNs);		
						}
					}
					
					omeArray.addChild(omeItem);
				}
				
			}catch(Exception e){
				log.error(e.getMessage());
			}
		}
		return omeArray;
	}
	
	/** Wrapper around the 3 common functions for adding a child element to a parent OMElement.
	 * 
	 * @param name - name/key of the child element
	 * @param value - value of the new element
	 * @param parent - OMElement to add the child to.
	 * @param factory - OMFactory
	 * @param dsNs - OMNamespace
	 */
	public static void addOMEChild(String name, String value, OMElement parent, OMFactory factory, OMNamespace dsNs){
		OMElement child = factory.createOMElement(name, dsNs);
		child.addChild(factory.createOMText(value));
		parent.addChild(child);
	}
}
