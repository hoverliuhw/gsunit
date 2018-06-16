package com.lucent.gui;

public class CaseResult {
	//NOT_RUN, SUCCESS, FAILURE, PASS, FAIL, FAIL_PARSE, ERROR, INVALID;
	public static final int NOT_RUN = 0;
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	public static final int ERROR = 3;

	public static final String[] STRING = {"NOT_RUN", "SUCCESS", "FAILURE", "ERROR"};
	public static final CaseResult RESULT_NOT_RUN = new CaseResult(CaseResult.NOT_RUN);

	private String message;
	
	/*The log file is not debuglog, it is case execution log */
	private String logFile;
	
	private String reportFile;
	private int time;
	
	private final int value;
	
	public CaseResult(int value) {
		this.value = value % 4;
	}
	
	/* String s should be one value in STRING[],
	 * Else, take it as ERROR
	 */
	public static CaseResult valueOf(String s) {
		int value = -1;
		for (int i = 0; i < STRING.length; i++) {
			if (STRING[i].equals(s)) {
				value = i;
				break;
			}
		}
		if (value < 0) {
			value = ERROR;
		}
		
		return new CaseResult(value);
	}
	
	public int getValue() {
		return value;
	}
	public void setMessage(String str) {
		message = str;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setLogFile(String file) {
		logFile = file;
	}
	
	public String getLogFile() {
		return logFile;
	}
	
	public void setReportFile(String report) {
		reportFile = report;
	}
	
	public String getReportFile() {
		return reportFile;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public int getTimeCost() {
		return time;
	}
	
	public String toString() {
		return STRING[value];
	}

}
