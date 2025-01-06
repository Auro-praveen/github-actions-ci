package com.auro.beans;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "terminalhealthpacket")

public class TerminalHealthPacket {

	@Id
	@Column(name="terminalID")
	private String terminalID;

	@Column(name="packettype")
	private String packettype;
	
	@Column(name="devicedate")
	private Date devicedate;
	
	@Column(name="devicetime")
	private Time devicetime;
	
	@Column(name="rdate")
	private Date rdate;
	
	@Column(name="rtime")
	private Time rtime;
	
	private int slno;
	
	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}

	

	public String getTerminalID() {
		return terminalID;
	}

	public void setTerminalID(String terminalID) {
		this.terminalID = terminalID;
	}

	public String getPackettype() {
		return packettype;
	}

	public void setPackettype(String packettype) {
		this.packettype = packettype;
	}

	public Date getDevicedate() {
		return devicedate;
	}

	public void setDevicedate(Date devicedate) {
		this.devicedate = devicedate;
	}

	public Time getDevicetime() {
		return devicetime;
	}

	public void setDevicetime(Time devicetime) {
		this.devicetime = devicetime;
	}

	public Date getRdate() {
		return rdate;
	}

	public void setRdate(Date rdate) {
		this.rdate = rdate;
	}

	public Time getRtime() {
		return rtime;
	}

	public void setRtime(Time rtime) {
		this.rtime = rtime;
	}
	
	

}
