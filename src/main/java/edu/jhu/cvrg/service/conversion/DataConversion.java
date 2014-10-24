package edu.jhu.cvrg.service.conversion;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.log4j.Logger;

import edu.jhu.cvrg.data.factory.Connection;
import edu.jhu.cvrg.data.factory.ConnectionFactory;
import edu.jhu.cvrg.service.conversion.vo.MetaContainer;
import edu.jhu.cvrg.service.utilities.ServiceUtils;
import edu.jhu.icm.ecgFormatConverter.ECGformatConverter;

public class DataConversion {

	private static final String SUCCESS = "SUCCESS";
	private String sep = File.separator;
	
	private boolean verbose = false;
	
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
				ECGformatConverter.fileFormat.RDT,  
				ECGformatConverter.fileFormat.WFDB_16 );
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
				ECGformatConverter.fileFormat.WFDB,  
				ECGformatConverter.fileFormat.RDT);

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
				ECGformatConverter.fileFormat.GEMUSE,  
				ECGformatConverter.fileFormat.RDT);
		if (element.getText().equalsIgnoreCase(SUCCESS))
		{
			element = convertFile(
					e, 
					ECGformatConverter.fileFormat.GEMUSE,  
					ECGformatConverter.fileFormat.WFDB_16);
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
	private OMElement hL7(OMElement e) {
		log.info("DataConversion.hL7 service called. Starting HL7 -> RDT.");
		org.apache.axiom.om.OMElement element = convertFile(e, 
															ECGformatConverter.fileFormat.HL7,  
															ECGformatConverter.fileFormat.RDT);
		if (element.getText().equalsIgnoreCase(SUCCESS)) {
			
			log.info("HL7 Conversion to RDT succeeded.");
			element = convertFile(e, 
								  ECGformatConverter.fileFormat.HL7,  
								  ECGformatConverter.fileFormat.WFDB_16);
			if (element.getText().equalsIgnoreCase(SUCCESS)){
				log.info("HL7 Conversion to WFDB16 succeeded.");
			}
		}
		return element;
	}

	/** DataConversion service method to convert a xyFile formatted text file (from digitizer) to both WFDB and RDT formatted files.<BR/>
	 * On SUCCESS files of all three formats will be in the input directory<BR/>
	 * Assumes that the input files are in the input directories, e.g.:<BR/>
	 * <i>parentFolder/userId/public/subjectId/input</i><br>
	 * @param e - OMElement containing the parameters:<BR/>
	 * userId, subjectId, filename, isPublic
	 *  
	 * @return OMElement containing  "SUCCESS" or "FAILURE"
	 */
	public OMElement xyFile(OMElement e) {
		log.info("DataConversion.xyFile service called.");

		org.apache.axiom.om.OMElement element = convertFile(e, 
															ECGformatConverter.fileFormat.RAW_XY_VAR_SAMPLE,  
															ECGformatConverter.fileFormat.RDT);

		if (element.getText().equalsIgnoreCase(SUCCESS)){
			element = convertFile(e, 
								  ECGformatConverter.fileFormat.RAW_XY_VAR_SAMPLE,  
								  ECGformatConverter.fileFormat.WFDB_16);
			 
		} 
		
		return element;
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
	public OMElement philips103ToWFDB(OMElement e) {
		log.info("DataConversion.philips103ToWFDB service called. Starting Philips -> WFDB.");

		return convertFile(e, 
						   ECGformatConverter.fileFormat.PHILIPS103,  
						   ECGformatConverter.fileFormat.WFDB_16 );
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
						   ECGformatConverter.fileFormat.PHILIPS104,  
						   ECGformatConverter.fileFormat.WFDB_16 );
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
						   ECGformatConverter.fileFormat.MUSEXML,  
						   ECGformatConverter.fileFormat.WFDB_16 );
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
		System.out.println("DataConversion.SCHILLER service called. Starting SCHILLER -> WFDB.");
		log.info("DataConversion.SCHILLER service called. Starting SCHILLER -> WFDB.");
		//log.info(" ");

		return convertFile(e, 
						   ECGformatConverter.fileFormat.SCHILLER,  
						   ECGformatConverter.fileFormat.WFDB_16 );
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
		verbose = Boolean.getBoolean(((OMElement)iterator.next()).getText());
		verbose = true;
		
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
						else if (method.equals("xyFile")) { 
							successText = xyFile(param).getText();
						}

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
		ECGformatConverter.fileFormat inputFormat, outputFormat1, outputFormat2;

		Map<String, OMElement> params = ServiceUtils.extractParams(param0);
		
		MetaContainer metaData = getMetaData(params);
		
		verbose = Boolean.getBoolean(params.get("verbose").getText());
		
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
		
		String inputPath = ServiceUtils.SERVER_TEMP_CONVERSION_FOLDER + sep + metaData.getUserID();
		
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMNamespace omNs = fac.createOMNamespace("http://www.example.org/nodeDataStagingService/","nodeDataStagingService");
		OMElement nodeConversionStatus = fac.createOMElement("nodeConversionStatus", omNs);
		boolean allFilesSuccessful = true;

		String zipDirName = metaData.getFileName().substring(0,metaData.getFileName().lastIndexOf(sep)+1);
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
	 
								inputFormat = ECGformatConverter.fileFormat.RDT;
								outputFormat1 = ECGformatConverter.fileFormat.WFDB_16;
								
								wfdbStatus = convertFileCommon(metaData, 
																inputFormat, 
																outputFormat1,
																inputPath, groupId, folderId, companyId, filesId);
							}
							else if (method.equals("wfdbToRDT")) { 
								log.info(".hea file found, calling wfdbToRDT()");
	
								inputFormat = ECGformatConverter.fileFormat.WFDB;
								outputFormat1 = ECGformatConverter.fileFormat.RDT;
								
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
	
								inputFormat = ECGformatConverter.fileFormat.GEMUSE;
								outputFormat1 = ECGformatConverter.fileFormat.RDT;
								outputFormat2 = ECGformatConverter.fileFormat.WFDB_16;
								
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
	
								inputFormat = ECGformatConverter.fileFormat.HL7;
								outputFormat1 = ECGformatConverter.fileFormat.RDT;
								outputFormat2 = ECGformatConverter.fileFormat.WFDB_16;
								
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
							}
							else if (method.equals("xyFile")) { 
								log.info("xyFile called.");
	
								inputFormat = ECGformatConverter.fileFormat.RAW_XY_VAR_SAMPLE;
								outputFormat1 = ECGformatConverter.fileFormat.RDT;
								outputFormat2 = ECGformatConverter.fileFormat.WFDB_16;
								
								wfdbStatus = convertFileCommon(metaData, 
										inputFormat, 
										outputFormat1,
										inputPath, groupId, folderId, companyId, filesId);
								
								if (wfdbStatus.equalsIgnoreCase(SUCCESS))
								{
									log.info("RAW_XY_VAR_SAMPLE to RDT suceeded, now calling RAW_XY_VAR_SAMPLE to WFDB_16.");
									
									wfdbStatus = convertFileCommon(metaData, 
											inputFormat, 
											outputFormat2,
											inputPath, groupId, folderId, companyId, filesId);
								}
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
	private org.apache.axiom.om.OMElement convertFile(	org.apache.axiom.om.OMElement param0,	ECGformatConverter.fileFormat inputFormat, ECGformatConverter.fileFormat outputFormat) {
		System.out.println("DataConversion.convertFile()");
		
		Map<String, OMElement> params = ServiceUtils.extractParams(param0);
		
		MetaContainer metaData = getMetaData(params);
		
		verbose = Boolean.getBoolean(params.get("verbose").getText());
		System.out.println(" DataConversion, verbose:" + verbose);
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
		
		
		String inputPath = ServiceUtils.SERVER_TEMP_CONVERSION_FOLDER + sep + metaData.getUserID() + sep;
		String headerFileName = null;
		
		if(inputFormat.equals(ECGformatConverter.fileFormat.WFDB)){
			metaData.setFileName(metaData.getRecordName()+".dat");
			ServiceUtils.createTempLocalFile(params,"contentFile", inputPath, metaData.getFileName());
			
			headerFileName = metaData.getRecordName() + ".hea";
			ServiceUtils.createTempLocalFile(params,"headerFile", inputPath, headerFileName);	
		}else{
			ServiceUtils.createTempLocalFile(params,"contentFile", inputPath, metaData.getFileName());	
		}
		
		log.info("passed verbose: " + verbose);
		
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
		
		ServiceUtils.deleteFile(inputPath, metaData.getFileName());
		if(inputFormat.equals(ECGformatConverter.fileFormat.WFDB)){
			ServiceUtils.deleteFile(inputPath, headerFileName);	
		}
		
		debugPrintln("DataConversion.convertFile() finished.");
		return e;
	}

	private MetaContainer getMetaData(Map<String, OMElement> params) {
		
		String userId = params.get("userid").getText();
		String subjectId = params.get("subjectid").getText();
		String inputFilename = params.get("filename").getText();
		String studyID = params.get("studyID").getText();
		String datatype = params.get("datatype").getText();
		String treePath = params.get("treePath").getText();
		String recordName = params.get("recordName").getText();
		int fileSize = Integer.valueOf(params.get("fileSize").getText());
		int fileFormat = Integer.valueOf(params.get("fileFormat").getText());
		
		MetaContainer metaData = new MetaContainer(inputFilename, fileSize, fileFormat, subjectId, userId, recordName, datatype, studyID, treePath);
		
		metaData.setFullFilePath(metaData.getTreePath()+sep);
		
		SimpleDateFormat newDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		metaData.setFileDate(newDateFormat.format(new Date()));
		
		return metaData; 
	}

	private String convertFileCommon(MetaContainer metaData,
									 ECGformatConverter.fileFormat inputFormat,
									 ECGformatConverter.fileFormat outputFormat, final String inputPath, long groupId, long folderId, long companyId, long[] filesId){
		
		String errorMessage = null;

		
		
		debugPrintln("++++ Starting: convertFileCommon()");
		debugPrintln("++++           userId:" + metaData.getUserID());  
		debugPrintln("++++           subjectId:" + metaData.getSubjectID());  
		debugPrintln("++++           inputFilename:" + metaData.getFileName());
		
		String outputPath;
		debugPrintln("++++           inputPath: " + inputPath);
		outputPath = inputPath;

		int signalsRequested = 0; // zero means request all signals, only used when reading WFDB format.
		
		// check that both files are available for WFDB conversion.
		if(inputFormat == ECGformatConverter.fileFormat.WFDB){
			errorMessage = checkWFDBcompleteness(inputPath, metaData.getUserID(), metaData.getFileName());
			if (errorMessage != null && errorMessage.length()>0){
				debugPrintln("checkWFDBcompleteness() returned: " + errorMessage);
				return errorMessage;
			}
			debugPrintln("checkWFDBcompleteness() indicates WFDB is complete.");
		}

		ECGformatConverter conv = new ECGformatConverter();
		debugPrintln(metaData.getFileName() + " this is the file sent to the converter ECGformatConverter()");
		String recordName = metaData.getFileName().substring(0, metaData.getFileName().lastIndexOf(".")); // trim off the extension
		
		long docId = 0;
		
		try{
			
			boolean ret = conv.read(inputFormat, metaData.getFileName(), signalsRequested, inputPath, recordName);
			debugPrintln("File read returned: " + ret);
			
			if(!ret){
				errorMessage = "Error: On File read.";
				return  errorMessage;
			}
			
			metaData.setSampFrequency(conv.getSamplingRate());
			//system.out.println(conv.getSamplingRate() + " this is the sampling rate returned.");
			metaData.setChannels(conv.getChannelCount());
			metaData.setNumberOfPoints(conv.getNumberOfPoints());

			
			Connection dbUtility = ConnectionFactory.createConnection();
			
			docId = dbUtility.storeDocument(Long.valueOf(metaData.getUserID()), metaData.getFileName(), metaData.getSubjectID(), metaData.getFileFormat(), 
											Double.valueOf(metaData.getSampFrequency()), metaData.getTreePath(), metaData.getChannels(), metaData.getNumberOfPoints(),
											new GregorianCalendar(), metaData.getSubjectAge(), metaData.getSubjectGender(), null, conv.getAduGain(), filesId);
			
			errorMessage = String.valueOf(docId);
			
			FileProccessThread newThread = new FileProccessThread(metaData, inputFormat, outputFormat, inputPath, groupId, folderId, outputPath, conv, recordName, docId, Long.valueOf(metaData.getUserID()));
			
			newThread.start();
			
		} catch (Exception ex) {
			errorMessage = "Error: " + ex.toString();
			ex.printStackTrace();
			return errorMessage;
		}
		
		return errorMessage;
	}

	/** Checks whether the WFDB upload has all the needed files in the same directory.
	 *	It currently expects the files to all have the same prefix.  This is wrong and should be fixed.
	 * 
	 * @param inputPath - full local path the WFDB files where uploaded to.
	 * @param inputFilename - a single file, either .dat, .hea or .xyz 
	 * @return - reminder message if one of the .dat, .hea or afiles is missing.
	 */
	private String checkWFDBcompleteness(String inputPath, String userId, String inputFilename){
		String status="";
		
		debugPrintln("Checking that both .dat and .hea files are available for WFDB conversion in the path:" + inputPath);
		
		String ext = inputFilename.substring(inputFilename.lastIndexOf(".")+1).toLowerCase(); // get the extension (in lower case)
		String name = inputFilename.substring(0, inputFilename.lastIndexOf(".")); // file name minus extension.
//		debugPrintln(inputPath + name + ".dat");
//		debugPrintln(inputPath + name + ".hea");

		File datFile = new File(inputPath + name + ".dat");
		File heaFile = new File(inputPath + name + ".hea");
		if (ext.equalsIgnoreCase("hea")) {
			if (!datFile.exists()){
				status = "Reminder: WFDB format requires that you also upload a .xyz or .dat file before the ECG gadget can analyze it.";
			}
			// TODO: need to adjust the name of the .dat file within .hea file!!!
			debugPrintln("bonjour");
		}
		else if ((ext.equalsIgnoreCase("dat")) || (ext.equalsIgnoreCase("xyz"))) {

			if (!heaFile.exists()){
				status = "Reminder: WFDB format requires that you also upload a .hea file before the ECG gadget can analyze it.";
			}
		}
		
		return status;
	}
	
	/** debug utility method, only println (with line end) if verbose is true.
	 * 
	 * @param out - String to be printed
	 */
	private void debugPrintln(String out){
//		System.out.println(" #DC3# " + out);
		log.info(" #DC3# " + out);
	}
	
	
}
