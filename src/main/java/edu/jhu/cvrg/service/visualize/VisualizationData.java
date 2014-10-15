package edu.jhu.cvrg.service.visualize;

import java.io.Serializable;

/** The raw ECG samples, in millivolts, for displaying the ecg chart in graphical form.
 * 
 * @author M.Shipway, W.Gertin
 *
 */
public class VisualizationData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/** Id of the subject this data refers to **/
	String subjectID;
	
	/** Count of samples in RDT data. (rows)*/
	int ecgDataLength;
	
	/** Count of leads in RDT data. (columns)*/
	int ecgDataLeads = 3;

	/** The raw ECG samples **/
	double[][] ecgData;
	
	/** Offset, in samples, from beginning of the ecg data set (SubjectData). Zero offset means first reading in data set.**/
	int offset;
	
	/**Number of samples to skip after each one returned. To adjust for graph resolution.**/
	int skippedSamples;
	
	/** duration of the ECG in milliseconds. **/
	int msDuration;
	
//****************************************	
	/** @return the Id of the subject this data refers to. 	 */
	public String getSubjectID() {
		return subjectID;
	}
	/** Set the Id of the subject this data refers to.
	 * @param subjectID the subjectID to set */
	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}
	
	/**Get number of samples to skip after each one returned. To adjust for graph resolution.**/
	public int getSkippedSamples() {
		return skippedSamples;
	}
	/**Set number of samples to skip after each one returned. To adjust for graph resolution.**/
	public void setSkippedSamples(int skippedSamples) {
		this.skippedSamples = skippedSamples;
	}

	/** Get offset, in samples, from beginning of the ecg data set (SubjectData).**/
	public int getOffset() { return offset; }
	/** Set offset, in samples, from beginning of the ecg data set (SubjectData). **/
	public void setOffset(int offset) { this.offset = offset; }
	
	/** Get the count of leads in RDT data. (columns)*/
	public int getECGDataLeads() {return ecgDataLeads; }
	/** Set the count of leads in RDT data. (columns)*/
	public void setECGDataLeads(int ecgDataLeads) { this.ecgDataLeads = ecgDataLeads; }

	/** Get the count of samples in RDT data.(rows)*/
	public int getECGDataLength() {return ecgDataLength;}
	/** Get the count of samples in RDT data.(rows)*/
	public void setECGDataLength(int ecgDataLength) { this.ecgDataLength = ecgDataLength; }

	/** Get the duration of the ECG in milliseconds. 
	 * @return the msDuration
	 */
	public int getMsDuration() {
		return msDuration;
	}
	/** Set the duration of the ECG in milliseconds. 
	 * @param msDuration the msDuration to set
	 */
	public void setMsDuration(int msDuration) {
		this.msDuration = msDuration;
	}

	/** ECG samples in millivolts, one column per lead, one row per sample displayed. */
	public double[][] getECGData() {return ecgData;}
	public void setECGData(double[][] ecgData) {
		this.ecgData = ecgData;
	}

}