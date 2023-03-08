package com.GJardim.BMVendors;

/**
* Author: Guilherme Jardim
* version: 1.0
* Date: 28 July 2020
*/



public class BMVendor {

	private String name;
	private String type;
	private String address1;
	private String address2;
	private String city;
	private String province;
	private String country;
	private String phoneNumber;
	private String postalCode;

	public BMVendor(String name, String type, String address1, String address2, String city, String province,
			String country, String postalCode, String phoneNumber) {
		super();
		this.name = name;
		this.type = type;
		this.address1 = address1;
		this.address2 = address2;
		this.city = city;
		this.province = province;
		this.country = country;
		this.phoneNumber = phoneNumber;
		this.postalCode = postalCode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		BMVendor vendor = (BMVendor) obj;
		return (this.address1.equals(vendor.address1) && this.address2.equals(vendor.address2)
				&& this.city.equals(vendor.city) && this.country.equals(vendor.country) && this.name.equals(vendor.name)
				&& this.phoneNumber.equals(vendor.phoneNumber) && this.province.equals(vendor.province)
				&& this.postalCode.equals(vendor.postalCode) && this.type.equals(vendor.type));
	}

	@Override
	public String toString() {
		return "BMVendor [name=" + name + ", type=" + type + ", address1=" + address1 + ", address2=" + address2
				+ ", city=" + city + ", province=" + province + ", country=" + country + ", phoneNumber=" + phoneNumber
				+ ", postalCode=" + postalCode + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

}