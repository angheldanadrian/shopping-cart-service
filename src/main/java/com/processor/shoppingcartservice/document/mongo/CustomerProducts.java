package com.processor.shoppingcartservice.document.mongo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@Document(collection = "cart")
public class CustomerProducts {
	@Id
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
	private List<Product> products;
}
