package com.processor.shoppingcartservice.controller;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import com.processor.shoppingcartservice.repository.mongo.MongoCartRepository;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ShoppingCartController {

	@Qualifier("mongoCartRepository")
	@Autowired
	private MongoCartRepository mongoCartRepository;

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

		return mongoCartRepository.findAllByRDateOrCreatedDateOrEndDate(rdate, startdate, endDate);
	}

	@GetMapping(path = "/shopping-cart/{customerEcifId}")
	@ApiOperation(value = "Finds customer shopping cart record by customer ECIF ID", response = ResponseEntity.class)
	@ApiImplicitParams({@ApiImplicitParam(name = "customerEcifId",
			value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
			dataType = "string", paramType = "path")
	})
	public ResponseEntity<MongoCartDocument> findByCustomerId(@PathVariable final String customerEcifId) {

		Optional<MongoCartDocument> doc = mongoCartRepository.findById(customerEcifId);

		if (doc.isPresent()) {
			return ResponseEntity.ok(doc.get());
		}

		return ResponseEntity.notFound().build();
	}
}
