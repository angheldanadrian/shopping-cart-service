package com.processor.shoppingcartservice.controller;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import com.processor.shoppingcartservice.service.ShoppingCartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.processor.shoppingcartservice.TestUtils.SHOPPING_CART_DOCUMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShoppingCartControllerTest {
	private ShoppingCartController shoppingCartController;

	@Mock
	private ShoppingCartService shoppingCartService;

	@Before
	public void setup() {
		shoppingCartController = new ShoppingCartController(shoppingCartService);
	}

	@Test
	public void When_getShoppingCartByCustomerId_Expect_ShoppingCartDocument_And_HttpStatusOK() {
		//given
		String customerEcifId = "5628504543";
		MongoCartDocument expectedResult = SHOPPING_CART_DOCUMENT;

		//when
		when(shoppingCartService.findById(customerEcifId)).thenReturn(Optional.of(expectedResult));
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.findByCustomerId(customerEcifId);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(expectedResult, actual.getBody());
		verify(shoppingCartService, times(1)).findById(customerEcifId);
	}

	@Test
	public void When_getShoppingCartByCustomerId_Expect_HttpStatusNotFound() {
		//given
		String customerEcifId = "5628504543";

		//when
		when(shoppingCartService.findById(customerEcifId)).thenReturn(Optional.empty());
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.findByCustomerId(customerEcifId);

		//then
		assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
		assertNull(actual.getBody());
		verify(shoppingCartService, times(1)).findById(customerEcifId);
	}
}
