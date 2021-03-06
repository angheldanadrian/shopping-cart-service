package com.processor.shoppingcartservice;

import com.processor.shoppingcartservice.document.mongo.CustomerProducts;
import com.processor.shoppingcartservice.document.mongo.Product;
import com.processor.shoppingcartservice.model.CustomerProfileType;
import com.processor.shoppingcartservice.model.ProductModel;
import com.processor.shoppingcartservice.model.ShoppingCartStatus;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestUtils {

	public static final RestTemplate REST = new RestTemplate();

	public static final CustomerProducts SHOPPING_CART_DOCUMENT = CustomerProducts.builder()
			.shopCartStatus(ShoppingCartStatus.OPEN)
			.customerEcifId(UUID.randomUUID().toString())
			.customerProfileType(CustomerProfileType.PERSONAL)
			.createdDate(LocalDateTime.now().toString())
			.createdBy("loggedCustommer")
			.modifiedDate(LocalDateTime.now().toString())
			.modifiedBy("loggedCustommer")
			.products(Arrays.asList(Product.builder().id(UUID.randomUUID().toString())
					.productCode(UUID.randomUUID().toString())
					.productStatus(ShoppingCartStatus.OPEN.name())
					.productBundleCode(UUID.randomUUID().toString()).build()))
			.build();

	public static final List<CustomerProducts> SHOPPING_CART_DOCUMENTS = Arrays.asList(SHOPPING_CART_DOCUMENT);

	public static final List<ProductModel> SHOPPING_CART_RECORDS = Arrays.asList(new ProductModel());
}
