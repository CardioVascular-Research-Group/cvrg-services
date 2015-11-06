package edu.jhu.cvrg.service.analysis;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.analysis.vo.AnalysisResultType;
import edu.jhu.cvrg.analysis.vo.AnalysisType;
import edu.jhu.cvrg.analysis.vo.AnalysisVO;
import edu.jhu.cvrg.waveform.utility.ServiceProperties;
import edu.jhu.cvrg.waveform.utility.ServiceUtils;
import edu.jhu.cvrg.waveform.utility.WebServiceUtility;


public class AnalysisUtils {

	String errorMessage="";
	/** uri parameter for OMNamespace.createOMNamespace() - the namespace URI; must not be null, <BR>e.g. http://www.cvrgrid.org/physionetAnalysisService/ **/
	private String sOMNameSpaceURI = "http://www.cvrgrid.org/physionetAnalysisService/";  
	
	/** prefix parameter for OMNamespace.createOMNamespace() - the prefix<BR>e.g. physionetAnalysisService **/
	private String sOMNameSpacePrefix =  "physionetAnalysisService";  
	public Map<String, Object> mapCommandParam = null;
//	public List<String> inputFileNames = null;
	
	private long folderID;
	private long groupID;
	private String userID;
	
	private int channels;
	private String leadNames;
	private double scalingFactor;
	private int samplesPerChannel;
	private float samplingRate;
	private String timeseriesId;
	private String subjectId;
	private String openTsdbHost;
	
	private static final Logger log = Logger.getLogger(AnalysisUtils.class);
	
//	private String sep = File.separator;
	
	public AnalysisVO parseInputParametersType2(OMElement param0, AnalysisType algorithm, AnalysisResultType resultType){
		AnalysisVO ret = null;
//		log.info("<cvrg-services> parseInputParametersType2()");
		try {
			Map<String, OMElement> params = ServiceUtils.extractParams(param0);
			
			String jobID     	= params.get("jobID").getText() ;
			userID 		     	= params.get("userID").getText() ;
			folderID      		= Long.parseLong(params.get("folderID").getText()) ;
			groupID      		= Long.parseLong(params.get("groupID").getText()) ;
			subjectId 			= params.get("subjectID").getText();
			
			channels 			= Integer.parseInt(params.get("channels").getText());
			leadNames 			= params.get("leadNames").getText();
			scalingFactor 		= Double.parseDouble(params.get("scalingFactor").getText());
			samplesPerChannel	= Integer.parseInt(params.get("samplesPerChannel").getText());
			samplingRate		= Float.parseFloat(params.get("samplingRate").getText());
			timeseriesId 		= params.get("timeseriesId").getText();
			openTsdbHost		= params.get("openTsdbHost").getText();
			
			
			OMElement parameterlist = (OMElement) params.get("parameterlist");
//			log.info("<cvrg-services> ****  parameterlist ****: " + parameterlist);
			
//			String inputPath = ServiceUtils.SERVER_TEMP_ANALYSIS_FOLDER + sep + jobID;
//			StringTokenizer strToken = new StringTokenizer(params.get("fileNames").getText(), "^");
//			List<String> fileNames = new ArrayList<String>();
//			while (strToken.hasMoreTokens()) {
//				String name = strToken.nextToken();
//				//ServiceUtils.createTempLocalFile(params, name, userID, inputPath, name);
//				fileNames.add(inputPath + sep + name);
//			}
			
//			inputFileNames = fileNames;

			if(parameterlist != null){
//				log.info("<cvrg-services> Building Command Parameter map...;");
				mapCommandParam = buildParamMap(parameterlist);
			}else{
				log.info("<cvrg-services> There are no parameters, so Command Parameter map was not built.");
				mapCommandParam = new HashMap<String, Object>(); 
			}
			
			ret = new AnalysisVO(jobID, algorithm, resultType, null, mapCommandParam);
			ret.setRecordName(subjectId);
			ret.setTempFolder(ServiceUtils.SERVER_TEMP_ANALYSIS_FOLDER);
			
		} catch (Exception e) {
			errorMessage = "parseInputParametersType2 failed.";
//			log.error(parseInputParametersType2() " + e.getMessage());
			log.error("<cvrg-services> " + errorMessage + " " + e.getMessage());
		}
		
		return ret;
	}
	
	/** Parses a service's incoming XML and builds a Map of all the parameters for easy access.
	 * @param param0 - OMElement representing XML with the incoming parameters.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> buildParamMap(OMElement param0){
//		log.info("<cvrg-services> buildParamMap()");
	
		String key="";
		Object oValue = null;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			Iterator<OMElement> iterator = param0.getChildren();
			
			while(iterator.hasNext()) {
				OMElement param = iterator.next();
				key = param.getLocalName();
				oValue = param.getText();
				if(oValue.toString().length()>0){
//					log.info("<cvrg-services>  - Key/Value: " + key + " / '" + oValue + "'");
					paramMap.put(key,oValue);
				}else{
					Iterator<OMElement> iterTester = param.getChildren();
					if(iterTester.hasNext()){
						OMElement omValue = (OMElement)param;
						paramMap.put(key,param);
//						log.info("<cvrg-services> - Key/OMElement Value: " + key + " / " + omValue.getText()); // param.getText());
					}else{
//						log.info("<cvrg-services> - Key/Blank: " + key + " / '" + oValue + "'");
						paramMap.put(key,"");	
					}
				}

			}
		} catch (Exception e) {
			errorMessage = "buildParamMap() failed.";
			log.error("<cvrg-services> " + errorMessage + " " + e.getMessage());
			return null;
		}
		
//		log.info("<cvrg-services> found " + paramMap.size() + " parameters.");
		return paramMap;
	}
	

	public OMElement buildOmeReturnType2(AnalysisVO analysis, Map<Long, String> fileMap){
		OMElement omeReturn = null;
		try{
			OMFactory omFactory = OMAbstractFactory.getOMFactory(); 	 
			OMNamespace omNs = omFactory.createOMNamespace(sOMNameSpaceURI, sOMNameSpacePrefix); 	 

			omeReturn = omFactory.createOMElement(analysis.getType().getOmeName(), omNs); 
	
			// Converts the array of filenames to a single "^" delimited String for output.
			if (analysis.getErrorMessage() == null || analysis.getErrorMessage().length() == 0){
				
				ServiceUtils.addOMEChild("jobID", analysis.getJobId(), omeReturn, omFactory, omNs);
				
				if(analysis.getOutputData() != null){
					ServiceUtils.addOMEChild("outputData", analysis.getOutputData(),omeReturn,omFactory,omNs);
					
				}
				
				ServiceUtils.addOMEChild("filecount", new Long(analysis.getOutputFileNames().size()).toString(),omeReturn,omFactory,omNs);
				omeReturn.addChild( ServiceUtils.makeOutputOMElement(analysis.getOutputFileNames(), "filenamelist", "filename", omFactory, omNs) );
				
//				OMElement result = sendResultsBack(analysis);
//				Map<String, OMElement> params = ServiceUtils.extractParams(result);
//				omeReturn.addChild(params.get("fileList"));
				
				omeReturn.addChild(ServiceUtils.makeOutputOMElement(fileMap.keySet(), "fileList", "fileId", omFactory, omNs));
				
			}else{
				log.error("Analysis errorMessage: '" + analysis.getErrorMessage() + "'");
					
				if(analysis.getFileNames() != null && !analysis.getFileNames().isEmpty()){
					File tmpJobFolder = new File(ServiceUtils.extractPath(analysis.getFileNames().get(0)));
//					for (File f : tmpJobFolder.listFiles()) {
//						log.error(" * deleting file: " + f.getAbsoluteFile());
//						f.delete();
//					}
//					log.error(" * deleting folder: " + tmpJobFolder.getAbsoluteFile());
//					tmpJobFolder.delete();
				}
				
				ServiceUtils.addOMEChild("error",analysis.getErrorMessage(),omeReturn,omFactory,omNs);
			}
		} catch (Exception e) {
			errorMessage = "genericWrapperType2 failed.";
			log.error(errorMessage + " " + e.getMessage());
		}
//		log.info("omeReturn built ");
		return omeReturn;
	}
	
	private OMElement sendResultsBack(AnalysisVO analysis) {
		
		Map<String, String> parameterMap = new HashMap<String, String>();
		
		parameterMap.put("jobID", analysis.getJobId());
		parameterMap.put("groupID", String.valueOf(this.groupID));
		parameterMap.put("folderID", String.valueOf(this.folderID));
		parameterMap.put("userID", this.userID);
		
		String fileNames = "";
		for (String name : analysis.getOutputFileNames()) {
			fileNames+=(name+'^');
		}
//		log.info("resultFileNames: " + fileNames);
		parameterMap.put("resultFileNames", fileNames);
		
		ServiceProperties props = ServiceProperties.getInstance();
		
//		log.info("Calling data tranfer service: " + props.getProperty(ServiceProperties.DATATRANSFER_SERVICE_METHOD));
		return WebServiceUtility.callWebService(parameterMap, props.getProperty(ServiceProperties.DATATRANSFER_SERVICE_METHOD), props.getProperty(ServiceProperties.DATATRANSFER_SERVICE_NAME), props.getProperty(ServiceProperties.MAIN_SERVICE_URL), null);
	}

	public int getChannels() {
		return channels;
	}

	public String getLeadNames() {
		return leadNames;
	}

	public double getScalingFactor() {
		return scalingFactor;
	}

	public int getSamplesPerChannel() {
		return samplesPerChannel;
	}

	public float getSamplingRate() {
		return samplingRate;
	}

	public String getTimeseriesId() {
		return timeseriesId;
	}

	public String getSubjectId() {
		return subjectId;
	}
	
	public Map<String, Object> getMap(){
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("userID", userID);
		map.put("folderID", folderID);
		map.put("groupID", groupID);
		map.put("subjectID", subjectId);
		map.put("channels", channels);
		map.put("leadNames", leadNames);
		map.put("scalingFactor", scalingFactor);
		map.put("samplesPerChannel", samplesPerChannel);
		map.put("samplingRate", samplingRate);
		map.put("timeseriesId", timeseriesId);
		map.put("openTsdbHost", openTsdbHost);
	
		return map;
	}
}
