package com.auro.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "terminallockerstatusdetail ")
public class TerminalLockStatusDetail {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int SlNo;
	
	@Column(name="packettype")
	private String packettype;
	
	 
	@Column(name="status")
	private String status;
	
	
	@Column(name="remarks")
	private String remarks;
	
	@Column(name="userid")
	private String userid;
	
	 
	@Column(name="ticldate")
	private java.sql.Date ticldate;
	
	@Column(name="ticltime")
	private java.sql.Time ticltime;
	
	@Column(name="lockerNo")
	private int lockerNo;
	

	@Column(name="devicedate")
	private java.sql.Date devicedate;
	
	@Column(name="devicetime")
	private java.sql.Time devicetime;
	
	@Column(name="rdate")
	private java.sql.Date rdate;
	
	@Column(name="rtime")
	private java.sql.Time rtime;
	
	@Column(name="terminalID")
	private String terminalID;
	
	public int getLockerNo() {
		return lockerNo;
	}

	public void setLockerNo(int lockerNo) {
		this.lockerNo = lockerNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public java.sql.Date getTicldate() {
		return ticldate;
	}

	public void setTicldate(java.sql.Date ticldate) {
		this.ticldate = ticldate;
	}

	public java.sql.Time getTicltime() {
		return ticltime;
	}

	public void setTicltime(java.sql.Time ticltime) {
		this.ticltime = ticltime;
	}

	public int getSlNo() {
		return SlNo;
	}

	public void setSlNo(int slNo) {
		SlNo = slNo;
	}

	public String getPackettype() {
		return packettype;
	}
	
	public void setPackettype(String packettype) {
		this.packettype = packettype;
	}

	public java.sql.Date getDevicedate() {
		return devicedate;
	}

	public void setDevicedate(java.sql.Date devicedate) {
		this.devicedate = devicedate;
	}

	public java.sql.Time getDevicetime() {
		return devicetime;
	}

	public void setDevicetime(java.sql.Time devicetime) {
		this.devicetime = devicetime;
	}

	public java.sql.Date getRdate() {
		return rdate;
	}

	public void setRdate(java.sql.Date rdate) {
		this.rdate = rdate;
	}

	public java.sql.Time getRtime() {
		return rtime;
	}

	public void setRtime(java.sql.Time rtime) {
		this.rtime = rtime;
	}


	
	public String getTerminalID() {
		return terminalID;
	}

	public void setTerminalID(String terminalID) {
		this.terminalID = terminalID;
	}

	 
}

