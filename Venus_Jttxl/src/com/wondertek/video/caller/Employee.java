package com.wondertek.video.caller;

public class Employee {
	private String empid;
	private String name;
	private String mobile;
	private String headship;
	private String department;
	private String picutre;
	private String departmentFax;

	public String getDepartmentFax() {
		return departmentFax;
	}

	public void setDepartmentFax(String departmentFax) {
		this.departmentFax = departmentFax;
	}

	public String getEmpid() {
		return empid;
	}

	public void setEmpid(String empid) {
		this.empid = empid;
	}

	public String getPicutre() {
		return picutre;
	}

	public void setPicutre(String picutre) {
		this.picutre = picutre;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeadship() {
		return headship;
	}

	public void setHeadship(String headship) {
		this.headship = headship;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Employee(String empid, String name, String mobile, String headship,
			String department, String picture, String departmentFax) {
		this.name = name;
		this.mobile = mobile;
		this.headship = headship;
		this.department = department;
		this.picutre = picture;
		this.empid = empid;
		this.departmentFax = departmentFax;
	}

}
