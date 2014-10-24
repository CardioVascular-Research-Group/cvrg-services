package edu.jhu.cvrg.service.conversion.vo;
/*
Copyright 2013 Johns Hopkins University Institute for Computational Medicine

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
/**
* @author Mike Shipway
* 
*/
public class MetaContainer {

	private String fileName = ""; // just the file name, no directory
	private String fullFilePath = ""; // the filename with full path
	private int fileSize = 0;
	private int fileFormat = -1;
	private int channels = 1;
	private float sampFrequency = 250;
	private String subjectID = "";
	private String userID = "";
	private int subjectAge = 71;
	private String subjectGender = "Unknown";
	private String recordName = "";
	private String datatype = "";
	private String studyID = "";
	private String fileDate = "";
	private int numberOfPoints = 0;
	private String date = "1/1/2013";
	private String treePath = "";

	public MetaContainer() {
	}

	
	
	public MetaContainer(String fileName, int fileSize, int fileFormat,
			String subjectID, String userID, String recordName,
			String datatype, String studyID, String treePath) {
		super();
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.fileFormat = fileFormat;
		this.subjectID = subjectID;
		this.userID = userID;
		this.recordName = recordName;
		this.datatype = datatype;
		this.studyID = studyID;
		this.treePath = treePath;
	}



	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}
	
	public String getFullFilePath() {
		return fullFilePath;
	}

	public void setFullFilePath(String fullFilePath) {
		this.fullFilePath = fullFilePath;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getFileFormat() {
		return fileFormat;
	}

	public void setFileFormat(int fileFormat) {
		this.fileFormat = fileFormat;
	}

	public int getChannels() {
		return channels;
	}

	public void setChannels(int channels) {
		this.channels = channels;
	}

	public float getSampFrequency() {
		return sampFrequency;
	}

	public void setSampFrequency(float sampFrequency) {
		this.sampFrequency = sampFrequency;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getSubjectAge() {
		return subjectAge;
	}

	public void setSubjectAge(int subjectAge) {
		this.subjectAge = subjectAge;
	}

	public String getSubjectGender() {
		return subjectGender;
	}

	public void setSubjectGender(String subjectGender) {
		this.subjectGender = subjectGender;
	}

	public String getRecordName() {
		return recordName;
	}

	public void setRecordName(String recordName) {
		this.recordName = recordName;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getStudyID() {
		return studyID;
	}

	public void setStudyID(String studyID) {
		this.studyID = studyID;
	}

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
