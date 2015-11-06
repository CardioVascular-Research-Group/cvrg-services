package edu.jhu.cvrg.service.dataTransfer;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.axiom.om.OMElement;

import edu.jhu.cvrg.service.utilities.TransferSvcUtils;
import edu.jhu.cvrg.waveform.utility.ServiceUtils;


public class DataTransfer {

	private TransferSvcUtils utils = new TransferSvcUtils();

	/** For testing of service.
	 * @return version number.
	 * @throws Exception
	 */
	public String getVersion() throws Exception{
		utils.debugPrintln("Running DataTransfer/getVersion() 1.0");
		return "<Version> 1.0</Version>";
	}

	public org.apache.axiom.om.OMElement receiveAnalysisTempFiles(org.apache.axiom.om.OMElement e) throws Exception {
		utils.debugPrintln("Running DataTransferService.DataTransfer.receiveAnalysisTempFiles()");

		boolean status = utils.storeLocalFiles(e);
		
		return utils.buildAnalysisReceiveReturn(status);
	}
	
	public org.apache.axiom.om.OMElement sendAnalysisResultFiles(org.apache.axiom.om.OMElement e) throws Exception {
		utils.debugPrintln("Running DataTransferService.DataTransfer.sendAnalysisResultFiles()");

		Map<String, OMElement> params = ServiceUtils.extractParams(e);
		String jobId = params.get("jobID").getText();
		long userId = Long.valueOf(params.get("userID").getText());
		long groupId = Long.valueOf(params.get("groupID").getText());
		long folderId = Long.valueOf(params.get("folderID").getText());
		
		StringTokenizer strToken = new StringTokenizer(params.get("resultFileNames").getText(), "^");
		String[] fileNames = new String[strToken.countTokens()];
		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = strToken.nextToken();
		}
		
		Set<FileResultDTO> status = utils.sendResultFiles(fileNames, groupId, folderId, jobId, userId);
		
		return utils.buildAnalysisResultReturn(status);
	}
	
	
	
}
