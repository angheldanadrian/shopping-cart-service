package com.processor.shoppingcartservice.model;

import org.springframework.lang.NonNull;

public class ProductModel {
	@NonNull
	private String productCode;
	private String productName;
	@NonNull
	private String productCategory;
	private String productBundleCode;

	public String getProductCode() {
		return productCode;
	}

	public String getProductName() {
		return productName;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public String getProductBundleCode() {
		return productBundleCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public void setProductBundleCode(String productBundleCode) {
		this.productBundleCode = productBundleCode;
	}
}
