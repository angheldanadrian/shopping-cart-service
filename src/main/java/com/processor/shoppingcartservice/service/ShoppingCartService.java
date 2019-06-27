package com.processor.shoppingcartservice.service;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import com.processor.shoppingcartservice.document.mongo.MongoProductDocument;
import com.processor.shoppingcartservice.model.CustomerProfileType;
import com.processor.shoppingcartservice.model.ProductModel;
import com.processor.shoppingcartservice.model.ShoppingCartStatus;
import com.processor.shoppingcartservice.repository.mongo.MongoCartRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ShoppingCartService {

	@Qualifier("mongoCartRepository")
	@Autowired
	private MongoCartRepository mongoCartRepository;


	public List<MongoCartDocument> findAllByRDateOrCreatedDateOrEndDate(final String rdate, final String startdate,
																		final String endDate) {
		log.info("Search document by rdate: {}, startDate: {}, endDate: {}", rdate, startdate, endDate);

		return mongoCartRepository.findAllByRDateOrCreatedDateOrEndDate(rdate, startdate, endDate);
	}

	public Optional<MongoCartDocument> findById(final String customerId) {
		log.info("Search document by customerId: {}", customerId);

		return mongoCartRepository.findById(customerId);
	}

	public Optional<MongoCartDocument> insertOrUpdateShoppingCartRecords(final String shoppingCartId,
																		 final List<ProductModel> productModels) {
		if (shoppingCartId == null || productModels.size() == 0) {
			log.error("Failed to add records in the shopping cart. No shopping cart/products provided!");

		}
		log.info("Add records for the shopping cartId: {}", shoppingCartId);

		Optional<MongoCartDocument> cartDocument = mongoCartRepository.findById(shoppingCartId);
		if (cartDocument.isPresent()) {
			return updateMongoCartDocument(productModels, cartDocument);
		}

		return insertMongoCartDocument(productModels);
	}

	public void deleteByCustomerId(final String customerId) {
		//TODO
	}

	private Optional<MongoCartDocument> updateMongoCartDocument(final List<ProductModel> productModels,
																final Optional<MongoCartDocument> cartDocument) {
		mongoCartRepository.delete(cartDocument.get());

		MongoCartDocument updatedCartDocument = cartDocument.get();
		updatedCartDocument.setProducts(collectProductDocument(productModels));

		return Optional.of(mongoCartRepository.insert(updatedCartDocument));
	}

	private Optional<MongoCartDocument> insertMongoCartDocument(final List<ProductModel> productModels) {

		MongoCartDocument newCartDocument = MongoCartDocument.builder()
				.shopCartStatus(ShoppingCartStatus.OPEN.name())
				.customerEcifId(UUID.randomUUID().toString())
				.customerProfileType(CustomerProfileType.PERSONAL.name())
				.createdDate(LocalDateTime.now().toString())
				.endDate(LocalDateTime.now().plusMinutes(15).toString())
				.rDate(LocalDate.now().toString())
				.createdBy("loggedCustommer")
				.modifiedDate(LocalDateTime.now().toString())
				.modifiedBy("loggedCustommer")
				.products(collectProductDocument(productModels))
				.build();

		return Optional.of(mongoCartRepository.insert(newCartDocument));
	}

	private List<MongoProductDocument> collectProductDocument(List<ProductModel> productModels) {
		return productModels.stream().map(productModel -> MongoProductDocument.builder()
				.closedBy(productModel.getClosedBy())
				.closedDate(productModel.getClosedDate())
				.createdBy(productModel.getCreatedBy())
				.createdDate(productModel.getCreatedDate())
				.productBundleCode(productModel.getProductBundleCode())
				.productCategory(productModel.getProductCategory())
				.productCode(productModel.getProductCode())
				.productName(productModel.getProductName())
				.productStatus(productModel.getProductStatus())
				.build()).collect(Collectors.toList());
	}
}
