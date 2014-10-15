package edu.jhu.cvrg.service.dataTransfer;

public class FileResultDTO {

	private Long fileId;
	private String fileName;
	
	public FileResultDTO(Long fileId, String fileName) {
		super();
		this.fileName = fileName;
		this.fileId = fileId;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
}
