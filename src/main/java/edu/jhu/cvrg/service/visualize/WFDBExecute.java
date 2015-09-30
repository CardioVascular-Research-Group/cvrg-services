package edu.jhu.cvrg.service.visualize;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.service.utilities.DataServiceUtils;
import edu.jhu.cvrg.service.utilities.ServiceUtils;
import edu.jhu.cvrg.waveform.model.VisualizationData;
import edu.jhu.cvrg.waveform.utility.ECGVisualizeProcessor;

public class WFDBExecute {
	
	public String errorMessage="";
	public DataServiceUtils util;
	private static final int SHORTBYTES = 2;

	private static Logger log = Logger.getLogger(WFDBExecute.class);
	/** constructor
	 * @param annUtil - analysisUtil instance used by the webservice to parse the parameters and files lists.
	 */
	public WFDBExecute(DataServiceUtils annUtil) {
		util = annUtil;
	}

	/** Service that returns a short subset of an ecg stored in a specified file, suitable for graphical display.
	 * 
	 * @param param0 - Contains elements brokerURL, mySqlURL, fileName... etc<BR>
	 *  brokerURL - web address of the data file repository <BR>
	 *  fileName - file containing the ECG data in RDT format.<BR>
	 *  fileSize - used to size the file reading buffer.<BR>
	 *  offsetMilliSeconds - number of milliseconds from the beginning of the ECG at which to start the graph.<BR>
	 *  durationMilliSeconds - The requested length of the returned data subset, in milliseconds.<BR>
	 *  graphWidthPixels - Width of the zoomed graph in pixels(zoom factor*unzoomed width), hence the maximum points needed in the returned VisualizationData.<BR>
	 * @return - OMElement containing the values in the results
	 */
	public org.apache.axiom.om.OMElement collectWFDBdataSegment() {
		
		debugPrintln("** collectWFDBdataSegment() 1.0 called; "); 
		
		long startTime = System.currentTimeMillis(), stopTime = 0, elapsed=0;
		int iLeadCount=0, iDataArrayLength=0,iDataArrayWidth=0;
		boolean success = true;

		// create output parent OMElement
		OMFactory factory = OMAbstractFactory.getOMFactory();
		OMNamespace dsNs = factory.createOMNamespace("http://www.cvrgrid.org/nodeDataService/", "dataStaging");

		StringBuilder[] saLeadCSV = null; // array of comma separated ECG values, one string per lead.
		VisualizationData visData=null;
		try{
			// Parse parameters
			debugPrintln("- parsing the web service's parameters without regard to order.");
			// Assign specific input parameters to local variables.
			
			//**************************************************
			debugPrintln("** offsetMilliSeconds: " + util.getOffsetMilliSeconds() + " durationMilliSeconds: " + util.getDurationMilliSeconds());
			
			debugPrintln("[=====================================================================]");
			debugPrintln(" offsetMilliSeconds: " + util.getOffsetMilliSeconds());
			debugPrintln(" durationMilliSeconds: " + util.getDurationMilliSeconds());
			debugPrintln(" graphWidthPixels: " + util.getGraphWidthPixels());
			debugPrintln("[=====================================================================]");

			debugPrintln("Creating Visualization Data bean.");
			
			visData = ECGVisualizeProcessor.fetchDataSegment(util.getTimeseriesId(), util.getLeadNames(), util.getOffsetMilliSeconds(), util.getDurationMilliSeconds(), util.getGraphWidthPixels(), util.isSkipSamples(), util.getSamplesPerSignal(), (int) util.getSampleFrequency(), util.getAdugain());
			
			iLeadCount = visData.getECGDataLeads();
			iDataArrayLength = visData.getECGData().length; // rows/leads
			iDataArrayWidth = visData.getECGData()[0].length; // columns/samples
			debugPrintln(" iDataArrayLength: " + iDataArrayWidth);
			saLeadCSV = new StringBuilder[iLeadCount+1];
			debugPrintln(" iLeadCount: " + iLeadCount);
			
			//initialize all to an empty string.
			for(int lead=0;lead < iDataArrayWidth;lead++){
//				debugPrintln("Initializing CSV for lead: " + lead + " of " + iLeadCount);
				saLeadCSV[lead] = new StringBuilder(); 
			}

			//build a comma delimited list for each column
			for(int row=0;row < iDataArrayLength;row++){  
//				if(row<10) debugPrintln("Building a CSV list for row: " + row + " of " + visData.getECGDataLength());
				for(int lead=0;lead < iDataArrayWidth;lead++){
					saLeadCSV[lead].append(visData.getECGData()[row][lead]).append(',');
				}
			}						

			// trim trailing comma 
			for(int lead=0;lead < iDataArrayWidth;lead++){
//				debugPrintln("Finishing CSV for lead: " + lead + " of " + iLeadCount);
				saLeadCSV[lead].deleteCharAt(saLeadCSV[lead].length()-1);
			}
		} catch (Exception e) {
			System.err.println("collectVisualizationData failed while loading data from ecg file.");
			e.printStackTrace();
			success=false;
		}


		// build return xml
		OMElement collectVisualizationData = factory.createOMElement("collectVisualizationData", dsNs);
		if(success){
			debugPrintln("Building OMElement from Web Service return values");
			util.addOMEChild("Status", 			"success",									collectVisualizationData,factory,dsNs);
			util.addOMEChild("SampleCount", 	String.valueOf(visData.getECGDataLength()),	collectVisualizationData,factory,dsNs);
			util.addOMEChild("LeadCount", 		String.valueOf(iLeadCount),					collectVisualizationData,factory,dsNs);
			util.addOMEChild("Offset", 			String.valueOf(visData.getOffset()),		collectVisualizationData,factory,dsNs);
			util.addOMEChild("SkippedSamples", 	String.valueOf(visData.getSkippedSamples()),collectVisualizationData,factory,dsNs);
			util.addOMEChild("SegmentDuration", String.valueOf(visData.getMsDuration()),	collectVisualizationData,factory,dsNs);
			for(int lead=0;lead < iDataArrayWidth;lead++){
				util.addOMEChild("lead_"+lead, 	saLeadCSV[lead].toString(),					collectVisualizationData,factory,dsNs);
			}	
		}else{
			util.addOMEChild("Status", 			"fail",										collectVisualizationData,factory,dsNs);			
		}

		stopTime = System.currentTimeMillis();
		elapsed = (stopTime - startTime);
		
		debugPrintln(" finished, execution time(ms): " + elapsed);

		return collectVisualizationData;
	}

	
	
	/** Reads the file from the brokerURL and stores it as the RdtData of a VisualizationData.
	 * Then it stores that in a NodeBrokerData, which it returns via the callback.
	 *
	 * @param fileSize - used to size the file reading buffer.
	 * @param offsetMilliSeconds - number of milliseconds from the beginning of the ECG at which to start the graph.
	 * @param durationMilliSeconds - The requested length of the returned data subset, in milliseconds.
	 * @param graphWidthPixels - Width of the zoomed graph in pixels(zoom factor*unzoomed width), hence the maximum points needed in the returned VisualizationData.
	 * @param callback - call back handler class.
	 * 	 
	 * @see org.cvrgrid.widgets.node.client.BrokerService#fetchSubjectVisualization(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, int, int)
	 */
	private VisualizationData fetchSubjectVisualizationTestPattern(long fileSize, int offsetMilliSeconds, int durationMilliSeconds, int graphWidthPixels) {
		debugPrintln("+ fetchSubjectVisualizationTestPattern(), returning 3 leads with sine waves, with 1/3 of cycle phase shift between.");
		VisualizationData visualizationData = new VisualizationData();
		try {
			//	 * @param skippedSamples - number of samples to skip after each one returned. To adjust for graph resolution.
			int samplesPerPixel, skippedSamples, segDurationInSamples, requestedMaxPoints;
			short channels = 3; // hard coded, three leads worth of test-pattern data.
			short samplingRate =1000; // replaced with subjectData.setSamplingRate() 
			float fRateMsec = (float)(1000.0/samplingRate); // Sampling rate (samples per second or "Hz") converted to milliseconds per sample.
			if (offsetMilliSeconds<0)offsetMilliSeconds=0; // cannot read before the beginning of the file.
			int segOffset = (int) (offsetMilliSeconds*fRateMsec);// starting sample number of requested segment.
	
			//-------------------------------------------------
			// Calculate and Set Visualization parameters
			final int REALBUFFERSIZE = (int) fileSize;
			int counts = REALBUFFERSIZE / (channels * SHORTBYTES); // maximum number of samples which could be contained in a file of this size.
			segDurationInSamples = (int) (fRateMsec*durationMilliSeconds); // requested segment duration in samples
			if(segDurationInSamples>graphWidthPixels){
				samplesPerPixel=segDurationInSamples/graphWidthPixels;// number of samples which will be represented by each pixel available on the display.
				requestedMaxPoints = graphWidthPixels;
			}else{
				samplesPerPixel=1;
				requestedMaxPoints = segDurationInSamples;
			}
			skippedSamples = samplesPerPixel-1;
	
			int availableSamples = counts - segOffset; // total number of remaining samples from this offset.
			int availablePoints = availableSamples/samplesPerPixel; // total number of graphable points from this offset.
			int maxPoints = 0; // maximum data points that can be returned.
			// ensure that the copying loop doesn't try to go past the end of the data file.
			if(availablePoints > requestedMaxPoints) {
				maxPoints = requestedMaxPoints;
			} else {  // Requested duration is longer than the remainder after the offset.
				if(segDurationInSamples < counts){ // Requested duration is less than the file contains.
					// move the offset back so the requested amount of samples can be returned.
					segOffset = counts - segDurationInSamples;
					maxPoints = requestedMaxPoints;
				}else{	// Requested duration is longer than the file contains.
					maxPoints = availablePoints;
				}
			}
			debugPrintln("+ samplingRate: " + samplingRate); 
			debugPrintln("+ fRateMsec: " + fRateMsec); 
			debugPrintln("+ offsetMilliSeconds: " + offsetMilliSeconds);
			debugPrintln("+ durationMilliSeconds: " + durationMilliSeconds);
			debugPrintln("+ segOffset: " + segOffset); 
			debugPrintln("+ counts: " + counts); 
			debugPrintln("+ durationInSamples: " + segDurationInSamples); 
			debugPrintln("+ graphWidthPixels: " + graphWidthPixels); 
			debugPrintln("+ samplesPerPixel: " + samplesPerPixel); 
			debugPrintln("+ requestedMaxPoints: " + requestedMaxPoints); 
			debugPrintln("+ availableSamples: " + availableSamples); 
			debugPrintln("+ availablePoints: " + availablePoints); 
	
			visualizationData.setECGDataLength(maxPoints);
			visualizationData.setECGDataLeads(channels);
			visualizationData.setOffset(segOffset);
			visualizationData.setSkippedSamples(skippedSamples);
			int msDuration = (counts*1000)/samplingRate;
			visualizationData.setMsDuration(msDuration);
	
			debugPrintln("+ VD.maxPoints: " + maxPoints); 
			debugPrintln("+ VD.channels: " + channels); 
			debugPrintln("+ VD.vizOffset: " + segOffset); 
			debugPrintln("+ VD.skippedSamples: " + skippedSamples); 
			debugPrintln("+ VD.msDuration: " + msDuration); 
	
			//------------------------------------------------
			// generate the data from trig functions.
			double[][] tempData = new double[maxPoints][channels+1];
			//				int fileOffset = vizOffset*channels*SHORTBYTES; //offset in bytes from the beginning of the file.
	
			double index;
			index = (double)segOffset; // index of the first sample to return data for, index is in samples not bytes.
	
			for(int point = 0; point < maxPoints; point++) {
				index = index + 1 ;
	
				tempData[point][1] = Math.sin(index/16.0)*500.0;
				tempData[point][0] = point*samplesPerPixel;// milliseconds 
				tempData[point][2] = Math.sin((index/8.0)+2.094)*1000.0; // plus 1/3 cycle 
				tempData[point][3] = Math.sin((index/16.0)+4.189)*500.0; // plus 2/3 cycle
			}
			debugPrintln("+ Test Pattern data array contains: " + tempData[0].length + " columns(Leads) and " + tempData.length + " rows(Samples).");
			visualizationData.setECGData(tempData);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		return visualizationData;
	}
	
	
	
	/** Reads the WFDB file from the brokerURL and stores it as the RdtData of a VisualizationData.
	 * It is assuming that the file is in RDT format, with 3 leads.
	 *
	 * @param tempFile - name of a local WFDB file containing ECG data. 
	 * @param fileSize - used to size the file reading buffer.
	 * @param offsetMilliSeconds - number of milliseconds from the beginning of the ECG at which to start the graph.
	 * @param durationMilliSeconds - The requested length of the returned data subset, in milliseconds.
	 * @param graphWidthPixels - Width of the zoomed graph in pixels(zoom factor*unzoomed width), hence the maximum points needed in the returned VisualizationData.
	 * @param callback - call back handler class.
	 * 	 
	 * @see org.cvrgrid.widgets.node.client.BrokerService#fetchSubjectVisualization(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, int, int)
	 */
	private VisualizationData fetchWFDBdataSegment(String[] sWorkingFiles, int offsetMilliSeconds, int durationMilliSeconds, int graphWidthPixels, boolean skipSamples) {
		
		VisualizationData visualizationData = new VisualizationData();
		double samplesPerPixel = 0, skippedSamples = 0;
		int  skippedSamplesInt = 0, segDurationInSamples = 0;
		float fmilliSecondPerSample=0;
		int samplingRate = 0, segOffset = 0, channels = 0, counts = 0;
		int requestedMaxPoints = 0,availableSamples = 0,availablePoints = 0;
		int maxPoints = 0; // maximum data points that can be returned.
		
		WFDB_wrapper wfdb = null; 
		
		float fRateMsec = 0;
		try {
			String tempFile = util.findHeaderPathName(sWorkingFiles);
			String headerName = ServiceUtils.extractName(tempFile);
			String recordName = headerName.substring(0, headerName.lastIndexOf("."));
			wfdb = new WFDB_wrapper(ServiceUtils.extractPath(tempFile));
			
			channels = util.getSignalCount();
			counts = util.getSamplesPerSignal(); // s/b the same as iSamplesPerSignal
			samplingRate = (int) util.getSampleFrequency(); 
			
			fRateMsec = (float)(util.getSampleFrequency()/1000.0);
			fmilliSecondPerSample =  (float) (1000.0/((float)(samplingRate)));
			
			if (offsetMilliSeconds<0){
				offsetMilliSeconds=0; // cannot read before the beginning of the file.
			}
			segOffset = (int) (offsetMilliSeconds*fRateMsec);  // graph start position in number of samples from the start of record
	
			segDurationInSamples = (int) (fRateMsec*durationMilliSeconds);
			if(segDurationInSamples>graphWidthPixels && skipSamples){
				samplesPerPixel=(double)segDurationInSamples/graphWidthPixels;
				requestedMaxPoints = graphWidthPixels;
			}else{
				samplesPerPixel=1;
				requestedMaxPoints = segDurationInSamples;
			}
			skippedSamples = samplesPerPixel-1;
		    skippedSamplesInt = (int) skippedSamples; // number of samples to skip after each one returned. To adjust for graph resolution.
			availableSamples = counts - segOffset; // total number of remaining samples from this offset.
			availablePoints = availableSamples/(int)samplesPerPixel; // total number of graphable points from this offset.
			// ensure that the copying loop doesn't try to go past the end of the data file.
			if(availablePoints > requestedMaxPoints) {
				maxPoints = requestedMaxPoints;
			} else {  // Requested duration is longer than the remainder after the offset.
				if(segDurationInSamples < counts){ // Requested duration is less than the file contains.
					// move the offset back so the requested amount of samples can be returned.
					segOffset = counts - segDurationInSamples;
					maxPoints = requestedMaxPoints;
					offsetMilliSeconds = (int)(segOffset * fmilliSecondPerSample);
				}else{	// Requested duration is longer than the file contains.
					maxPoints = availablePoints;
				}
			}
			
			wfdb.WFDBtoArray(recordName, channels, maxPoints, channels, offsetMilliSeconds, durationMilliSeconds);
			
		} catch(Exception e1) {
			e1.printStackTrace();
		} 
	
		try {
			visualizationData.setECGDataLength(maxPoints);
			visualizationData.setECGDataLeads(channels);
			visualizationData.setOffset(segOffset);
			visualizationData.setSkippedSamples((int)skippedSamples);
			int msDuration = (int) ((counts*1000)/samplingRate);
			visualizationData.setMsDuration(msDuration);
	
			debugPrintln("+ samplingRate: " + samplingRate); 
			debugPrintln("+ fRateMsec: " + fRateMsec); 
			debugPrintln("+ offsetMilliSeconds: " + offsetMilliSeconds);
			debugPrintln("+ durationMilliSeconds: " + durationMilliSeconds);
			debugPrintln("+ segOffset: " + segOffset); 
			debugPrintln("+ counts: " + counts); 
			debugPrintln("+ durationInSamples: " + segDurationInSamples); 
			debugPrintln("+ graphWidthPixels: " + graphWidthPixels); 
			debugPrintln("+ samplesPerPixel: " + samplesPerPixel); 
			debugPrintln("+ requestedMaxPoints: " + requestedMaxPoints); 
			debugPrintln("+ availableSamples: " + availableSamples); 
			debugPrintln("+ availablePoints: " + availablePoints); 
			debugPrintln("+ VD.maxPoints: " + maxPoints); 
			debugPrintln("+ VD.channels: " + channels); 
			debugPrintln("+ VD.vizOffset: " + segOffset); 
			debugPrintln("+ VD.skippedSamples: " + skippedSamples); 
			debugPrintln("+ VD.msDuration: " + msDuration); 
	
	
			//------------------------------------------------
			// copy a subset of the wfdb data to get the data segment.
			debugPrintln("channels:" + channels);
			debugPrintln("maxPoints:" + maxPoints);
			debugPrintln("counts:" + counts);
			debugPrintln("msDuration:" + msDuration);
			debugPrintln("samplingRate:" + samplingRate);
			debugPrintln("fmilliSecondPerSample:" + fmilliSecondPerSample);
			
			double[][] segmentData = new double[maxPoints][channels+1];
			double skipDecimals = skippedSamples-skippedSamplesInt;
			double skipDecimalsCumulative = 0;
			
			for (int sample =  0; sample < maxPoints;){
				
				segmentData[sample][0] = offsetMilliSeconds + (fmilliSecondPerSample*sample); // time stamp in milliseconds
				for(int ch = 0; ch < channels; ch++) {
					segmentData[sample][ch+1] = wfdb.getData()[ch][sample];
				}
				
				skipDecimalsCumulative +=skipDecimals;
				
				sample = sample + 1 + skippedSamplesInt + (int)skipDecimalsCumulative;
				
				if(((int)skipDecimalsCumulative) > 0){
					skipDecimalsCumulative = skipDecimalsCumulative-(int)skipDecimalsCumulative;
				}
			}
			
			visualizationData.setECGData(segmentData);
			//*******************************************
		} catch (Exception e) {
			e.printStackTrace();
		}
		//*******************************************
	
		return visualizationData;
	}
	
	private void debugPrintln(String text){
		log.info(text);
	}

}
