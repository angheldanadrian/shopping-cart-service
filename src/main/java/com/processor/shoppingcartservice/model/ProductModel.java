package com.processor.shoppingcartservice.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductModel {
	@NonNull
	private String id;
	@NonNull
	private String productStatus;
	@NonNull
	private String productCode;
	private String productName;
	private String productCategory;
	@NonNull
	private String productBundleCode;
	private String createdDate;
	private String createdBy;
	private String closedDate;
	private String closedBy;
}
