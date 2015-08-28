package edu.jhu.cvrg.service.conversion;



import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.data.enums.FileExtension;
import edu.jhu.cvrg.data.enums.FileType;
import edu.jhu.cvrg.filestore.enums.EnumFileExtension;
import edu.jhu.cvrg.filestore.model.FSFile;
import edu.jhu.cvrg.service.utilities.ServiceUtils;
import edu.jhu.cvrg.waveform.model.ECGFileMeta;
import edu.jhu.cvrg.waveform.utility.ECGUploadProcessor;
import edu.jhu.icm.enums.DataFileFormat;

public class DataConversion {

	private static final String SUCCESS = "SUCCESS";
	private String sep = File.separator;
	
	Logger log = Logger.getLogger(DataConversion.class);


	/** DataConversion service method to convert a RDT formatted file to a WFDB 16 formatted file.<BR/>
	 * On SUCCESS files of both formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directory, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param param0 - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public org.apache.axiom.om.OMElement rdtToWFDB16(org.apache.axiom.om.OMElement param0) {
		log.info("DataConversion.rdtToWFDB16 service called.");

		return convertFile(
				param0, 
				DataFileFormat.RDT,  
				DataFileFormat.WFDB_16 );
	}

	/** DataConversion service method to convert a WFDB formatted file to an RDT formatted file.<BR/>
	 * On SUCCESS files of both formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param param0 - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public org.apache.axiom.om.OMElement wfdbToRDT(org.apache.axiom.om.OMElement param0) {
		log.info(" #DC# DataConversion.wfdbToRDT service called.");

		return convertFile(
				param0, 
				DataFileFormat.WFDB,  
				DataFileFormat.RDT);

	}

	/** DataConversion service method to convert a GE-Muse formatted text file to both WFDB and RDT formatted files.<BR/>
	 * On SUCCESS files of all three formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param e - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public OMElement geMuse(OMElement e) {
		log.info("DataConversion.geMuse service called.");

		org.apache.axiom.om.OMElement element = convertFile(
				e, 
				DataFileFormat.GEMUSE,  
				DataFileFormat.RDT);
		if (element.getText().equalsIgnoreCase(SUCCESS))
		{
			element = convertFile(
					e, 
					DataFileFormat.GEMUSE,  
					DataFileFormat.WFDB_16);
		}
		return element;
	}

	/** DataConversion service method to convert a GE-Muse formatted text file to both WFDB and RDT formatted files.<BR/>
	 * On SUCCESS files of all three formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param param0 - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public OMElement hL7(OMElement e) {
		log.info("DataConversion.hL7 service called. Starting HL7 -> WFDB.");
		
		return convertFile(e, 
						  DataFileFormat.HL7,  
						  DataFileFormat.WFDB_16);
			
	}

//	/** DataConversion service method to convert a xyFile formatted text file (from digitizer) to both WFDB and RDT formatted files.<BR/>
//	 * On SUCCESS files of all three formats will be in the input directory<BR/>
//	 * Assumes that the input files are in the input directories, e.g.:<BR/>
//	 * <i>parentFolder/userId/public/subjectId/input</i><br>
//	 * @param e - OMElement containing the parameters:<BR/>
//	 * userId, subjectId, filename, isPublic
//	 *  
//	 * @return OMElement containing  "SUCCESS" or "FAILURE"
//	 */
//	public OMElement xyFile(OMElement e) {
//		log.info("DataConversion.xyFile service called.");
//
//		org.apache.axiom.om.OMElement element = convertFile(e, 
//															DataFileFormat.RAW_XY_VAR_SAMPLE,  
//															DataFileFormat.RDT);
//
//		if (element.getText().equalsIgnoreCase(SUCCESS)){
//			element = convertFile(e, 
//								  DataFileFormat.RAW_XY_VAR_SAMPLE,  
//								  DataFileFormat.WFDB_16);
//			 
//		} 
//		
//		return element;
//	}

	/** DataConversion service method to convert a Philips formatted XML file (Base64) to both WFDB and RDT formatted files.<BR/>
	 * On SUCCESS files of all three formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param e - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public OMElement philips103ToWFDB(OMElement e) {
		log.info("DataConversion.philips103ToWFDB service called. Starting Philips -> WFDB.");

		return convertFile(e, 
						   DataFileFormat.PHILIPS103,  
						   DataFileFormat.WFDB_16 );
	}
	
	/** DataConversion service method to convert a Philips formatted XML file (Base64) to both WFDB and RDT formatted files.<BR/>
	 * On SUCCESS files of all three formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param e - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public OMElement philips104ToWFDB(OMElement e) {
		log.info("DataConversion.philips104ToWFDB service called. Starting Philips -> WFDB.");

		return convertFile(e, 
						   DataFileFormat.PHILIPS104,  
						   DataFileFormat.WFDB_16 );
	}
	
	/** DataConversion service method to convert a Philips formatted XML file (Base64) to both WFDB and RDT formatted files.<BR/>
	 * On SUCCESS files of all three formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param e - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public OMElement museXML(OMElement e) {
		log.info("DataConversion.museXML service called. Starting Philips -> WFDB.");

		return convertFile(e, 
						   DataFileFormat.MUSEXML,  
						   DataFileFormat.WFDB_16 );
	}
	
	/** DataConversion service method to convert a SCHILLER formatted XML file (Base64) to both WFDB and RDT formatted files.<BR/>
	 * On SUCCESS files of all three formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param e - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public OMElement SCHILLERToWFDB(OMElement e) {
		debugPrintln("DataConversion.SCHILLER service called. Starting SCHILLER -> WFDB.");
		log.info("DataConversion.SCHILLER service called. Starting SCHILLER -> WFDB.");
		//log.info(" ");

		return convertFile(e, 
						   DataFileFormat.SCHILLER,  
						   DataFileFormat.WFDB_16 );
	}

	/** DataConversion service method to process the files extracted from a zip file by DataStaging.extractZipFile() and then
	 * convert whatever files are found in it to  WFDB and/or RDT formatted files.<BR/>
	 * It does this by calling the DataConversion service methods again.<BR/>
	 * On SUCCESS files of all formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param param0 - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic, ftpHost, ftpUser, ftpPassword, verbose
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	private org.apache.axiom.om.OMElement processUnZipDirOld(org.apache.axiom.om.OMElement param0)
	{
		log.info("Service DataConversion.processUnZipDir() started.");
		Iterator iterator = param0.getChildren();
		String uId = ((OMElement)iterator.next()).getText();
		String sId = ((OMElement)iterator.next()).getText();
		String filename = ((OMElement)iterator.next()).getText();
		String ftpHost = ((OMElement)iterator.next()).getText();
		String ftpUser = ((OMElement)iterator.next()).getText();
		String ftpPassword = ((OMElement)iterator.next()).getText();
		boolean isPublic = Boolean.getBoolean(((OMElement)iterator.next()).getText());
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://www.example.org/nodeDataStagingService/","nodeDataStagingService");
		OMElement nodeConversionStatus = fac.createOMElement("nodeConversionStatus", omNs);

		String successText = null;
		boolean allFilesSuccessful = true;

		String zipFolderName = filename.substring(0,filename.lastIndexOf(sep)+1);
		File zipFolder = new File(zipFolderName);
		File[] zipSubjectDirs = zipFolder.listFiles();
		for (int s=0; s<zipSubjectDirs.length; s++) { 
			File subDir = zipSubjectDirs[s];
			String subDirName = subDir.getName();
			File[] zipFiles = subDir.listFiles();
			for (int i=0; i<zipFiles.length; i++) { 

				String dataFileName = zipFiles[i].getName();
				debugPrintln("###################### s:" + s + " i:" + i + " subDirName:" + subDirName + " dataFileName:" + dataFileName + "###############");
				try {
					
					String ext = dataFileName.substring(dataFileName.lastIndexOf(".")+1).toLowerCase(); // get the extension (in lower case)
					String inputDirectory = zipFolderName + subDirName + sep;
					if (ext.matches("rdt|dat|hea|xml|txt")) {

						String method = "na";
	
						String subjectId = subDirName;

						debugPrintln("datafileName " + dataFileName);
						debugPrintln("inputDirectory " + inputDirectory);
						debugPrintln("subjectID" + subjectId);
						
						if(ext.equalsIgnoreCase("rdt")) method = "rdtToWFDB16";
						if(ext.equalsIgnoreCase("xyz")) method = "wfdbToRDTData";
						if(ext.equalsIgnoreCase("dat")) method = "wfdbToRDTData";
						if(ext.equalsIgnoreCase("hea")) method = "wfdbToRDT";
						
						if(ext.equalsIgnoreCase("txt")) method = "geMuse";
						if(ext.equalsIgnoreCase("csv")) method = "xyFile";
						if(ext.equalsIgnoreCase("nat")) method = "na"; // for Bruce Nearing's 24 hour GE holter files *.nat (3 lead) format.  
						if(ext.equalsIgnoreCase("gtm")) method = "na"; // for Bruce Nearing's 24 hour GE holter files *.GTM (12 lead) format.
						if(ext.equalsIgnoreCase("xml")) method = "hL7";
	
	
						//call convertFile method -- update param0
	
						OMElement param = fac.createOMElement(method, omNs);
						
						OMElement userId = fac.createOMElement("userid", omNs);
						userId.addChild(fac.createOMText(uId));
						param.addChild(userId);
						
						OMElement subId = fac.createOMElement("subjectid", omNs);
						subId.addChild(fac.createOMText(subjectId));
						param.addChild(subId);
						
						OMElement fileName = fac.createOMElement("filename", omNs);
						fileName.addChild(fac.createOMText(inputDirectory + sep + dataFileName));
						param.addChild(fileName);		
						
						OMElement fHost = fac.createOMElement("ftpHost", omNs);
						fHost.addChild(fac.createOMText(ftpHost));
						param.addChild(fHost);
						
						OMElement fUser = fac.createOMElement("ftpUser", omNs);
						fUser.addChild(fac.createOMText(ftpUser));
						param.addChild(fUser);
						
						OMElement fPass = fac.createOMElement("ftpPassword", omNs);
						fPass.addChild(fac.createOMText(ftpPassword));
						param.addChild(fPass);	
	
						OMElement publicPrivateFolder = fac.createOMElement("publicprivatefolder", omNs);
						if(isPublic) {
							publicPrivateFolder.addChild(fac.createOMText("true"));
						} else {
							publicPrivateFolder.addChild(fac.createOMText("false"));
						}
						param.addChild(publicPrivateFolder);
	
						OMElement inputFolder = fac.createOMElement("inputFolder", omNs);
						inputFolder.addChild(fac.createOMText(inputDirectory));
						param.addChild(inputFolder);
	
						OMElement service = fac.createOMElement("service", omNs);
						service.addChild(fac.createOMText("DataConversion"));
						param.addChild(service);
	
	
						if (method.equals("rdtToWFDB16")) {
							successText = rdtToWFDB16(param).getText();
						}
						else if (method.equals("wfdbToRDT")) { 
							debugPrintln(".hea file found, calling wfdbToRDT()");
							successText = wfdbToRDT(param).getText();
						}
						else if (method.equals("wfdbToRDTData")) { 
							debugPrintln("Processing/routing " + zipFolderName  + subDirName + sep + "--" + dataFileName + " from zip file. ");
							debugPrintln("Don't need to copy to FTP, it was done in the Extraction service");
								successText = SUCCESS;
						}
						else if (method.equals("geMuse")) { 
							successText = geMuse(param).getText();
						}
						else if (method.equals("hL7")) { 
							successText = hL7(param).getText();
						}
//						else if (method.equals("xyFile")) { 
//							successText = xyFile(param).getText();
//						}

						debugPrintln("successText: " + successText);
						if (successText.equals(SUCCESS)) {
							param=null;
						}
						else { 
							allFilesSuccessful = false;
							nodeConversionStatus.addChild(fac.createOMText(successText));
						}

					}	
					//Close the input stream
	
				}catch (Exception e){//Catch exception if any
					allFilesSuccessful = false;
					log.error("Error: " + e.getMessage());
					nodeConversionStatus.addChild(fac.createOMText("Error: " + e.toString()));
				}
			}// next (formerly)zipped file in subdir
		} // next subdir
		if (allFilesSuccessful) {
			nodeConversionStatus.addChild(fac.createOMText(SUCCESS));
		}
		return nodeConversionStatus;
	}

	

	/** DataConversion service method to process the files extracted from a zip file by DataStaging.extractZipFile() and then
	 * convert whatever files are found in it to  WFDB and/or RDT formatted files.<BR/>
	 * It does this by calling the DataConversion service methods again.<BR/>
	 * On SUCCESS files of all formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param param0 - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic, ftpHost, ftpUser, ftpPassword, verbose
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	private org.apache.axiom.om.OMElement processUnZipDir(org.apache.axiom.om.OMElement param0)
	{
		log.info("Service DataConversion.processUnZipDir() started.");
		DataFileFormat inputFormat, outputFormat1, outputFormat2;

		Map<String, OMElement> params = ServiceUtils.extractParams(param0);
		
		ECGFileMeta metaData = getMetaData(params);
		
		long groupId = Long.valueOf(params.get("groupId").getText()); 
		long folderId = Long.valueOf(params.get("folderId").getText());
		long companyId = Long.valueOf(params.get("companyId").getText());
		
		long[] filesId = null;
		if(params.get("filesId").getText() != null){
			String[] filesIdStr = params.get("filesId").getText().split(",");
			filesId = new long[filesIdStr.length];
			for (int i = 0; i < filesIdStr.length; i++) {
				filesId[i] = Long.valueOf(filesIdStr[i]);
			}
		}
		
		String inputPath = ServiceUtils.SERVER_TEMP_CONVERSION_FOLDER + sep + metaData.getUserId();
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://www.example.org/nodeDataStagingService/","nodeDataStagingService");
		OMElement nodeConversionStatus = fac.createOMElement("nodeConversionStatus", omNs);
		boolean allFilesSuccessful = true;

		String zipDirName = metaData.getRecordName();
		debugPrintln("Opening unzip directory: " + zipDirName);

		File zipDir = new File(zipDirName);
		File[] zipSubjectDirs = zipDir.listFiles();
		debugPrintln("Found " + zipSubjectDirs.length + " subject sub-directories");
		for (int s=0; s<zipSubjectDirs.length; s++) { 
			File subDir = zipSubjectDirs[s];
			String subDirName = subDir.getName();
			if(subDir.isFile()) {
				debugPrintln(s + ") Unzip directory item is not a sub-directory: " + subDirName);
			}else{
				debugPrintln(s + ") Opening unzip subject sub-directory: " + subDirName);

				File[] zipFiles = subDir.listFiles();
				for (int i=0; i<zipFiles.length; i++) { 
					String dataFileName = zipFiles[i].getName();
					debugPrintln("###################### s:" + s + " i:" + i + " subDirName:" + subDirName + " dataFileName:" + dataFileName + " ###############");
					try {
						
						String ext = dataFileName.substring(dataFileName.lastIndexOf(".")+1).toLowerCase(); // get the extension (in lower case)
						String inputDirectory = zipDirName + subDirName + sep;
						//TODO [VILARDO] PLEASE USE CONSTANTS
						if (ext.matches("rdt|dat|hea|xml|txt")) {
		
							String method = "na";
							String subjectId = subDirName;
							debugPrintln("datafileName: " + dataFileName);
							debugPrintln("inputDirectory: " + inputDirectory);
							debugPrintln("subjectID: " + subjectId);
							//TODO [VILARDO] PLEASE USE CONSTANTS
							if(ext.equalsIgnoreCase("rdt")) method = "rdtToWFDB16";
							if(ext.equalsIgnoreCase("xyz")) method = "wfdbToRDTData";
							if(ext.equalsIgnoreCase("dat")) method = "wfdbToRDTData";
							if(ext.equalsIgnoreCase("hea")) method = "wfdbToRDT";
							//TODO [VILARDO] PLEASE USE CONSTANTS
							if(ext.equalsIgnoreCase("txt")) method = "geMuse";
							if(ext.equalsIgnoreCase("csv")) method = "xyFile";
							if(ext.equalsIgnoreCase("nat")) method = "na"; // for Bruce Nearing's 24 hour GE holter files *.nat (3 lead) format.  
							if(ext.equalsIgnoreCase("gtm")) method = "na"; // for Bruce Nearing's 24 hour GE holter files *.GTM (12 lead) format.
							if(ext.equalsIgnoreCase("xml")) method = "hL7";
		
							String wfdbStatus="";
	
							// call covertFileCommon with the appropriate input and output formats.
							if (method.equals("rdtToWFDB16")) {
								log.info("rdtToWFDB16 called.");
	 
								inputFormat = DataFileFormat.RDT;
								outputFormat1 = DataFileFormat.WFDB_16;
								
								wfdbStatus = convertFileCommon(metaData, 
																inputFormat, 
																outputFormat1,
																inputPath, groupId, folderId, companyId, filesId);
							}
							else if (method.equals("wfdbToRDT")) { 
								log.info(".hea file found, calling wfdbToRDT()");
	
								inputFormat = DataFileFormat.WFDB;
								outputFormat1 = DataFileFormat.RDT;
								
								wfdbStatus = convertFileCommon(metaData, 
										inputFormat, 
										outputFormat1,
										inputPath, groupId, folderId, companyId, filesId);
							}
							else if (method.equals("wfdbToRDTData")) { 
								debugPrintln("Processing/routing " + zipDirName  + subDirName + sep + "--" + dataFileName + " from zip file. ");
								wfdbStatus = SUCCESS;
							}
							else if (method.equals("geMuse")) { 
								log.info("geMuse called.");
	
								inputFormat = DataFileFormat.GEMUSE;
								outputFormat1 = DataFileFormat.RDT;
								outputFormat2 = DataFileFormat.WFDB_16;
								
								wfdbStatus = convertFileCommon(metaData, 
										inputFormat, 
										outputFormat1,
										inputPath, groupId, folderId, companyId, filesId);
								
								if (wfdbStatus.equalsIgnoreCase(SUCCESS))
								{
									log.info("geMuse to RDT suceeded, now calling geMuse to WFDB_16.");
									
									wfdbStatus = convertFileCommon(metaData, 
											inputFormat, 
											outputFormat2,
											inputPath, groupId, folderId, companyId, filesId);
								}
	
							}
							else if (method.equals("hL7")) { 
								log.info("hL7 called.");
	
								inputFormat = DataFileFormat.HL7;
								outputFormat1 = DataFileFormat.RDT;
								outputFormat2 = DataFileFormat.WFDB_16;
								
								wfdbStatus = convertFileCommon(metaData, 
										inputFormat, 
										outputFormat1,
										inputPath, groupId, folderId, companyId, filesId);
								
								if (wfdbStatus.equalsIgnoreCase(SUCCESS))
								{
									log.info("HL7 to RDT suceeded, now calling HL7 to WFDB_16.");
									
									wfdbStatus = convertFileCommon(metaData, 
											inputFormat, 
											outputFormat2,
											inputPath, groupId, folderId, companyId, filesId);
								}
//							}
//							else if (method.equals("xyFile")) { 
//								log.info("xyFile called.");
//	
//								inputFormat = DataFileFormat.RAW_XY_VAR_SAMPLE;
//								outputFormat1 = DataFileFormat.RDT;
//								outputFormat2 = DataFileFormat.WFDB_16;
//								
//								wfdbStatus = convertFileCommon(metaData, 
//										inputFormat, 
//										outputFormat1,
//										inputPath, groupId, folderId, companyId, filesId);
//								
//								if (wfdbStatus.equalsIgnoreCase(SUCCESS))
//								{
//									log.info("RAW_XY_VAR_SAMPLE to RDT suceeded, now calling RAW_XY_VAR_SAMPLE to WFDB_16.");
//									
//									wfdbStatus = convertFileCommon(metaData, 
//											inputFormat, 
//											outputFormat2,
//											inputPath, groupId, folderId, companyId, filesId);
//								}
							}else if (method.equals("na")) { 
								wfdbStatus = SUCCESS;
							}
	
							debugPrintln("wfdbStatus: " + wfdbStatus);
							if (wfdbStatus.equals(SUCCESS)) {
							}
							else { 
								allFilesSuccessful = false;
								nodeConversionStatus.addChild(fac.createOMText(wfdbStatus));
							}
						}	
					}catch (Exception e){//Catch exception if any
						allFilesSuccessful = false;
						log.error("Error: " + e.toString());
						nodeConversionStatus.addChild(fac.createOMText("Error: " + e.toString()));
					}
				}// next (formerly)zipped file in subdir
			}
		} // next subdir
		if (allFilesSuccessful) {
			nodeConversionStatus.addChild(fac.createOMText(SUCCESS));
			debugPrintln("Deleting zip Dir: " + zipDir.getAbsolutePath());
			zipDir.delete();
		}
//		zipFolder.delete();
		return nodeConversionStatus;
	}
	
	public OMElement extractData(OMElement e) {
		log.info("DataConversion.hL7 service called. Starting HL7 -> WFDB.");
		
		debugPrintln("DataConversion.convertFile()");
		
		Map<String, OMElement> params = ServiceUtils.extractParams(e);
		
		DataFileFormat inputFormat = DataFileFormat.values()[Integer.parseInt(params.get("inputFormat").getText())];
		DataFileFormat outputFormat = DataFileFormat.values()[Integer.parseInt(params.get("outputFormat").getText())];
		
		return convertFile(e, inputFormat, outputFormat);
	}

	/** Common method for the format converter methods.
	 * On SUCCESS files of both formats will be in the input directory<BR/>
	 * Assumes that the input files are in the local ftp directory, e.g.: /export/icmv058/cvrgftp <BR/>
	 * 
	 * @param param0 - original OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 * @param inputFormat - format of the ecg file.
	 * @param outputFormat - format to convert to.
	 * @return OMElement containing  "SUCCESS" or some other string
	 */
	private org.apache.axiom.om.OMElement convertFile(	org.apache.axiom.om.OMElement param0, DataFileFormat inputFormat, DataFileFormat outputFormat) {
		debugPrintln("DataConversion.convertFile()");
		
		Map<String, OMElement> params = ServiceUtils.extractParams(param0);
		
		ECGFileMeta metaData = getMetaData(params);
		
		long groupId = Long.valueOf(params.get("groupId").getText()); 
		long folderId = Long.valueOf(params.get("folderId").getText());
		long companyId = Long.valueOf(params.get("companyId").getText());
		
		long[] filesId = null;
		if(params.get("filesId").getText() != null){
			String[] filesIdStr = params.get("filesId").getText().split(",");
			filesId = new long[filesIdStr.length];
			for (int i = 0; i < filesIdStr.length; i++) {
				filesId[i] = Long.valueOf(filesIdStr[i]);
			}
		}
		
		String inputPath = ServiceUtils.SERVER_TEMP_CONVERSION_FOLDER + sep + metaData.getUserId() + sep;
		
		if(inputFormat.equals(DataFileFormat.WFDB)){
			metaData.setFile(ServiceUtils.createFSFile(params, "headerFile", metaData.getRecordName(), FileExtension.HEA));
			metaData.addAuxFile(FileExtension.DAT, ServiceUtils.createFSFile(params, "contentFile", metaData.getRecordName(), FileExtension.DAT));
			metaData.addAuxFile(FileExtension.XYZ, ServiceUtils.createFSFile(params, "extraFile", metaData.getRecordName(), FileExtension.XYZ));
		}else{
			metaData.setFile(ServiceUtils.createFSFile(params, "contentFile", metaData.getRecordName(), metaData.getFileType().getExtension()[0]));
		}
		
		String returnString = convertFileCommon(metaData, inputFormat, outputFormat, inputPath, groupId, folderId, companyId, filesId);
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://www.example.org/nodeConversionService/", "nodeConversionService");
		OMElement e = fac.createOMElement("nodeConversionStatus", omNs);
		
		try{
			long docId = Long.parseLong(returnString);
			ServiceUtils.addOMEChild("documentId", String.valueOf(docId), e, fac, omNs);
		}catch(NumberFormatException ex){
			debugPrintln("Error:" + ex.getLocalizedMessage());
			
			ServiceUtils.addOMEChild("errorMessage", returnString, e, fac, omNs);	
		}
		
		debugPrintln("DataConversion.convertFile() finished.");
		return e;
	}

	private ECGFileMeta getMetaData(Map<String, OMElement> params) {
		
		String userId = params.get("userId").getText();
		String documentId = params.get("documentId").getText();
		String recordName = params.get("recordName").getText();
		String inputFormat = params.get("inputFormat").getText();
		String subjectId = params.get("subjectId").getText();
		String studyID = params.get("studyID").getText();
		String datatype = params.get("datatype").getText();
		
		ECGFileMeta ecgFileMeta = new ECGFileMeta(subjectId, recordName, datatype, studyID, Long.valueOf(userId));
		
		ecgFileMeta.setDocumentId(Long.valueOf(documentId));
		ecgFileMeta.setFileType(FileType.values()[Integer.parseInt(inputFormat)]);
		
		return ecgFileMeta; 
	}
	
	private String convertFileCommon(ECGFileMeta metaData,
									 DataFileFormat inputFormat,
									 DataFileFormat outputFormat, final String inputPath, long groupId, long folderId, long companyId, long[] filesId){
		
		String returnMessage = null;
		
		debugPrintln("++++ Starting: convertFileCommon()");
		debugPrintln("++++           userId:" + metaData.getUserId());  
		debugPrintln("++++           subjectId:" + metaData.getSubjectID());  
		debugPrintln("++++           inputFilename:" + metaData.getFile().getName());
		debugPrintln("++++           inputPath: " + inputPath);
		
		// check that both files are available for WFDB conversion.
		if(inputFormat == DataFileFormat.WFDB){
			returnMessage = checkWFDBcompleteness(metaData);
			if (returnMessage != null && returnMessage.length()>0){
				debugPrintln("checkWFDBcompleteness() returned: " + returnMessage);
				return returnMessage;
			}
			debugPrintln("checkWFDBcompleteness() indicates WFDB is complete.");
		}

		debugPrintln(metaData.getFile().getName() + " this is the file sent to the converter ECGformatConverter()");
		
		try{
		
			ECGUploadProcessor processor = new ECGUploadProcessor();
			processor.execute(metaData);
			returnMessage = String.valueOf(metaData.getDocumentId());
		
		} catch (Exception ex) {
			returnMessage = "Error: " + ex.toString();
			ex.printStackTrace();
			return returnMessage;
		}
		
		return returnMessage;
	}

	/** Checks whether the WFDB upload has all the needed files in the same directory.
	 *	It currently expects the files to all have the same prefix.  This is wrong and should be fixed.
	 * 
	 * @param inputPath - full local path the WFDB files where uploaded to.
	 * @param inputFilename - a single file, either .dat, .hea or .xyz 
	 * @return - reminder message if one of the .dat, .hea or afiles is missing.
	 */
	//TODO [VILARDO] Improve it, to open the HEA file and check the need of the XYZ file.
	private String checkWFDBcompleteness(ECGFileMeta ecgFileMeta){
		String status="";
		
		debugPrintln("Checking that both .dat and .hea files are available for WFDB conversion.");
		
		FSFile datFile = ecgFileMeta.getAuxiliarFiles().get(EnumFileExtension.DAT);
		FSFile heaFile = ecgFileMeta.getFile();
		
		if (heaFile != null) {
			if (datFile == null){
				status = "Reminder: WFDB format requires that you also upload the .dat file before the ECG gadget can analyze it.";
			}
		}
		
		return status;
	}
	
	/** debug utility method, only println (with line end) if verbose is true.
	 * 
	 * @param out - String to be printed
	 */
	private void debugPrintln(String out){
		log.info(" #DC3# " + out);
	}
	
	
}
