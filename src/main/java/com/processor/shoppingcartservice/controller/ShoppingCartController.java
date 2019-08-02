package com.processor.shoppingcartservice.controller;

import com.processor.shoppingcartservice.document.mongo.CustomerProducts;
import com.processor.shoppingcartservice.model.ProductModel;
import com.processor.shoppingcartservice.service.ShoppingCartService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ShoppingCartController {

    private ShoppingCartService shoppingCartService;

    @Autowired
    public ShoppingCartController(final ShoppingCartService shoppingCartService) {
        this.shoppingCartService = Objects.requireNonNull(shoppingCartService, "shoppingCartService must not be null!");
    }

    @GetMapping(path = "/shopping-cart")
    @ApiOperation(value = "Filter customer shopping cart records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rdate", value = "A relative date range for the report, such as Today or LastWeek. " +
                    "For an exact range, use start_date and end_date instead.", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "start_date", value = "The start date for the report. Must be used together with end_date. " +
                    "This parameter is incompatible with rdate.", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "end_date", value = "The end date for the report. Must be used together with start_date. " +
                    "This parameter is incompatible with rdate.", dataType = "string", paramType = "query")
    })
    public List<CustomerProducts> filterCustomerShoppingCartRecords(@RequestParam(required = false) final String rdate,
                                                                    @RequestParam(required = false) final String startdate,
                                                                    @RequestParam(required = false) final String endDate) {

        return shoppingCartService.findAllByRDateOrCreatedDateOrEndDate(rdate, startdate, endDate);
    }

    @GetMapping(path = "/shopping-cart/{customerEcifId}")
    @ApiOperation(value = "Finds customer shopping cart record by customer ECIF ID")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "customerEcifId",
                    value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
                    dataType = "string", paramType = "path")
    })
    public ResponseEntity<CustomerProducts> findByCustomerId(@PathVariable final String customerEcifId) {

        Optional<CustomerProducts> doc = shoppingCartService.findByCustomerId(customerEcifId);

        if (doc.isPresent()) {
            return ResponseEntity.ok(doc.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping(path = "/shopping-cart/{shoppingCartId}")
    @ApiOperation(value = "Adds shopping cart records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shoppingCartId",
                    value = "Unique ID of a shopping cart", required = true,
                    dataType = "string", paramType = "path")
    })
    public ResponseEntity<CustomerProducts> addShoppingCartRecords(@PathVariable final String shoppingCartId,
                                                                   @RequestBody final List<ProductModel> productModels) {

        Optional<CustomerProducts> doc = shoppingCartService.insertOrUpdateShoppingCartRecords(shoppingCartId, productModels);

        if (doc.isPresent()) {
            return ResponseEntity.ok(doc.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping(path = "/shopping-cart/{customerEcifId}")
    @ApiOperation(value = "Updates an existing customer shopping cart record")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "customerEcifId",
                    value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
                    dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "productIds", value = "productIds separated by comma", dataType = "string", paramType = "query")
    })
    public ResponseEntity<CustomerProducts> updateExistingShoppingCart(@PathVariable final String customerEcifId,
                                                                       @RequestParam final String productIds) {

        Optional<CustomerProducts> doc = shoppingCartService.updateShoppingCartRecords(customerEcifId, productIds);

        if (doc.isPresent()) {
            return ResponseEntity.ok(doc.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/shopping-cart/{customerEcifId}")
    @ApiOperation(value = "Deletes customer shopping cart record")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "customerEcifId",
                    value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
                    dataType = "string", paramType = "path")
    })
    public ResponseEntity<CustomerProducts> deleteShoppingCartRecords(@PathVariable final String customerEcifId) {

        Boolean succeed = shoppingCartService.deleteByCustomerId(customerEcifId);
        if (succeed == false) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
