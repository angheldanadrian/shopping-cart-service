package com.processor.shoppingcartservice;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import com.processor.shoppingcartservice.document.mongo.MongoProductDocument;
import com.processor.shoppingcartservice.model.CustomerProfileType;
import com.processor.shoppingcartservice.model.ShoppingCartStatus;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestUtils {

	public static final RestTemplate REST = new RestTemplate();

	public static final MongoCartDocument SHOPPING_CART_DOCUMENT = MongoCartDocument.builder()
			.shopCartStatus(ShoppingCartStatus.OPEN.name())
			.customerEcifId(UUID.randomUUID().toString())
			.customerProfileType(CustomerProfileType.PERSONAL.name())
			.createdDate(LocalDateTime.now().toString())
			.endDate(LocalDateTime.now().plusMinutes(15).toString())
			.rDate(LocalDate.now().toString())
			.createdBy("loggedCustommer")
			.modifiedDate(LocalDateTime.now().toString())
			.modifiedBy("loggedCustommer")
			.products(Arrays.asList(MongoProductDocument.builder().build()))
			.build();

	public static final List<MongoCartDocument> SHOPPING_CART_DOCUMENTS = Arrays.asList(SHOPPING_CART_DOCUMENT);
}
