package com.auro.beans;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity

@Table(name="transaction_details")
public class TransactionDetails {
	
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

	public Date getDate_of_open() {
		return date_of_open;
	}

	public void setDate_of_open(Date date_of_open) {
		this.date_of_open = date_of_open;
	}

	public Time getTime_of_open() {
		return time_of_open;
	}

	public void setTime_of_open(Time time_of_open) {
		this.time_of_open = time_of_open;
	}

	public String getTerminalid() {
		return terminalid;
	}

	public void setTerminalid(String terminalid) {
		this.terminalid = terminalid;
	}

	public float getNo_of_hours() {
		return no_of_hours;
	}

	public void setNo_of_hours(float no_of_hours) {
		this.no_of_hours = no_of_hours;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTransactionID() {
		return transactionID;
	}

	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public Date getClosing_date() {
		return closing_date;
	}

	public void setClosing_date(Date closing_date) {
		this.closing_date = closing_date;
	}

	public Time getClosing_time() {
		return closing_time;
	}

	public void setClosing_time(Time closing_time) {
		this.closing_time = closing_time;
	}

	public float getExcess_hours() {
		return excess_hours;
	}

	public void setExcess_hours(float excess_hours) {
		this.excess_hours = excess_hours;
	}

	public float getExcess_amount() {
		return excess_amount;
	}

	public void setExcess_amount(float excess_amount) {
		this.excess_amount = excess_amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLockNo() {
		return lockNo;
	}

	public void setLockNo(String lockNo) {
		this.lockNo = lockNo;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

//	public Date getDateofbirth() {
//		return dateofbirth;
//	}
//
//	public void setDateofbirth(Date dateofbirth) {
//		this.dateofbirth = dateofbirth;
//	}
	
	public String getDateofbirth() {
		return dateofbirth;
	}

	public void setDateofbirth(String dateofbirth) {
		this.dateofbirth = dateofbirth;
	}

	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id//this annotation is used for primarykey column(it show this variable is primarykey in table)
    private int slno;
	
	private String customerName;
	
	private String mobileNo;
	
	private Date date_of_open;
	
	private Time time_of_open;
	
	private String terminalid ;
	
	private float no_of_hours;
	
	private float  amount;
	
	private String status;
	
	private String transactionID;
	
    private Date closing_date;
	
	private Time closing_time;
	
	private float  excess_hours;
	
	private float  excess_amount;
	
	private String remark ;
	
	private String lockNo ;
	
	private String passcode ;
	
	private String dateofbirth;
	
//	private Date dateofbirth;
	
	private String systemorderId;
	
	private int balance=0;
	private String itemsStored;
	
	public String getItemsStored() {
		return itemsStored;
	}

	public void setItemsStored(String itemsStored) {
		this.itemsStored = itemsStored;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public String getSystemorderId() {
		return systemorderId;
	}

	public void setSystemorderId(String systemorderId) {
		this.systemorderId = systemorderId;
	}

	public String getSysgenorderstoreID() {
		return sysgenorderstoreID;
	}

	public void setSysgenorderstoreID(String sysgenorderstoreID) {
		this.sysgenorderstoreID = sysgenorderstoreID;
	}

	public String getSysgenorderretrieveID() {
		return sysgenorderretrieveID;
	}

	public void setSysgenorderretrieveID(String sysgenorderretrieveID) {
		this.sysgenorderretrieveID = sysgenorderretrieveID;
	}

	public String getPaygatewayPaymenstoreTRID() {
		return paygatewayPaymenstoreTRID;
	}

	public void setPaygatewayPaymenstoreTRID(String paygatewayPaymenstoreTRID) {
		this.paygatewayPaymenstoreTRID = paygatewayPaymenstoreTRID;
	}

	public String getPaygatewayPaymenstroeSignature() {
		return paygatewayPaymenstroeSignature;
	}

	public void setPaygatewayPaymenstroeSignature(String paygatewayPaymenstroeSignature) {
		this.paygatewayPaymenstroeSignature = paygatewayPaymenstroeSignature;
	}

	public String getPaygatewayexcpayTRID() {
		return paygatewayexcpayTRID;
	}

	public void setPaygatewayexcpayTRID(String paygatewayexcpayTRID) {
		this.paygatewayexcpayTRID = paygatewayexcpayTRID;
	}

	public String getPaygatewaystoreOrderID() {
		return paygatewaystoreOrderID;
	}

	public void setPaygatewaystoreOrderID(String paygatewaystoreOrderID) {
		this.paygatewaystoreOrderID = paygatewaystoreOrderID;
	}

	public String getPaygatewayexcpayorderTRID() {
		return paygatewayexcpayorderTRID;
	}

	public void setPaygatewayexcpayorderTRID(String paygatewayexcpayorderTRID) {
		this.paygatewayexcpayorderTRID = paygatewayexcpayorderTRID;
	}

	public String getPaygatewayPaymenretreiveSignature() {
		return paygatewayPaymenretreiveSignature;
	}

	public void setPaygatewayPaymenretreiveSignature(String paygatewayPaymenretreiveSignature) {
		this.paygatewayPaymenretreiveSignature = paygatewayPaymenretreiveSignature;
	}

	private String sysgenorderstoreID;
	private String sysgenorderretrieveID;
	private String paygatewayPaymenstoreTRID;
	private String paygatewayPaymenstroeSignature;
	private String paygatewayexcpayTRID;
	private String paygatewaystoreOrderID;
	private String paygatewayexcpayorderTRID;
	private String paygatewayPaymenretreiveSignature;
	
	private String browtype;
	
	public String getBrowtype() {
		return browtype;
	}

	public void setBrowtype(String browtype) {
		this.browtype = browtype;
	}

	private String expayTraID;

	public String getExpayTraID() {
		return expayTraID;
	}

	public void setExpayTraID(String expayTraID) {
		this.expayTraID = expayTraID;
	}
	
}

