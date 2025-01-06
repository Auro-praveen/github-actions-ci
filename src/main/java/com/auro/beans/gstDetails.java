package com.auro.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="statewise_gstdetails")

public class gstDetails {
	
	String stateName;
	String gstNumber;
	double cgstPercentage;
	double igstPercentage;
	String hsnCode;
	
	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private int slno;
	
	
	public String getHsnCode() {
		return hsnCode;
	}
	public void setHsnCode(String hsnCode) {
		this.hsnCode = hsnCode;
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
	public double getCgstPercentage() {
		return cgstPercentage;
	}
	public void setCgstPercentage(double cgstPercentage) {
		this.cgstPercentage = cgstPercentage;
	}
	public double getIgstPercentage() {
		return igstPercentage;
	}
	public void setIgstPercentage(double igstPercentage) {
		this.igstPercentage = igstPercentage;
	}
	

}
