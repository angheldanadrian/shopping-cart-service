package com.processor.shoppingcartservice.controller;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import com.processor.shoppingcartservice.model.ProductModel;
import com.processor.shoppingcartservice.model.ShoppingCartStatus;
import com.processor.shoppingcartservice.service.ShoppingCartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.processor.shoppingcartservice.TestUtils.*;
import static org.junit.Assert.*;
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
	public void when_gettingShoppingCartByCustomerId_Expect_ShoppingCartDocument_And_HttpStatusOK() {
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
	public void when_getttingShoppingCartByCustomerId_Expect_HttpStatusNotFound() {
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

	@Test
	public void when_filteringCustomerShoppingCartRecords_Expect_ShoppingCartDocument() {
		//given
		String rdate = LocalDate.now().plusDays(1).toString();
		String startDate = LocalDate.now().toString();
		String endDate = LocalDate.now().plusDays(10).toString();
		List<MongoCartDocument> expectedResult = SHOPPING_CART_DOCUMENTS;

		//when
		when(shoppingCartService.findAllByRDateOrCreatedDateOrEndDate(rdate, startDate, endDate))
				.thenReturn(expectedResult);
		List<MongoCartDocument> actual = shoppingCartController
				.filterCustomerShoppingCartRecords(rdate, startDate, endDate);

		//then
		assertEquals(expectedResult, actual);
		verify(shoppingCartService, times(1)).findAllByRDateOrCreatedDateOrEndDate(rdate,
				startDate, endDate);
	}

	@Test
	public void when_addingShoppingCartRecords_Expect_ShoppingCartDocument_And_HttpStatusOK() {
		//given
		String customerEcifId = "5628504543";
		List<ProductModel> shoppingCartRecords = SHOPPING_CART_RECORDS;
		MongoCartDocument expectedResult = SHOPPING_CART_DOCUMENT;

		//when
		when(shoppingCartService.insertOrUpdateShoppingCartRecords(customerEcifId, shoppingCartRecords))
				.thenReturn(Optional.of(expectedResult));
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.addShoppingCartRecords(customerEcifId,
				shoppingCartRecords);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(expectedResult, actual.getBody());
		verify(shoppingCartService, times(1)).insertOrUpdateShoppingCartRecords(customerEcifId,
				shoppingCartRecords);
		assertNotNull(actual.getBody().getProducts());
		assertTrue(actual.getBody().getProducts().size() > 0);
		assertNotNull(actual.getBody().getProducts().get(0).getId());
		assertNotNull(actual.getBody().getProducts().get(0).getProductBundleCode());
		assertNotNull(actual.getBody().getProducts().get(0).getProductCode());
		assertEquals(ShoppingCartStatus.OPEN.name(), actual.getBody().getProducts().get(0).getProductStatus());
	}

	@Test
	public void when_addingShoppingCartRecords_Expect_HttpStatusNotFound() {
		//given
		String customerEcifId = "5628504543";
		List<ProductModel> shoppingCartRecords = SHOPPING_CART_RECORDS;

		//when
		when(shoppingCartService.insertOrUpdateShoppingCartRecords(customerEcifId, shoppingCartRecords))
				.thenReturn(Optional.empty());
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.addShoppingCartRecords(customerEcifId,
				shoppingCartRecords);

		//then
		assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
		assertNull(actual.getBody());
		verify(shoppingCartService, times(1)).insertOrUpdateShoppingCartRecords(customerEcifId,
				shoppingCartRecords);
	}


	@Test
	public void when_updatingExistingShoppingCart_Expect_ShoppingCartDocument_And_HttpStatusOK() {
		//given
		String customerEcifId = "5628504543";
		String productIds = "4325435,756765876,56645654,5435,8769679,543424654";
		MongoCartDocument expectedResult = SHOPPING_CART_DOCUMENT;

		//when
		when(shoppingCartService.updateShoppingCartRecords(customerEcifId, productIds)).thenReturn(
				Optional.of(expectedResult));
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.updateExistingShoppingCart(customerEcifId,
				productIds);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(expectedResult, actual.getBody());
		verify(shoppingCartService, times(1)).updateShoppingCartRecords(customerEcifId, productIds);
	}

	@Test
	public void when_updatingExistingShoppingCart_Expect_HttpStatusNotFound() {
		//given
		String customerEcifId = "5628504543";
		List<ProductModel> shoppingCartRecords = SHOPPING_CART_RECORDS;

		//when
		when(shoppingCartService.insertOrUpdateShoppingCartRecords(customerEcifId, shoppingCartRecords))
				.thenReturn(Optional.empty());
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.addShoppingCartRecords(customerEcifId,
				shoppingCartRecords);

		//then
		assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
		assertNull(actual.getBody());
		verify(shoppingCartService, times(1)).insertOrUpdateShoppingCartRecords(customerEcifId,
				shoppingCartRecords);
	}

	@Test
	public void when_deletingShoppingCartRecords_Expect_HttpStatusOK() {
		//given
		String customerEcifId = "5628504543";

		//when
		when(shoppingCartService.deleteByCustomerId(customerEcifId)).thenReturn(Boolean.TRUE);
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.deleteShoppingCartRecords(customerEcifId);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		verify(shoppingCartService, times(1)).deleteByCustomerId(customerEcifId);
	}

	@Test
	public void when_deletingShoppingCartRecords_Expect_HttpStatusNotFound() {
		//given
		String customerEcifId = "5628504543";
		List<ProductModel> shoppingCartRecords = SHOPPING_CART_RECORDS;

		//when
		when(shoppingCartService.deleteByCustomerId(customerEcifId)).thenReturn(Boolean.FALSE);
		ResponseEntity<MongoCartDocument> actual = shoppingCartController.deleteShoppingCartRecords(customerEcifId);

		//then
		assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
		assertNull(actual.getBody());
		verify(shoppingCartService, times(1)).deleteByCustomerId(customerEcifId);
	}
}
