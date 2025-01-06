package com.auro.beans;

import java.sql.Date;
import java.sql.Time;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="partialRetrieve_details")
public class PartialRetrieveData {

	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private int slno;
	
	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getTerminalID() {
		return terminalID;
	}

	public void setTerminalID(String terminalID) {
		this.terminalID = terminalID;
	}

	public String getLocNo() {
		return locNo;
	}

	public void setLocNo(String locNo) {
		this.locNo = locNo;
	}

	public Date getDateOfopen() {
		return dateOfopen;
	}

	public void setDateOfopen(Date dateOfopen) {
		this.dateOfopen = dateOfopen;
	}

	public Time getTime_of_open() {
		return time_of_open;
	}

	public void setTime_of_open(Time time_of_open) {
		this.time_of_open = time_of_open;
	}

	public String getOpenStatus() {
		return openStatus;
	}

	public void setOpenStatus(String openStatus) {
		this.openStatus = openStatus;
	}

	public Date getDateOffullclose() {
		return dateOffullclose;
	}

	public void setDateOffullclose(Date dateOffullclose) {
		this.dateOffullclose = dateOffullclose;
	}

	public Time getTime_of_fullclose() {
		return time_of_fullclose;
	}

	public void setTime_of_fullclose(Time time_of_fullclose) {
		this.time_of_fullclose = time_of_fullclose;
	}

	private String mobileNo;
	
    private String terminalID;
	
	 
	
	private String locNo;
	
	private Date dateOfopen;
	
	private Time time_of_open;
	private String openStatus;
	
	private Date dateOffullclose;
	
	private Time time_of_fullclose;

}
