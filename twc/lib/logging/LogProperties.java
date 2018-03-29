package com.twc.eis.lib.logging;

/**
 * <p>
 * Value object holding properties data.
 * </p>
 * 
 * @author Chris Mathew
 * @Task 560
 * 
 */

public class LogProperties {

	private Integer thresholdLevel;
	private String dateFormat;
	private String loggerType;
	private Boolean isDsbLogger;
	private String fileName;
	private String folderLocation;
	private Integer queueCapacity;
	private String fileRollingType;

	/**
	 * 
	 * @return threshold level
	 */
	public Integer getThresholdLevel() {
		return thresholdLevel;
	}

	/**
	 * 
	 * @param thresholdLevel
	 */
	public void setThresholdLevel(Integer thresholdLevel) {
		this.thresholdLevel = thresholdLevel;
	}

	/**
	 * 
	 * @return date format
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * 
	 * @param dateFormat
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * 
	 * @return logger type
	 */
	public String getLoggerType() {
		return loggerType;
	}

	/**
	 * 
	 * @param loggerType
	 */
	public void setLoggerType(String loggerType) {
		this.loggerType = loggerType;
	}

	/**
	 * 
	 * @return true or false
	 */
	public Boolean getIsDsbLogger() {
		return isDsbLogger;
	}

	/**
	 * 
	 * @param isDsbLogger
	 */
	public void setIsDsbLogger(Boolean isDsbLogger) {
		this.isDsbLogger = isDsbLogger;
	}

	/**
	 * 
	 * @return file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 
	 * @return folder location
	 */
	public String getFolderLocation() {
		return folderLocation;
	}

	/**
	 * 
	 * @param folderLocation
	 */
	public void setFolderLocation(String folderLocation) {
		this.folderLocation = folderLocation;
	}

	/**
	 * 
	 * @return queue capacity
	 */
	public Integer getQueueCapacity() {
		return queueCapacity;
	}

	/**
	 * 
	 * @param queueCapacity
	 */
	public void setQueueCapacity(Integer queueCapacity) {
		this.queueCapacity = queueCapacity;
	}
	
	/**
	 * 
	 * @return rolling type
	 */
	public String getFileRollingType() {
		return fileRollingType;
	}
	/**
	 * 
	 * @param fileRollingType
	 */
	public void setFileRollingType(String fileRollingType) {
		this.fileRollingType = fileRollingType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateFormat == null) ? 0 : dateFormat.hashCode());
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result
				+ ((folderLocation == null) ? 0 : folderLocation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LogProperties other = (LogProperties) obj;
		if (dateFormat == null) {
			if (other.dateFormat != null)
				return false;
		} else if (!dateFormat.equals(other.dateFormat))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (folderLocation == null) {
			if (other.folderLocation != null)
				return false;
		} else if (!folderLocation.equals(other.folderLocation))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LogProperties [thresholdLevel=" + thresholdLevel
				+ ", dateFormat=" + dateFormat + ", loggerType=" + loggerType
				+ ", isDsbLogger=" + isDsbLogger + ", fileName=" + fileName
				+ ", folderLocation=" + folderLocation + "]";
	}

}
