package com.processor.shoppingcartservice.model;

public enum ShoppingCartStatus {
	OPEN("open"), CLOSED("closed");

	private String type;

	ShoppingCartStatus(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}
}
