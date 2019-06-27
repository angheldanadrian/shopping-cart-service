package com.processor.shoppingcartservice.controller;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import com.processor.shoppingcartservice.model.ProductModel;
import com.processor.shoppingcartservice.service.ShoppingCartService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class ShoppingCartController {

	@Autowired
	private ShoppingCartService shoppingCartService;

	@GetMapping(path = "/shopping-cart")
	@ApiOperation(value = "Filter customer shopping cart records", response = ResponseEntity.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "rdate", value = "A relative date range for the report, such as Today or LastWeek. " +
					"For an exact range, use start_date and end_date instead.", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "start_date", value = "The start date for the report. Must be used together with end_date. " +
					"This parameter is incompatible with rdate.", dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "end_date", value = "The end date for the report. Must be used together with start_date. " +
					"This parameter is incompatible with rdate.", dataType = "string", paramType = "query")
	})
	public List<MongoCartDocument> filterCustomerShoppingCartRecords(@RequestParam(required = false) final String rdate,
																	 @RequestParam(required = false) final String startdate,
																	 @RequestParam(required = false) final String endDate) {

		return shoppingCartService.findAllByRDateOrCreatedDateOrEndDate(rdate, startdate, endDate);
	}

	@GetMapping(path = "/shopping-cart/{customerEcifId}")
	@ApiOperation(value = "Finds customer shopping cart record by customer ECIF ID", response = ResponseEntity.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "customerEcifId",
					value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
					dataType = "string", paramType = "path")
	})
	public ResponseEntity<MongoCartDocument> findByCustomerId(@PathVariable final String customerEcifId) {

		Optional<MongoCartDocument> doc = shoppingCartService.findById(customerEcifId);

		if (doc.isPresent()) {
			return ResponseEntity.ok(doc.get());
		}

		return ResponseEntity.notFound().build();
	}

	@PostMapping(path = "/shopping-cart/{shoppingCartId}")
	@ApiOperation(value = "Adds shopping cart records", response = ResponseEntity.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "shoppingCartId",
					value = "Unique ID of a shopping cart", required = true,
					dataType = "string", paramType = "path")
	})
	public ResponseEntity<MongoCartDocument> addShoppingCartRecords(@PathVariable final String shoppingCartId,
																	@Valid final List<ProductModel> productModels) {

		Optional<MongoCartDocument> doc = shoppingCartService.insertOrUpdateShoppingCartRecords(shoppingCartId, productModels);

		if (doc.isPresent()) {
			return ResponseEntity.ok(doc.get());
		}

		return ResponseEntity.notFound().build();
	}

	@DeleteMapping(path = "/shopping-cart/{customerEcifId}")
	@ApiOperation(value = "Deletes customer shopping cart record", response = ResponseEntity.class)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "customerEcifId",
					value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
					dataType = "string", paramType = "path")
	})
	public ResponseEntity addShoppingCartRecords(@PathVariable final String customerEcifId) {

		Boolean succeed = shoppingCartService.deleteByCustomerId(customerEcifId);
		if (succeed == false) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok().build();
	}
}
