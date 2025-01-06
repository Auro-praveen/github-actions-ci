package com.auro.beans;


 
import java.sql.*;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity

@Table(name="paygatorderid_details")
public class paygatorderid_details {

	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private int slno;
	
	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}

	public String getOrderID() {
		return orderID;
	}

	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}

	public Time getTraTime() {
		return traTime;
	}

	public void setTraTime(Time traTime) {
		this.traTime = traTime;
	}

	public Date getTraDate() {
		return traDate;
	}

	public void setTraDate(Date traDate) {
		this.traDate = traDate;
	}

	public String getTerminaLID() {
		return terminaLID;
	}

	public void setTerminaLID(String terminaLID) {
		this.terminaLID = terminaLID;
	}

	public String getLockNo() {
		return lockNo;
	}

	public void setLockNo(String lockNo) {
		this.lockNo = lockNo;
	}

	private String orderID;
	
    private String mobileNo;
	
    public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	int amount;
    int balance;
	private Time traTime;
	
	private Date traDate;
	
	private String terminaLID;
	private String lockNo;
	
	
	private String transactionType;
	
	private String paygatwVerStatus;
	
	 

	public String getPaygatwVerStatus() {
		return paygatwVerStatus;
	}

	public void setPaygatwVerStatus(String paygatwVerStatus) {
		this.paygatwVerStatus = paygatwVerStatus;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
}
