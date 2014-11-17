package edu.jhu.cvrg.service.conversion;

import java.util.HashMap;
import java.util.Map;

public class BioportalReferenceMap {

	
	private static Map<String, String> bioportalMap = new HashMap<String, String>();
	
	static {
		bioportalMap.put("Atrial_Flutter_Fibrillation_Autocorrelation_Peak", 		null);
		bioportalMap.put("Atrial_Flutter_Fibrillation_Autocorrelation_Peak_Width", 	null);
		bioportalMap.put("Atrial_Flutter_Fibrillation_Cycle_Length", 				null);
		bioportalMap.put("Atrial_Rate", 	null);
		bioportalMap.put("Atrial_Rhythm", 	null);
		bioportalMap.put("Atrial_Rate_Irregular_Percentage", null);
		bioportalMap.put("Atrial_Rate_Power_Spectrum", 		null);
		bioportalMap.put("Beat_Classification", 			null);
		bioportalMap.put("Bigeminy_Premature_Ventricular_Contraction_Count", 	null); 
		bioportalMap.put("Bigeminy_Premature_Ventricular_Contraction_String", 	null);
		bioportalMap.put("Bigeminy_RR_Interval_Autocorrelation",null);
		bioportalMap.put("Blood_Pressure_Diastolic", 			null);
		bioportalMap.put("Blood_Pressure_Systolic", 			null);
		bioportalMap.put("Constant_P_Shape_Percentage", 		null);
		bioportalMap.put("Database", 				"ECGTermsv1.owl#ECG_000000070");
		bioportalMap.put("ECG_Delta_Wave_Complex", 	"ECGTermsv1.owl#ECG_000000243");
		bioportalMap.put("Lead_Placement", 			"ECGOntologyv1.owl#ECG_000000338");
		bioportalMap.put("Fibrillation_Amplitude", 	null);
		bioportalMap.put("Fibrillation_Frequency",	null);
		bioportalMap.put("Fibrillation_Median_Frequency",		null);
		bioportalMap.put("Fibrillation_Width", 		null);
		bioportalMap.put("Atrial_Flutter_Fibrillation_Count", 	null);
		bioportalMap.put("High_Pass_Filter", 		null);
		bioportalMap.put("Low_Pass_Filter", 		null);
		bioportalMap.put("Heart_Rate", 	"ECGTermsv1.owl#ECG_000000833");
		bioportalMap.put("PR_Interval", "ECGTermsv1.owl#ECG_000000341");
		bioportalMap.put("PR_Segment", 	"ECGTermsv1.owl#ECG_000000506");
		bioportalMap.put("QRS_Wave_Duration", 	"ECGOntologyv1.owl#ECG_000000072");
		bioportalMap.put("QT_Corrected", 	  	"ECGTermsv1.owl#ECG_000000701");
		bioportalMap.put("Fridericias_Formula", "ECGTermsv1.owl#ECG_000000040");
		bioportalMap.put("QT_Interval", "ECGTermsv1.owl#ECG_000000682");
		bioportalMap.put("Mean_Ventricular_Rate", 	null);
		bioportalMap.put("Minimum_Ventricular_Rate",null);
		bioportalMap.put("P_Wave_Axis", "ECGTermsv1.owl#ECG_000000845");
		bioportalMap.put("P_Wave", 		"ECGOntologyv1.owl#ECG_000000759");
		bioportalMap.put("P_Wave_Offset", 		"ECGOntologyv1.owl#ECG_000000423");
		bioportalMap.put("P_Wave_Onset", 		"ECGOntologyv1.owl#ECG_000000805");
		bioportalMap.put("Pace_Mode_Alpha", 	null);
		bioportalMap.put("Pacemaker", 			null);
		bioportalMap.put("Pace_Mode_Ischemic", 	null);
		bioportalMap.put("Pace_Mode", 			null);
		bioportalMap.put("Q_Wave_Onset", 		"ECGOntologyv1.owl#ECG_000000238");
		bioportalMap.put("QRS_Wave_Complex_Axis", 	"ECGTermsv1.owl#ECG_000000838");
		bioportalMap.put("QRS_Wave_Complex_Offset", "ECGTermsv1.owl#ECG_000000522");
		bioportalMap.put("QRS_Wave_Complex_Onset", 	"ECGTermsv1.owl#ECG_000000727");
		bioportalMap.put("QT_Dispersion", 	"ECGOntologyv1.owl#ECG_000001078");
		bioportalMap.put("R_Wave_Axis", 	"ECGTermsv1.owl#ECG_000000843");
		bioportalMap.put("RR_Interval", 	"ECGOntologyv1.owl#ECG_000000042");
		bioportalMap.put("T_Wave_Axis", 	"ECGTermsv1.owl#ECG_000000841");
		bioportalMap.put("T_Wave_Offset", 	"ECGOntologyv1.owl#ECG_000000593");
		bioportalMap.put("T_Wave_Onset", 	"ECGOntologyv1.owl#ECG_000000422");
		bioportalMap.put("Trigeminy_Premature_Ventricular_Contraction_Count", 	null);
		bioportalMap.put("Trigeminy_Premature_Ventricular_Contraction_String", 	null);
		bioportalMap.put("Trigeminy_RR_Interval_Autocorrelation", 				null);
		bioportalMap.put("Wenckebach_Count", 	null);
		bioportalMap.put("Wenckebach_String", 	null);
		bioportalMap.put("J_Point_Amplitude", 	null);
		bioportalMap.put("PP_Wave_Amplitude", 	null);
		bioportalMap.put("P_Wave_Amplitude", 	"ECGOntologyv1.owl#ECG_000000415");
		bioportalMap.put("PP_Wave_Area", 		null);
		bioportalMap.put("P_Wave_Area", 		"ECGOntologyv1.owl#ECG_000000252");
		bioportalMap.put("PPP_Wave_Area", 		null);
		bioportalMap.put("PP_Wave_Duration", 	null);
		bioportalMap.put("P_Wave_Duration", 	"ECGOntologyv1.owl#ECG_000000231");
		bioportalMap.put("PPP_Wave_Duration", 	null);
		bioportalMap.put("PR_Interval", 		"ECGTermsv1.owl#ECG_000000341");
		bioportalMap.put("PR_Segment", 			"ECGTermsv1.owl#ECG_000000506");
		bioportalMap.put("Q_Wave_Amplitude", 	"ECGOntologyv1.owl#ECG_000000652");
		bioportalMap.put("Q_Wave_Duration",		"ECGOntologyv1.owl#ECG_000000551");
		bioportalMap.put("QRS_Wave_Complex_Amplitude", 	null);
		bioportalMap.put("QRS_Wave_Complex_Area", 		null);
		bioportalMap.put("QRS_Wave_Complex_Duration", 	null);
		bioportalMap.put("QT_Interval", 		"ECGTermsv1.owl#ECG_000000682");
		bioportalMap.put("RR_Wave_Amplitude", 	null);
		bioportalMap.put("R_Wave_Amplitude", 	"ECGOntologyv1.owl#ECG_000000750");
		bioportalMap.put("RR_Wave_Duration", 	null);
		bioportalMap.put("R_Wave_Duration", 	"ECGOntologyv1.owl#ECG_000000597");
		bioportalMap.put("S_Wave_Amplitude", 	"ECGOntologyv1.owl#ECG_000000107");
		bioportalMap.put("SS_Wave_Amplitude", 	null);
		bioportalMap.put("S_Wave_Duration", 	"ECGOntologyv1.owl#ECG_000000491");
		bioportalMap.put("SS_Wave_Duration", 	null);
		bioportalMap.put("ST_Segment_Duration", null);
		bioportalMap.put("ST_Segment", 			"ECGTermsv1.owl#ECG_000000617");
		bioportalMap.put("TT_Wave_Amplitude", 	null);
		bioportalMap.put("TTT_Wave_Amplitude", 	null);
		bioportalMap.put("T_Wave_Amplitude", 	"ECGOntologyv1.owl#ECG_000000468");
		bioportalMap.put("TT_Wave_Area", 		null);
		bioportalMap.put("T_Wave_Area", 		"ECGOntologyv1.owl#ECG_000000321");
		bioportalMap.put("TTT_Wave_Area", 		null);
		bioportalMap.put("TT_Wave_Duration", 	null);
		bioportalMap.put("T_Wave_Duration", 	"ECGOntologyv1.owl#ECG_000000345");
		
	}
	
	
	public static String lookup(String term){
		return bioportalMap.get(term);
	}
}
