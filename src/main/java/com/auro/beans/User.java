package com.auro.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;




@Entity

@Table(name="user_creation")
public class User {
	
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id//this annotation is used for primarykey column(it show this variable is primarykey in table)
    private int slno;
    
	private String userName;
	
	private String password;
	
	private String type;
	
	private String status;
	
	private String userpermissions;
	
	private String site_name;
	
	private String site_location;
	
	
	
	public String getSite_name() {
		return site_name;
	}

	public void setSite_name(String site_name) {
		this.site_name = site_name;
	}

	public String getSite_location() {
		return site_location;
	}

	public void setSite_location(String site_location) {
		this.site_location = site_location;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}


	public String getUserpermissions() {
		return userpermissions;
	}

	public void setUserpermissions(String userpermissions) {
		this.userpermissions = userpermissions;
	}




	

}
