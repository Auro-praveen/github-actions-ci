package com.auro.beans;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity

@Table(name="amountreceiveddetails")
public class amountreceiveddetails   {
	
	 
	@Id//this annotation is used for primarykey column(it show this variable is primarykey in table)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private int slno;
	@Column(name = "orderID", unique = true) // Ensures email is unique
		private String orderID;  //Satish August 07 2024
	
	
	public int getSlno() {
		return slno;
	}
	public void setSlno(int slno) {
		this.slno = slno;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public Date getReceived_date() {
		return received_date;
	}
	public void setReceived_date(Date received_date) {
		this.received_date = received_date;
	}
	public Time getReceived_time() {
		return received_time;
	}
	public void setReceived_time(Time received_time) {
		this.received_time = received_time;
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
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public String getGstNumber() {
		return gstNumber;
	}
	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}
	 
	public float getSgstPercentage() {
		return sgstPercentage;
	}
	public void setSgstPercentage(float sgstPercentage) {
		this.sgstPercentage = sgstPercentage;
	}
	public float getCgstPercentage() {
		return cgstPercentage;
	}
	public void setCgstPercentage(float cgstPercentage) {
		this.cgstPercentage = cgstPercentage;
	}
	public float getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(float totalAmount) {
		this.totalAmount = totalAmount;
	}
	public float getDiscountPercentage() {
		return discountPercentage;
	}
	public void setDiscountPercentage(float discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	public float getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(float discountAmount) {
		this.discountAmount = discountAmount;
	}
	public String getHsnCode() {
		return hsnCode;
	}
	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}
	public String getTransactionid() {
		return transactionid;
	}
	public void setTransactionid(String transactionid) {
		this.transactionid = transactionid;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getPaymentGatewayID() {
		return paymentGatewayID;
	}
	public void setPaymentGatewayID(String paymentGatewayID) {
		this.paymentGatewayID = paymentGatewayID;
	}
	public String getTransaction_type() {
		return transaction_type;
	}
	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}

	private String customerName;
	private String mobileNo;
	
	private Date received_date;
	
	private Time received_time;
	
	private String terminalid ;
	

	
 
	
	
	private String lockNo ;
	
 	
	
	 
	private String stateName;
	private String gstNumber;
	private float sgstPercentage;
	private float cgstPercentage;
	private float totalAmount;
	private float amount;
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}

	private float discountPercentage;
	private float discountAmount;
	private String hsnCode;
 
	private String transactionid;  //Satish August 07 2024
	
	private String paymentGatewayID;  //Satish August 07 2024
	private String transaction_type;  //Satish August 07 2024

 
 	
}
