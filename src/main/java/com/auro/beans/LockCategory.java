package com.auro.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity

@Table(name="locker_category")

public class LockCategory {
	

	@GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    private int slno;
	
	private String terminalid;
	
	private String siteName;
	
	private String no_of_locks;
	
	private String locks;
	
	private String category;
	
	private String slot;
	
	private float amount;
	private int minslotamt;
	private String typeofday;
	private int minslot;
	
	public LockCategory() {
		
	}
	

	public LockCategory(String terminalid, String siteName, String no_of_locks, String locks, String category,
			String slot, float amount, int minslotamt, String typeofday, int minslot) {
		super();
//		this.slno = slno;
		this.terminalid = terminalid;
		this.siteName = siteName;
		this.no_of_locks = no_of_locks;
		this.locks = locks;
		this.category = category;
		this.slot = slot;
		this.amount = amount;
		this.minslotamt = minslotamt;
		this.typeofday = typeofday;
		this.minslot = minslot;
	}

	public int getMinslotamt() {
		return minslotamt;
	}

	public void setMinslotamt(int minslotamt) {
		this.minslotamt = minslotamt;
	}

	public String getTypeofday() {
		return typeofday;
	}

	public void setTypeofday(String typeofday) {
		this.typeofday = typeofday;
	}

	public int getMinslot() {
		return minslot;
	}

	public void setMinslot(int minslot) {
		this.minslot = minslot;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public int getSlno() {
		return slno;
	}

	public void setSlno(int slno) {
		this.slno = slno;
	}

	public String getTerminalid() {
		return terminalid;
	}

	public void setTerminalid(String terminalid) {
		this.terminalid = terminalid;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getNo_of_locks() {
		return no_of_locks;
	}

	public void setNo_of_locks(String no_of_locks) {
		this.no_of_locks = no_of_locks;
	}

	public String getLocks() {
		return locks;
	}

	public void setLocks(String locks) {
		this.locks = locks;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
