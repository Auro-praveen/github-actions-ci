package com.auro.beans;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="termwiselockermalfunction")

public class TermwiseLockermalfunction {

	public String getTerminalid() {
		return terminalid;
	}

	public void setTerminalid(String terminalid) {
		this.terminalid = terminalid;
	}

	public String getLocks() {
		return locks;
	}

	public void setLocks(String locks) {
		this.locks = locks;
	}

	public Date getMdate() {
		return mdate;
	}

	public void setMdate(Date mdate) {
		this.mdate = mdate;
	}

	public Date getRdate() {
		return rdate;
	}

	public void setRdate(Date rdate) {
		this.rdate = rdate;
	}

	public String getOpenuserid() {
		return openuserid;
	}

	public void setOpenuserid(String openuserid) {
		this.openuserid = openuserid;
	}

	public String getCloseuserid() {
		return closeuserid;
	}

	public void setCloseuserid(String closeuserid) {
		this.closeuserid = closeuserid;
	}
	
	

	public String getCloseremarks() {
		return closeremarks;
	}

	public void setCloseremarks(String closeremarks) {
		this.closeremarks = closeremarks;
	}

	public int getNo_of_locks() {
		return no_of_locks;
	}

	public void setNo_of_locks(int no_of_locks) {
		this.no_of_locks = no_of_locks;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIssueremarks() {
		return issueremarks;
	}

	public void setIssueremarks(String issueremarks) {
		this.issueremarks = issueremarks;
	}



	@Id
	private String terminalid;

	
	private String locks;
	
//	@Column(name = "mdate" , columnDefinition = "Date default ''")
	private Date mdate;

	private Date rdate;
	
	
	
	private String  openuserid;

	private String  closeuserid;
	
	private String closeremarks;
	
	private int no_of_locks;
	
	private String status;
	
	private String issueremarks;
	
}
