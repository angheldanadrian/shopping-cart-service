package com.processor.shoppingcartservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CartModel {
	private String id;
	private String shopCartStatus;
	private String customerEcifId;
	private String customerProfileType;
	private String createdDate;
	private String endDate;
	private String rDate;
	private String createdBy;
	private String modifiedDate;
	private String modifiedBy;
	private List<ProductModel> products;
}
