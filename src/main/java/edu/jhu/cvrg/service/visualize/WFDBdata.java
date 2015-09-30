package edu.jhu.cvrg.service.visualize;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.service.utilities.DataServiceUtils;

/** A collection of methods for building a generic Web Service to wrap around an arbitrary analysis algorithm..
 * 
 * @author Michael Shipway - 3/29/2012
 *
 */
public class WFDBdata {
	
	private static Logger log = Logger.getLogger(WFDBdata.class);
	
	/** For testing of service.
	 * @return version and usage text.
	 * @throws Exception
	 */
	public String getVersion() throws Exception{
		
		return "Version: 0.1.0 (03/26/2013)";
	}

	/** FTPs the WFDB files to the execute directory, extracts the segment requested, cleans up the temp files, then returns the data segment.
	 * Assumes that the data fetching will return fast enough to avoid the connection timeouts.
	 * 
	 * @param param0 - contains the input parameters coded as XML.
	 * @return - Result files names coded as XML.
	 * @throws Exception 
	 */
	public org.apache.axiom.om.OMElement fetchWFDBdataSegmentType2(org.apache.axiom.om.OMElement param0) throws Exception {
		
		debugPrintln("fetchWFDBdataSegmentType2() started.");
		
		DataServiceUtils util = new DataServiceUtils();
		util.parseInputParametersType2(param0);

		WFDBExecute execute = new WFDBExecute(util);
		OMElement omeWFDBdataReturn = execute.collectWFDBdataSegment(); 

		return omeWFDBdataReturn;	
	}
	
	private void debugPrintln(String text){
		log.info("+ waveform-support-Service + " + text);
	}
	
}
