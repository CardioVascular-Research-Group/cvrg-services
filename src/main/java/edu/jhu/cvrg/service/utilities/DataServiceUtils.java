package edu.jhu.cvrg.service.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.service.visualize.VisualizationData;

public class DataServiceUtils {

	String errorMessage="";
	
	private String[] inputFileNames = null;
	private String fileName="";
	private long fileSize=0;
	private int offsetMilliSeconds=0, durationMilliSeconds=0, graphWidthPixels=0, signalCount= 0, samplesPerSignal = 0;
	private double sampleFrequency = 0.0;
	private boolean skipSamples = false;
	
	public Map<String, Object> mapCommandParam = null;
	public String sJobID="";
	public String tempFile ="";
	public Long userId;
	public String[] saLeadCSV = null; // array of comma separated ECG values, one string per lead.
	public VisualizationData visData=null;
	public boolean bTestPattern = false;
	
	
	private static Logger log = Logger.getLogger(DataServiceUtils.class);
	
	public Map<String, OMElement> parseInputParametersType2(OMElement param0){
		// parse the input parameter's OMElement XML into a Map.
	    Map<String, OMElement> mapWServiceParam = null;
	    
		try {
			mapWServiceParam = ServiceUtils.extractParams(param0);
			// Assign specific input parameters to local variables.
			
			int iFileCount      = Integer.parseInt( (String) mapWServiceParam.get("fileCount").getText() ); 
			int iParameterCount = Integer.parseInt( (String) mapWServiceParam.get("parameterCount").getText()); 
			/********************************************/
			bTestPattern		= Boolean.parseBoolean((String) mapWServiceParam.get("testPattern").getText());
			if(mapWServiceParam.get("fileSize") != null && bTestPattern){
				fileSize			= Long.parseLong((String) mapWServiceParam.get("fileSize").getText());
			}
			
			offsetMilliSeconds	= Integer.parseInt((String) mapWServiceParam.get("offsetMilliSeconds").getText());
			durationMilliSeconds= Integer.parseInt((String) mapWServiceParam.get("durationMilliSeconds").getText());
			graphWidthPixels	= Integer.parseInt((String) mapWServiceParam.get("graphWidthPixels").getText());
			userId				= Long.valueOf((String) mapWServiceParam.get("userId").getText());
			
			sampleFrequency		= Double.valueOf((String) mapWServiceParam.get("sampleFrequency").getText());
			signalCount			= Integer.valueOf((String) mapWServiceParam.get("signalCount").getText());
			samplesPerSignal	= Integer.valueOf((String) mapWServiceParam.get("samplesPerSignal").getText());
			
			if(mapWServiceParam.get("noSkip")!=null){
				skipSamples	= Boolean.valueOf((String) mapWServiceParam.get("noSkip").getText());
			}
			
			debugPrintln("Extracting fileNameList, should be " + iFileCount + " files ...;");

			if(!bTestPattern & (iFileCount>0)){
				OMElement filehandlelist = (OMElement) mapWServiceParam.get("fileNameList");
				debugPrintln("Building Input Filename array...;");
				inputFileNames = buildParamArray(filehandlelist);
				debugPrintln("Finished Extracting fileNameList, founnd " + inputFileNames.length + " files ...;");
				
			}
			if(iParameterCount>0){
				debugPrintln("Extracting parameterlist, should be " + iParameterCount + " parameters ...;");
				OMElement parameterlist = (OMElement) mapWServiceParam.get("parameterlist");
				debugPrintln("Building Command Parameter map...;");
				mapCommandParam = buildParamMap(parameterlist);
			}else{
				debugPrintln("There are no parameters, so Command Parameter map was not built.");
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "parseInputParametersType2 failed.";
		}
		
		return mapWServiceParam;
	}
	


	/** Parses a service's incoming XML and builds a string array of all the parameters for easy access.
	 * @param param0 - OMElement representing XML with the incoming parameters.
	 */
	private String[] buildParamArray(OMElement param0){
		debugPrintln("buildParamArray()");

		ArrayList<String> paramList = new ArrayList<String>();

		try {
			@SuppressWarnings("unchecked")
			Iterator<OMElement> iterator = param0.getChildren();
			
			while(iterator.hasNext()) {
				OMElement param = iterator.next();
				paramList.add(param.getText());

				debugPrintln(" -- paramList.add(v): " + param.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String[] ret = new String[paramList.size()];
		ret = paramList.toArray(ret);
		
		return ret;
	}
	
	/** Parses a service's incoming XML and builds a Map of all the parameters for easy access.
	 * @param param0 - OMElement representing XML with the incoming parameters.
	 */
	public Map<String, Object> buildParamMap(OMElement param0){
		debugPrintln("buildParamMap()");
	
		String key="";
		Object oValue = null;
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {
			@SuppressWarnings("unchecked")
			Iterator<OMElement> iterator = param0.getChildren();
			
			while(iterator.hasNext()) {
				OMElement param = iterator.next();
				key = param.getLocalName();
				oValue = param.getText();
				if(oValue.toString().length()>0){
					debugPrintln(" - Key/Value: " + key + " / '" + oValue + "'");
					paramMap.put(key,oValue);
				}else{
					Iterator<OMElement> iterTester = param.getChildren();
					if(iterTester.hasNext()){
						OMElement omValue = (OMElement)param;
						paramMap.put(key,param);
					}else{
						debugPrintln(" - Key/Blank: " + key + " / '" + oValue + "'");
						paramMap.put(key,"");	
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			errorMessage = "buildParamMap() failed.";
			return null;
		}
		
		debugPrintln("buildParamMap() found " + paramMap.size() + " parameters.");
		return paramMap;
	}
	



	/** Wrapper around the 3 common functions for adding a child element to a parent OMElement.
	 * 
	 * @param name - name/key of the child element
	 * @param value - value of the new element
	 * @param parent - OMElement to add the child to.
	 * @param factory - OMFactory
	 * @param dsNs - OMNamespace
	 */
	public void addOMEChild(String name, String value, OMElement parent, OMFactory factory, OMNamespace dsNs){
		OMElement child = factory.createOMElement(name, dsNs);
		child.addChild(factory.createOMText(value));
		parent.addChild(child);
	}


	
	
	/** Find the first filename in the array with the "hea" extension.
	 * 
	 * @param asInputFileNames - array of filenames to search
	 * @return - full path/name.ext as found in the array.
	 */
	public String findHeaderPathName(String[] asInputFileNames){
		debugPrintln("findHeaderPathName()");
		String sHeaderPathName="";
		sHeaderPathName = findPathNameExt(asInputFileNames, "hea");
		return sHeaderPathName;
	}

	/** Find the first filename in the array with the specified extension.
	 * 
	 * @param asInputFileNames - array of filenames to search
	 * @param sExtension - extension to look for, without the dot(".") e.g. "hea".
	 * @return - full path/name.ext as found in the array.
	 */
	public String findPathNameExt(String[] asInputFileNames, String sExtension){
		debugPrintln("findHeaderPathName()");
		String sHeaderPathName="", sTemp="";
		int iIndexPeriod=0;
		
		for(int i=0;i<asInputFileNames.length;i++){
			sTemp = asInputFileNames[i];
			debugPrintln("- asInputFileNames[" + i + "]: " + asInputFileNames[i]);
			iIndexPeriod = sTemp.lastIndexOf(".");
			
			if( sTemp.substring(iIndexPeriod+1).equalsIgnoreCase(sExtension) ){
				sHeaderPathName = sTemp;
			}
		}
		debugPrintln("- ssHeaderPathName: " + sHeaderPathName);
		return sHeaderPathName;
	}

	
	
	public void debugPrintln(String text){
		log.info("++ DataServiceUtils + " + text);
	}



	public int getOffsetMilliSeconds() {
		return offsetMilliSeconds;
	}



	public int getDurationMilliSeconds() {
		return durationMilliSeconds;
	}



	public int getGraphWidthPixels() {
		return graphWidthPixels;
	}



	public int getSignalCount() {
		return signalCount;
	}



	public int getSamplesPerSignal() {
		return samplesPerSignal;
	}



	public double getSampleFrequency() {
		return sampleFrequency;
	}



	public String[] getInputFileNames() {
		return inputFileNames;
	}



	public String getFileName() {
		return fileName;
	}



	public long getFileSize() {
		return fileSize;
	}



	public boolean isSkipSamples() {
		return skipSamples;
	}



	public void setSkipSamples(boolean noSkip) {
		this.skipSamples = noSkip;
	}

}
