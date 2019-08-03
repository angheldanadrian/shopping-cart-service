package com.processor.shoppingcartservice.controller;

import com.processor.shoppingcartservice.document.mongo.CustomerProducts;
import com.processor.shoppingcartservice.model.CustomerProfileType;
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
import java.util.UUID;

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
		CustomerProducts expectedResult = SHOPPING_CART_DOCUMENT;

		//when
		when(shoppingCartService.findByCustomerId(customerEcifId)).thenReturn(Optional.of(expectedResult));
		ResponseEntity<CustomerProducts> actual = shoppingCartController.findByCustomerId(customerEcifId);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(expectedResult, actual.getBody());
		verify(shoppingCartService, times(1)).findByCustomerId(customerEcifId);
	}

	@Test
	public void when_getttingShoppingCartByCustomerId_Expect_HttpStatusNotFound() {
		//given
		String customerEcifId = "5628504543";

		//when
		when(shoppingCartService.findByCustomerId(customerEcifId)).thenReturn(Optional.empty());
		ResponseEntity<CustomerProducts> actual = shoppingCartController.findByCustomerId(customerEcifId);

		//then
		assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
		assertNull(actual.getBody());
		verify(shoppingCartService, times(1)).findByCustomerId(customerEcifId);
	}

	@Test
	public void when_filteringCustomerShoppingCartRecords_Expect_ShoppingCartDocument() {
		//given
		String rdate = LocalDate.now().plusDays(1).toString();
		String startDate = LocalDate.now().toString();
		String endDate = LocalDate.now().plusDays(10).toString();
		List<CustomerProducts> expectedResult = SHOPPING_CART_DOCUMENTS;

		//when
		when(shoppingCartService.findAllByRDateOrCreatedDateOrEndDate(rdate, startDate, endDate))
				.thenReturn(expectedResult);
		List<CustomerProducts> actual = shoppingCartController
				.filterCustomerShoppingCartRecords(rdate, startDate, endDate);

		//then
		assertEquals(expectedResult, actual);
		verify(shoppingCartService, times(1)).findAllByRDateOrCreatedDateOrEndDate(rdate,
				startDate, endDate);
	}

	@Test
	public void when_addingShoppingCartRecords_Expect_ShoppingCartDocument_And_HttpStatusOK() {
		//given
		String customerEcifId = UUID.randomUUID().toString();
		List<ProductModel> shoppingCartRecords = SHOPPING_CART_RECORDS;
		CustomerProducts expectedResult = SHOPPING_CART_DOCUMENT;
		final String createdBy = "Mocked person name";

		//when
		when(shoppingCartService.insertShoppingCartRecords(customerEcifId, shoppingCartRecords, createdBy,
				CustomerProfileType.PERSONAL)).thenReturn(Optional.of(expectedResult));
		ResponseEntity<Object> actual = shoppingCartController.addShoppingCartRecords(customerEcifId,
				createdBy, CustomerProfileType.PERSONAL, shoppingCartRecords);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(expectedResult, actual.getBody());
		verify(shoppingCartService, times(1)).insertShoppingCartRecords(customerEcifId,
				shoppingCartRecords, createdBy, CustomerProfileType.PERSONAL);
	}

	@Test
	public void when_addingShoppingCartRecords_Expect_HttpStatusNotAllowed() {
		//given
		String customerEcifId = "5628504543";
		List<ProductModel> shoppingCartRecords = SHOPPING_CART_RECORDS;
		final String createdBy = "Mocked person name";

		//when
		when(shoppingCartService.insertShoppingCartRecords(customerEcifId, shoppingCartRecords, createdBy,
				CustomerProfileType.PERSONAL)).thenReturn(Optional.empty());
		ResponseEntity<Object> actual = shoppingCartController.addShoppingCartRecords(customerEcifId,
				createdBy, CustomerProfileType.PERSONAL, shoppingCartRecords);

		//then
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, actual.getStatusCode());
		assertNotNull(actual.getBody());
		verify(shoppingCartService, times(1)).insertShoppingCartRecords(customerEcifId,
				shoppingCartRecords, createdBy, CustomerProfileType.PERSONAL);
	}


	@Test
	public void when_updatingExistingShoppingCart_Expect_ShoppingCartDocument_And_HttpStatusOK() {
		//given
		String customerEcifId = "5628504543";
		String productIds = "4325435,756765876,56645654,5435,8769679,543424654";
		CustomerProducts expectedResult = SHOPPING_CART_DOCUMENT;
		final String modifiedBy = "Mocked person name";

		//when
		when(shoppingCartService.updateShoppingCartRecords(customerEcifId, productIds, "Mocked person name")).thenReturn(
				Optional.of(expectedResult));
		ResponseEntity<CustomerProducts> actual = shoppingCartController.updateExistingShoppingCart(customerEcifId,
				modifiedBy, productIds);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals(expectedResult, actual.getBody());
		verify(shoppingCartService, times(1)).updateShoppingCartRecords(customerEcifId, productIds,
				modifiedBy);
	}

	@Test
	public void when_updatingExistingShoppingCart_Expect_HttpStatusNotAllowed() {
		//given
		String customerEcifId = "5628504543";
		List<ProductModel> shoppingCartRecords = SHOPPING_CART_RECORDS;
		final String createdBy = "Mocked person name";
		final String customerProfileType = "Personal";

		//when
		when(shoppingCartService.insertShoppingCartRecords(customerEcifId, shoppingCartRecords, createdBy,
				CustomerProfileType.PERSONAL)).thenReturn(Optional.empty());
		ResponseEntity<Object> actual = shoppingCartController.addShoppingCartRecords(customerEcifId,
				createdBy, CustomerProfileType.PERSONAL, shoppingCartRecords);

		//then
		assertEquals(HttpStatus.METHOD_NOT_ALLOWED, actual.getStatusCode());
		assertNotNull(actual.getBody());
		verify(shoppingCartService, times(1)).insertShoppingCartRecords(customerEcifId,
				shoppingCartRecords, createdBy, CustomerProfileType.PERSONAL);
	}

	@Test
	public void when_deletingShoppingCartRecords_Expect_HttpStatusOK() {
		//given
		String customerEcifId = "5628504543";

		//when
		when(shoppingCartService.deleteByCustomerId(customerEcifId)).thenReturn(Boolean.TRUE);
		ResponseEntity<CustomerProducts> actual = shoppingCartController.deleteShoppingCartRecords(customerEcifId);

		//then
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		verify(shoppingCartService, times(1)).deleteByCustomerId(customerEcifId);
	}

	@Test
	public void when_deletingShoppingCartRecords_Expect_HttpStatusNotFound() {
		//given
		String customerEcifId = "5628504543";

		//when
		when(shoppingCartService.deleteByCustomerId(customerEcifId)).thenReturn(Boolean.FALSE);
		ResponseEntity<CustomerProducts> actual = shoppingCartController.deleteShoppingCartRecords(customerEcifId);

		//then
		assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
		assertNull(actual.getBody());
		verify(shoppingCartService, times(1)).deleteByCustomerId(customerEcifId);
	}
}
