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
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ShoppingCartController {

	@Qualifier("mongoCartRepository")
	@Autowired
	private MongoCartRepository mongoCartRepository;

	@GetMapping(path = "/shopping-cart/{customerEcifId}")
	@ApiOperation(value = "Finds customer shopping cart record by customer ECIF ID", response = ResponseEntity.class)
	@ApiImplicitParams({@ApiImplicitParam(name = "customerEcifId",
			value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
			dataType = "string", paramType = "path")
	})
	public ResponseEntity<MongoCartDocument> findByCustomerId(@PathVariable String customerEcifId) {

		Optional<MongoCartDocument> doc = mongoCartRepository.findById(customerEcifId);

		if (doc.isPresent()) {
			return ResponseEntity.ok(doc.get());
		}

		return ResponseEntity.notFound().build();
	}
}
