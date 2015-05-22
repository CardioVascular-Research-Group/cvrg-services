package edu.jhu.cvrg.service.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.jhu.cvrg.annotations.processors.AnnotationsProcessor;
import edu.jhu.cvrg.annotations.processors.MuseAnnotationsProcessor;
import edu.jhu.cvrg.annotations.processors.Philips103AnnotationsProcessor;
import edu.jhu.cvrg.annotations.processors.Philips104AnnotationsProcessor;
import edu.jhu.cvrg.annotations.processors.SchillerAnnotationsProcessor;
import edu.jhu.cvrg.data.dto.AnnotationDTO;
import edu.jhu.cvrg.data.enums.UploadState;
import edu.jhu.cvrg.data.factory.Connection;
import edu.jhu.cvrg.data.factory.ConnectionFactory;
import edu.jhu.cvrg.data.util.DataStorageException;
import edu.jhu.cvrg.service.conversion.vo.MetaContainer;
import edu.jhu.cvrg.service.utilities.ServiceUtils;
import edu.jhu.cvrg.waveform.utility.WebServiceUtility;
import edu.jhu.icm.ecgFormatConverter.ECGformatConverter;
import edu.jhu.icm.ecgFormatConverter.ECGformatConverter.fileFormat;
import edu.jhu.icm.enums.LeadEnum;

public class FileProccessThread extends Thread {

	private String sep = File.separator;
	
	private static Map<String, Map<String, String>> ontologyCache = new HashMap<String, Map<String,String>>();
	
	private MetaContainer metaData;
	private ECGformatConverter.fileFormat inputFormat;
	private ECGformatConverter.fileFormat outputFormat; 
	private String inputPath;
	private long groupId;
	private long folderId;
	private String outputPath;
	private ECGformatConverter conv; 
	private String recordName;
	private Connection dbUtility;
	private long docId;
	private long userId;
	private long[] filesId = null;
	
	long writeTime = 0L;
	
	Logger log = Logger.getLogger(FileProccessThread.class);
	
	public FileProccessThread(MetaContainer metaData, fileFormat inputFormat,	fileFormat outputFormat, String inputPath, 
							  long groupId, long folderId, String outputPath, ECGformatConverter conv, String recordName, long docId, long userId) throws DataStorageException {
		super();
		this.metaData = metaData;
		this.inputFormat = inputFormat;
		this.outputFormat = outputFormat;
		this.inputPath = inputPath;
		this.groupId = groupId;
		this.folderId = folderId;
		this.outputPath = outputPath;
		this.conv = conv;
		this.recordName = recordName;
		this.setName(metaData.getUserID()+ " - " + recordName);
		this.dbUtility = ConnectionFactory.createConnection();
		this.docId = docId;
		this.userId = userId;
	}


	@Override
	public void run() {
		try {
			this.fileProccess();
		} catch (Exception e) {
			try {
				dbUtility.updateUploadStatus(docId, null, null, Boolean.FALSE, e.getMessage());
			} catch (DataStorageException e1) {
				log.error(" " +e1.getMessage());
			}
		}
	}
	
	
	private void fileProccess() throws Exception{

		boolean noConversionErrors = false;
		String message = null;
		
		long writeTime = java.lang.System.currentTimeMillis();
		Boolean done = !(fileFormat.PHILIPS103.equals(inputFormat) || fileFormat.PHILIPS104.equals(inputFormat)  || fileFormat.SCHILLER.equals(inputFormat) || fileFormat.MUSEXML.equals(inputFormat));
		
		try{
			int rowsWritten = conv.write(outputFormat, outputPath, recordName);
			
			if(rowsWritten == 0){
				throw new Exception("Unable to write the WFDB file");
			}
			
			log.info("rowsWritten: " + rowsWritten);
			log.info(" +++++ Conversion completed successfully, results will be transfered.");
			
			tranferFileToLiferay(outputFormat, inputFormat, metaData.getFileName(), inputPath, groupId, folderId, docId, userId);
			
			noConversionErrors = true;
		}catch (Exception e){
			
			this.cleanupError();
			message = e.getMessage();
			
		}finally{
			writeTime = java.lang.System.currentTimeMillis() - writeTime;
			log.info("["+docId+"]The runtime for writing the new file(" + recordName + ") is = " + writeTime + " milliseconds");
			dbUtility.updateUploadStatus(docId, UploadState.WRITE, writeTime, (!noConversionErrors && done) ? Boolean.TRUE : null, message);
		}
		
		
		if(noConversionErrors){
			long  annotationTime = java.lang.System.currentTimeMillis();
			message = null;
			try{
				Map<String, String> nonLeadList = null;
				Map<Integer, Map<String, String>> leadList = null;
				AnnotationsProcessor processor = null;
				
				if(fileFormat.PHILIPS103.equals(inputFormat)) {
					
					org.sierraecg.schema.Restingecgdata ecgData = (org.sierraecg.schema.Restingecgdata) conv.getPhilipsRestingecgdata();
					processor = new Philips103AnnotationsProcessor(ecgData);
				
				}else if(fileFormat.PHILIPS104.equals(inputFormat)) {
					
					org.cvrgrid.philips.jaxb.beans.Restingecgdata ecgData = (org.cvrgrid.philips.jaxb.beans.Restingecgdata) conv.getPhilipsRestingecgdata();
					processor = new Philips104AnnotationsProcessor(ecgData);
					
				}else if(fileFormat.SCHILLER.equals(inputFormat)) {
					                                               
					org.cvrgrid.schiller.jaxb.beans.ComXiriuzSemaXmlSchillerEDISchillerEDI ecgData = (org.cvrgrid.schiller.jaxb.beans.ComXiriuzSemaXmlSchillerEDISchillerEDI) conv.getComXiriuzSemaXmlSchillerEDISchillerEDI();
					processor = new SchillerAnnotationsProcessor(ecgData);
					
				}else if(fileFormat.MUSEXML.equals(inputFormat)) {
					String rawMuseXML = conv.getMuseRawXML();
					if(rawMuseXML != null) {
						processor = new MuseAnnotationsProcessor(rawMuseXML, docId, userId);
					}
				}
				
				
				if(processor != null){
					processor.processAll();
					
					nonLeadList = processor.getGlobalAnnotations();
					leadList = processor.getLeadAnnotations();
					
					Set<AnnotationDTO> annotationsDTO = new HashSet<AnnotationDTO>();
					
					annotationsDTO.addAll(convertLeadAnnotations(leadList, processor, conv.getLeadNames()));
					annotationsDTO.addAll(convertNonLeadAnnotations(nonLeadList, processor));
				
					commitAnnotations(annotationsDTO);
				}
			}catch (Exception e){
				message = e.getMessage();
			}finally{
				annotationTime = java.lang.System.currentTimeMillis() - annotationTime;
				log.info("["+docId+"]The runtime for analyse annotation and entering it into the database is = " + annotationTime + " milliseconds");
				
				dbUtility.updateUploadStatus(docId, UploadState.ANNOTATION, annotationTime, Boolean.TRUE, message);
				
			}
		}
		
		
		
	}
	
	private void cleanupError() throws DataStorageException {
		
		boolean status = ServiceUtils.deleteFolderFromLiferay(groupId, folderId, userId);
		
		if(status){
			dbUtility.deleteAllFilesByDocumentRecordId(docId);
		}
		
	}


	private void tranferFileToLiferay(fileFormat outputFormat, fileFormat inputFormat, String inputFilename, String inputPath, long groupId, long folderId, long docId, long userId) throws Exception{
		
		String outputExt = ".dat";
		if (outputFormat == ECGformatConverter.fileFormat.RDT){ 
			outputExt = ".rdt"; }
		else if (outputFormat == ECGformatConverter.fileFormat.GEMUSE) {
			outputExt = ".txt";
		}else if (outputFormat == ECGformatConverter.fileFormat.HL7) {
			outputExt = ".xml";
		}

		String outputFileName = inputFilename.substring(0, inputFilename.lastIndexOf(".")) + outputExt;

		File orign = new File(inputPath + outputFileName);
		FileInputStream fis = new FileInputStream(orign);
		
		Long fileId = ServiceUtils.sendToLiferay(groupId, folderId, userId, inputPath, outputFileName, orign.length(), fis);
		
		String name = inputFilename.substring(0, inputFilename.lastIndexOf(".")); // file name minus extension.

		File heaFile = new File(inputPath + name + ".hea");
		if (inputFormat != ECGformatConverter.fileFormat.WFDB && heaFile.exists()) {
			orign = new File(inputPath + heaFile.getName().substring(heaFile.getName().lastIndexOf(sep) + 1));
			fis = new FileInputStream(orign);
			
			filesId = new long[2];
			filesId[0] = fileId;
			
			fileId = ServiceUtils.sendToLiferay(groupId, folderId, userId, inputPath, heaFile.getName().substring(heaFile.getName().lastIndexOf(sep) + 1), orign.length(), fis);
			filesId[1] = fileId;
		
		}else{
			filesId = new long[1];
			filesId[0] = fileId;
		}
		
		dbUtility.storeFilesInfo(docId, filesId, null);
			
	}
	
	private boolean commitAnnotations(Set<AnnotationDTO> annotationSet) {
		boolean success = true;

		if(annotationSet != null && annotationSet.size() > 0){
			try {
				success = annotationSet.size() == dbUtility.storeAnnotations(annotationSet);
			} catch (DataStorageException e) {
				log.error("Error on Annotation persistence. " + e.getMessage());
				success = false;
			}
		}
				
		return success;
	}	

	private Set<AnnotationDTO> convertLeadAnnotations(Map<Integer, Map<String, String>> leadAnnotations, AnnotationsProcessor processor, String leadNames) {
		Set<AnnotationDTO> leadAnnotationSet = new HashSet<AnnotationDTO>();
		String[] leads = null;
		if(leadNames != null){
			leads = leadNames.split(",");
		}
		
		for (Integer key : leadAnnotations.keySet()) {
			Map<String, String> lead = leadAnnotations.get(key);
			
			Integer leadIndex = key;
			LeadEnum l = LeadEnum.values()[key];
			
			if(leads != null){
				for (int i = 0; i < leads.length; i++) {
					if(l.name().equals(leads[i])){
						leadIndex = i;
						break;
					}
				}
			}
			
			leadAnnotationSet.addAll(convertAnnotations(lead, leadIndex, processor));
		}
		return leadAnnotationSet;
	}
	
	private Set<AnnotationDTO>  convertNonLeadAnnotations(Map<String, String> allAnnotations, AnnotationsProcessor processor){
		return convertAnnotations(allAnnotations, null, processor);
	}
	
	private Set<AnnotationDTO>  convertAnnotations(Map<String, String> annotationArray, Integer leadIndex, AnnotationsProcessor processor) {
		
		AnnotationDTO ann = null;
		String type = null;
		
		if(leadIndex != null) {
			type = "ANNOTATION";
		}else {
			type = "COMMENT";
		}
		
		Set<AnnotationDTO> annotationSet = new HashSet<AnnotationDTO>();
		if(annotationArray != null && annotationArray.size() > 0){
			for(String name : annotationArray.keySet()) {
				
				
				String termName = name;
				String fullAnnotation = null;
				String bioportalOntology = null;
				String bioportalClassId = null;
				
				String bioportalReference = BioportalReferenceMap.lookup(name);
				
				if(bioportalReference != null){
					
					if(bioportalReference.startsWith("ECGTermsv1")){
						bioportalOntology = AnnotationDTO.ECG_TERMS_ONTOLOGY;
					}else if(bioportalReference.startsWith("ECGOntology")){
						bioportalOntology = AnnotationDTO.ELECTROCARDIOGRAPHY_ONTOLOGY;
					}
					
					if(bioportalOntology != null){
						Map<String, String> saOntDetails = ontologyCache.get(bioportalReference);
						if(saOntDetails == null){
							saOntDetails = WebServiceUtility.lookupOntology(bioportalOntology, bioportalReference, "definition", "prefLabel", "@id");
							ontologyCache.put(bioportalReference, saOntDetails);
						}
						 
						termName = "Not found";
						fullAnnotation = "Not found";
						
						if(saOntDetails != null){
							termName = saOntDetails.get("prefLabel");
							fullAnnotation = saOntDetails.get("definition");
							bioportalClassId = saOntDetails.get("@id");
						}
					}
				}
				
				ann = new AnnotationDTO(Long.valueOf(userId), docId, processor.getName(), type, termName, 
										bioportalOntology, 
										bioportalClassId, null /*will be generate at constructor*/,
									    leadIndex, null/*unit*/, fullAnnotation, annotationArray.get(name), new GregorianCalendar(), 
									    null, null, 
									    null, null);
				 
				annotationSet.add(ann);
			}
		}
				
		return annotationSet;
	}

}
