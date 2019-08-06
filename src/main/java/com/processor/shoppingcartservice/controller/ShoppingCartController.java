package com.processor.shoppingcartservice.controller;

import com.processor.shoppingcartservice.document.mongo.CustomerProducts;
import com.processor.shoppingcartservice.model.CustomerProfileType;
import com.processor.shoppingcartservice.model.ProductModel;
import com.processor.shoppingcartservice.service.ShoppingCartService;
import com.processor.shoppingcartservice.utils.ApiError;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    @ApiOperation(value = "Get all records")
    public List<CustomerProducts> getAllShoppingCartRecords() {

        return shoppingCartService.findAll();
    }

    @GetMapping(path = "/shopping-cart/filter")
    @ApiOperation(value = "Filter customer shopping cart records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startdate", value = "The start date for the report. Must be used together with end_date. "
                    , dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endDate", value = "The end date for the report. Must be used together with start_date. "
                    , dataType = "string", paramType = "query")
    })
    public List<CustomerProducts> filterCustomerShoppingCartRecords(@RequestParam(required = false) final String startdate,
                                                                    @RequestParam(required = false) final String endDate) {

        return shoppingCartService.findAllByCreatedDateOrEndDate(startdate, endDate);
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

    @PostMapping(path = "/shopping-cart")
    @ApiOperation(value = "Adds shopping cart records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "customerEcifId", value = "The customer Ecif Id", required = true,
                    dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "createdBy", value = "The person name that is creating the shopping cart",
                    required = true, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "customerProfileType", value = "The customer profile type",
                    dataType = "string", paramType = "query")
    })
    public ResponseEntity<Object> addShoppingCartRecords(@RequestParam final String customerEcifId,
                                                         @RequestParam final String createdBy,
                                                         @RequestParam final CustomerProfileType customerProfileType,
                                                         @RequestBody final List<ProductModel> productModels) {
        Optional<CustomerProducts> doc = shoppingCartService.insertShoppingCartRecords(customerEcifId,
                productModels, createdBy, customerProfileType);

        if (doc.isPresent()) {
            return ResponseEntity.ok(doc.get());
        }

        String errorMessage = "There already is a shopping cart with customerEcifId=" + customerEcifId + " for the " +
                "customer: " + createdBy;
        ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, errorMessage, errorMessage);
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @PutMapping(path = "/shopping-cart/{customerEcifId}")
    @ApiOperation(value = "Updates an existing customer shopping cart record")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "customerEcifId",
                    value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
                    dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "modifiedBy", value = "The person name that is modifying the shopping cart",
                    dataType = "string", paramType = "query")
    })
    public ResponseEntity<CustomerProducts> updateExistingShoppingCart(@PathVariable final String customerEcifId,
                                                                       @RequestParam final String modifiedBy,
                                                                       @RequestBody final List<ProductModel> productModels) {

        final Optional<CustomerProducts> customerProducts = shoppingCartService.updateShoppingCartRecords(customerEcifId,
                productModels, modifiedBy);
        if (customerProducts.isPresent()) {
            return ResponseEntity.ok(customerProducts.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping(path = "/shopping-cart/checkout/{customerEcifId}")
    @ApiOperation(value = "Customer shopping cart checkout")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "customerEcifId",
                    value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
                    dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "modifiedBy", value = "The person name that is modifying the shopping cart",
                    dataType = "string", paramType = "query")
    })
    public ResponseEntity<CustomerProducts> shoppingCartCheckout(@PathVariable final String customerEcifId,
                                                                 @RequestParam final String modifiedBy,
                                                                 @RequestBody final List<String> productIds) {
        final Optional<CustomerProducts> customerProducts = shoppingCartService.shoppingCartCheckout(customerEcifId,
                modifiedBy, productIds);

        if (customerProducts.isPresent()) {
            return ResponseEntity.ok(customerProducts.get());
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping(path = "/shopping-cart/products/{customerEcifId}")
    @ApiOperation(value = "Deletes customer shopping cart records")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "customerEcifId",
                    value = "Unique ID that is common between Customer Connect and Needs Navigator", required = true,
                    dataType = "string", paramType = "path"),
            @ApiImplicitParam(name = "modifiedBy", value = "The person name that is modifying the shopping cart",
                    dataType = "string", paramType = "query")
    })
    public ResponseEntity<CustomerProducts> deleteShoppingCartRecords(@PathVariable final String customerEcifId,
                                                                      @RequestParam final String modifiedBy,
                                                                      @RequestBody final List<String> productIds) {
        final Optional<CustomerProducts> customerProducts = shoppingCartService.deleteShoppingCartRecordsByCustomerId(
                customerEcifId, modifiedBy, productIds);
        if (customerProducts.isPresent()) {
            return ResponseEntity.ok(customerProducts.get());
        }

        return ResponseEntity.notFound().build();
    }
}
