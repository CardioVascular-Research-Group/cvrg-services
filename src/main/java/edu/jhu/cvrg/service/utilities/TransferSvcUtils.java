package edu.jhu.cvrg.service.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.service.dataTransfer.FileResultDTO;


public class TransferSvcUtils {
	private String errorMessage = "";
	
	/** uri parameter for OMNamespace.createOMNamespace() - the namespace URI; must not be null, <BR>e.g. http://www.cvrgrid.org/physionetAnalysisService/ **/
	private String sOMNameSpaceURI = "http://www.cvrgrid.org/dataTransferService/";  
	
	/** prefix parameter for OMNamespace.createOMNamespace() - the prefix<BR>e.g. physionetAnalysisService **/
	private String sOMNameSpacePrefix =  "dataTransferService";  
	
	Logger log = Logger.getLogger(TransferSvcUtils.class);
	private static Map<String, String[]> tempFilesMap = new HashMap<String, String[]>();
	
	public TransferSvcUtils(){
		debugPrintLocalln("Initializing brokerSvcUtils() in Verbose mode.");
	}
	 /** Wrapper around the 3 common functions for adding a child element to a parent OMElement.
	  * 
	  * @param name - name/key of the child element
	  * @param value - value of the new element
	  * @param parent - OMElement to add the child to.
	  * @param factory - OMFactory
	  * @param dsNs - OMNamespace
	  */
	 private void addOMEChild(String name, String value, OMElement parent, OMFactory factory, OMNamespace dsNs){
		 OMElement child = factory.createOMElement(name, dsNs);
		 child.addChild(factory.createOMText(value));
		 parent.addChild(child);
	 }

	
	public OMElement buildAnalysisReceiveReturn(boolean status){
		OMElement omeReturn = null;
		OMFactory omFactory = OMAbstractFactory.getOMFactory(); 	 
		OMNamespace omNs = omFactory.createOMNamespace(sOMNameSpaceURI, sOMNameSpacePrefix);
		omeReturn = omFactory.createOMElement("receiveAnalysisTempFiles", omNs);
		try{
			
			OMElement omeAnalysis = omFactory.createOMElement("status", omNs);
			omeAnalysis.setText(String.valueOf(status));
			
			omeReturn.addChild(omeAnalysis);
		} catch (Exception e) {
			errorMessage = "receiveAnalysisTempFiles failed. "+ e.getMessage();
			addOMEChild("status", errorMessage, omeReturn, omFactory, omNs);
			log.error(errorMessage);
		}
		return omeReturn;
	}
	
	
	public OMElement buildAnalysisResultReturn(Set<FileResultDTO> status){
		OMElement omeReturn = null;
		OMFactory omFactory = OMAbstractFactory.getOMFactory(); 	 
		OMNamespace omNs = omFactory.createOMNamespace(sOMNameSpaceURI, sOMNameSpacePrefix);
		omeReturn = omFactory.createOMElement("sendAnalysisResultFiles", omNs);
		try{
			omeReturn.addChild(ServiceUtils.makeOutputOMElement(status, "fileList", "file", omFactory, omNs));
		} catch (Exception e) {
			errorMessage = "sendAnalysisResultFiles failed. "+ e.getMessage();
			addOMEChild("status", errorMessage, omeReturn, omFactory, omNs);
			log.error(errorMessage);
		}
		return omeReturn;
	}
	

	

	public boolean storeLocalFiles(org.apache.axiom.om.OMElement e) {
		
		boolean ret = false;
		debugPrintln(" - storeLocalFiles()");
		
		Map<String, OMElement> params = ServiceUtils.extractParams(e);
		Map<String, OMElement> jobs = ServiceUtils.extractParams(params.get("jobs"));
		if(jobs != null){
			
			for (String jobId : jobs.keySet()) {
			
				String inputPath = ServiceUtils.SERVER_TEMP_ANALYSIS_FOLDER + File.separator + jobId;
				
				StringTokenizer strToken = new StringTokenizer(jobs.get(jobId).getText(), "^");
				String[] fileNames = new String[strToken.countTokens()];
				for (int i = 0; i < fileNames.length; i++) {
					String name = strToken.nextToken();
					fileNames[i] = inputPath + File.separator + name;
					
					ServiceUtils.createTempLocalFile(params, name, inputPath, name);
					
					ret = new File(fileNames[i]).exists();
					
					if(!ret){
						return ret;
					}
				}
				
				tempFilesMap.put(jobId, fileNames);
			}
		}
		
		return ret;
	}
	
	public Set<FileResultDTO> sendResultFiles(String[] resultFileNames, long groupId, long folderId, String jobId, long userId){
		
		Set<FileResultDTO> result = new HashSet<FileResultDTO>();
		
		String errorMessage = "";
		debugPrintln("moveFiles() from: local to: liferay");
		if (resultFileNames != null) {
			try {
				
				Long fileId = null;
				for(int i=0;i<resultFileNames.length;i++){
					
					File orign = new File(resultFileNames[i]);
					FileInputStream fis = new FileInputStream(orign);
					
					String path = ServiceUtils.extractPath(resultFileNames[i]);
					
					fileId = ServiceUtils.sendToLiferay(groupId, folderId, userId, path, orign.getName(), orign.length(), fis);
					
					result.add(new FileResultDTO(fileId, orign.getName()));
				}
				
			} catch (Exception e) {
				errorMessage += "sendResultFiles() failed.";
				log.error(errorMessage + " " + e.getMessage());
			}finally{
				
				String[] tempFileNames = tempFilesMap.get(jobId);
				//Delete temporary files
				if(tempFileNames != null){
					for (String fileName : tempFileNames) {
						ServiceUtils.deleteFile(fileName);
					}
					
					String jobFolder = ServiceUtils.extractPath(tempFileNames[0]);
					ServiceUtils.deleteFile(jobFolder);
					tempFilesMap.remove(jobId);
				}
			}
	    }
		return result;		
	}
	
	public void debugPrintLocalln(String text){
		debugPrintln("+ bSvcUtils: " + text);
	}
	public void debugPrintln(String text){
		System.out.println("+ TransferSvcUtils: " + text);
		log.info("+ " + text);
	}
	public void debugPrint(String text){
		log.info("+ " + text);
	}
}
