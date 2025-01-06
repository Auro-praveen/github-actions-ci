package com.auro.beans;


// its no entity just created for some operations regarding locker operation from hql data

public class LockerOccupacyCountBean {

	private String terminalid;
	private String lockNo;
	private long lockCount;
	
	public LockerOccupacyCountBean() {
		
	}
	
	
	public LockerOccupacyCountBean(String terminalid, String lockNo, long lockCount) {
		super();
		this.terminalid = terminalid;
		this.lockNo = lockNo;
		this.lockCount = lockCount;
	}
	
	public String getTerminalid() {
		return terminalid;
	}
	public void setTerminalid(String terminalid) {
		this.terminalid = terminalid;
	}
	public String getLockNo() {
		return lockNo;
	}
	public void setLockNo(String lockNo) {
		this.lockNo = lockNo;
	}
	public long getLockCount() {
		return lockCount;
	}
	public void setLockCount(long lockCount) {
		this.lockCount = lockCount;
	}
	
	

}
