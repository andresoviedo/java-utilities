package org.andresoviedo.util.bean;

import java.io.Serializable;

public class LoggerInfoBean implements Serializable {

	private static final long serialVersionUID = -237405922930781583L;

	// Logger name
	private String name;

	// File parameters
	private String logFileName;
	private String levelName;
	private int numFiles;
	private int fileSize;

	// Mail parameters
	private boolean useMailHandler;
	private MailInfoBean mailInfo;
	private int maxMailRecords;

	public LoggerInfoBean() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setNumFiles(int numFiles) {
		this.numFiles = numFiles;
	}

	public int getNumFiles() {
		return numFiles;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getFileSize() {
		return fileSize;
	}

	// ------------------------------------------------------------------------------------------- //

	public boolean isUseMailHandler() {
		return useMailHandler;
	}

	public void setUseMailHandler(boolean useMailHandler) {
		this.useMailHandler = useMailHandler;
	}

	public void setMailInfo(MailInfoBean mailInfo) {
		this.mailInfo = mailInfo;
	}

	public MailInfoBean getMailInfo() {
		return this.mailInfo;
	}

	public int getMaxMailRecords() {
		return maxMailRecords;
	}

	public void setMaxMailRecords(int maxMailRecords) {
		this.maxMailRecords = maxMailRecords;
	}
}