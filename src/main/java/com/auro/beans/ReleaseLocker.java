package com.auro.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "releaselockerdetails")
public class ReleaseLocker {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private int slno;

	private String terminalId;
	private String lockNumber;
	private String userId;
	private String lockReleaseDate;
	private String lockReleaseTime;
	public int getSlno() {
		return slno;
	}
	public void setSlno(int slno) {
		this.slno = slno;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getLockNumber() {
		return lockNumber;
	}
	public void setLockNumber(String lockNumber) {
		this.lockNumber = lockNumber;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getLockReleaseDate() {
		return lockReleaseDate;
	}
	public void setLockReleaseDate(String lockReleaseDate) {
		this.lockReleaseDate = lockReleaseDate;
	}
	public String getLockReleaseTime() {
		return lockReleaseTime;
	}
	public void setLockReleaseTime(String lockReleaseTime) {
		this.lockReleaseTime = lockReleaseTime;
	}
	
}
