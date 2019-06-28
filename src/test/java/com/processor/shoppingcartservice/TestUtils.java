package com.processor.shoppingcartservice;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import org.springframework.web.client.RestTemplate;

public class TestUtils {

	public static final RestTemplate REST = new RestTemplate();

	public static final MongoCartDocument SHOPPING_CART_DOCUMENT = MongoCartDocument.builder().build();
}
