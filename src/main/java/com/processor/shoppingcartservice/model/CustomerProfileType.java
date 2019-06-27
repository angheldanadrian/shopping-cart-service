package com.processor.shoppingcartservice.model;

public enum CustomerProfileType {
	PERSONAL("personal"), COMMERCIAL("commercial");

	private String type;

	CustomerProfileType(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}
}
