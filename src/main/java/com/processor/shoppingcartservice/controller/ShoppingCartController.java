package com.processor.shoppingcartservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShoppingCartController {

	@GetMapping("/status/health")
	public ResponseEntity<String> statusHealth() {
		return ResponseEntity.ok("OK");
	}
}
