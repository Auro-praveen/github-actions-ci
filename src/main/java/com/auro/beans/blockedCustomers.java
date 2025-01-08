package com.auro.beans;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity

@Table(name="blockedCustomers")
public class blockedCustomers {
	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private int slno;
	private String name;
	private String reason;
	private String mobilenumber;
	private Date dateofblock;
	private Date datofunblock;
	private String city;
	private String status;
	
	
	public int getSlno() {
		return slno;
	}
	public void setSlno(int slno) {
		this.slno = slno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	 
	 
	public String getMobilenumber() {
		return mobilenumber;
	}
	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}
	public Date getDateofblock() {
		return dateofblock;
	}
	public void setDateofblock(Date dateofblock) {
		this.dateofblock = dateofblock;
	}
	public Date getDatofunblock() {
		return datofunblock;
	}
	public void setDatofunblock(Date datofunblock) {
		this.datofunblock = datofunblock;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	
}
