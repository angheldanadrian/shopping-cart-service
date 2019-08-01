package com.processor.shoppingcartservice.document.mongo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Builder
@Document(collection = "product")
public class Product {
	@Id
	private String id;
	private String productStatus;
	private String productCode;
	private String productName;
	private String productCategory;
	private String productBundleCode;
	private String createdDate;
	private String createdBy;
	private String closedDate;
	private String closedBy;
}
