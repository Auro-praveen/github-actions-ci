package com.auro.beans;


 




import java.sql.Date;
import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity

@Table(name="invoiceDetails")
public class invoiceDetails {
	
	 

	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id//this annotation is used for primarykey column(it show this variable is primarykey in table)
    private int slno;
	
	
	
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
	public Date getInvoice_date() {
		return invoice_date;
	}
	public void setInvoice_date(Date invoice_date) {
		this.invoice_date = invoice_date;
	}
	public Time getInvoice_time() {
		return invoice_time;
	}
	public void setInvoice_time(Time invoice_time) {
		this.invoice_time = invoice_time;
	}
	public String getTerminalid() {
		return terminalid;
	}
	public void setTerminalid(String terminalid) {
		this.terminalid = terminalid;
	}
	public float getLockerRentamount() {
		return lockerRentamount;
	}
	public void setLockerRentamount(float lockerRentamount) {
		this.lockerRentamount = lockerRentamount;
	}
	public int getLockerRenthour() {
		return lockerRenthour;
	}
	public void setLockerRenthour(int lockerRenthour) {
		this.lockerRenthour = lockerRenthour;
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
	public String getLockNo() {
		return lockNo;
	}
	public void setLockNo(String lockNo) {
		this.lockNo = lockNo;
	}
	public int getPartretamount() {
		return partretamount;
	}
	public void setPartretamount(int partretamount) {
		this.partretamount = partretamount;
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
	public float getIgstPercentage() {
		return igstPercentage;
	}
	public void setIgstPercentage(float igstPercentage) {
		this.igstPercentage = igstPercentage;
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
	
	private String customerName;
	private String mobileNo;
	
	private Date invoice_date;
	
	private Time invoice_time;
	
	private String terminalid ;
	

	
	private float  lockerRentamount;
	private int lockerRenthour;
	
	private float  excess_hours;
	
	private float  excess_amount;
	
	
	private String lockNo ;
	
 	
	
	private int partretamount;
	private String stateName;
	private String gstNumber;
	private float igstPercentage;
	private float cgstPercentage;
	private float totalAmount;
	private float discountPercentage;
	private float discountAmount;
	private String hsnCode;
	private float balanceAmount;
	private String invoinumber;  //Satish August 07 2024


	public String getInvoinumber() {
		return invoinumber;
	}
	public void setInvoinumber(String invoinumber) {
		this.invoinumber = invoinumber;
	}
	public float getBalanceAmount() {
		return balanceAmount;
	}
	public void setBalanceAmount(float balanceAmount) {
		this.balanceAmount = balanceAmount;
	}
	public String getHsnCode() {
		return hsnCode;
	}
	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
	}
	
 	
}
