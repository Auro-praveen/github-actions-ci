package com.auro.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity

@Table(name="terminallockmapping")
public class TerminalLockMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int SlNo;
	
	@Column(name="terminalID")
	private String terminalID;
	
 
	@Column(name="lockerName")
	private String lockerName;
	
	
	@Column(name="lockerNumber")
	private int lockerNumber;

	public int getSlNo() {
		return SlNo;
	}


	public void setSlNo(int slNo) {
		SlNo = slNo;
	}


	public String getTerminalID() {
		return terminalID;
	}


	public void setTerminalID(String terminalID) {
		this.terminalID = terminalID;
	}


	public String getLockerName() {
		return lockerName;
	}


	public void setLockerName(String lockerName) {
		this.lockerName = lockerName;
	}


	public int getLockerNumber() {
		return lockerNumber;
	}


	public void setLockerNumber(int lockerNumber) {
		this.lockerNumber = lockerNumber;
	}



	
	
}
