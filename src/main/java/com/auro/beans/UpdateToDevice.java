package com.auro.beans;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "update_todevice")
public class UpdateToDevice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int slno;
	
	private String updateType;
	private String destPath;
	private String fileName;
	private String terminalID;
	private String status;
	private Time updatedTime;
	private Date updatedDate;
	private Time generatedTime;
	private Date generatedDate;
	
	public int getSlno() {
		return slno;
	}
	
	public void setSlno(int slno) {
		this.slno = slno;
	}
	
	public String getUpdateType() {
		return updateType;
	}
	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}
	public String getDestPath() {
		return destPath;
	}
	public void setDestPath(String destPath) {
		this.destPath = destPath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Time getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(Time updatedTime) {
		this.updatedTime = updatedTime;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Time getGeneratedTime() {
		return generatedTime;
	}
	public void setGeneratedTime(Time generatedTime) {
		this.generatedTime = generatedTime;
	}
	public Date getGeneratedDate() {
		return generatedDate;
	}
	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public String getTerminalID() {
		return terminalID;
	}

	public void setTerminalID(String terminalID) {
		this.terminalID = terminalID;
	}
	
}
