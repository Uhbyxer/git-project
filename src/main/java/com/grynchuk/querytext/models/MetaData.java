package com.grynchuk.querytext.models;

import java.util.Date;



public class MetaData {
	private String fileName;
	
	private String fileSize;
	
	private Date fileCreationDate;

	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public Date getFileCreationDate() {
		return fileCreationDate;
	}
	public void setFileCreationDate(Date date) {
		this.fileCreationDate = date;
	}
}
