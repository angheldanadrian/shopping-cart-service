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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public Optional<MongoCartDocument> findByCustomerId(final String customerId) {
		log.info("Search document by customerId: {}", customerId);

		MongoCartDocument mongoCartDocument = mongoCartRepository.findByCustomerEcifId(customerId);
		if (mongoCartDocument == null) {
			log.warn("Failed to find the shopping cat by customerId: {}! There is no shopping cart available for the " +
					"given id.", customerId);
			return Optional.empty();
		}

		return Optional.of(mongoCartDocument);
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

	public Optional<MongoCartDocument> updateShoppingCartRecords(final String customerEcifId, final String productIds) {
		if (customerEcifId == null) {
			log.error("Failed to update the shopping cart. No valid customerEcifId provided!");

		}

		MongoCartDocument cartDocument = mongoCartRepository.findByCustomerEcifId(customerEcifId);

		List<MongoProductDocument> mongoProductDocuments = new ArrayList<>();
		if (cartDocument != null) {
			List<MongoProductDocument> products = cartDocument.getProducts();
			split(productIds).stream().forEach(id -> {
				mongoProductDocuments.addAll(products.stream().filter(prduct -> id.equals(prduct.getId()))
						.map(updatedProduct -> MongoProductDocument.builder()
								.productStatus("updated")
								.build()).collect(Collectors.toList()));
			});

			if (mongoProductDocuments.size() == 0) {
				log.warn("Failed to update the shopping cart! Given product codes are not present in the shopping cart!");
			}

			cartDocument.setProducts(mongoProductDocuments);
			log.info("Successfully updated the cart records");

			return Optional.of(mongoCartRepository.insert(cartDocument));
		}

		log.warn("Failed to update the shopping cart records due to the fact that there is no cart assigned to the " +
				"following customerId: {}", customerEcifId);
		return Optional.empty();
	}

	public Boolean deleteByCustomerId(final String customerEcifId) {
		if (customerEcifId == null) {
			log.info("Failed to deleted the shopping cart by customerId: {}", customerEcifId);
			return false;
		}

		MongoCartDocument cartDocument = mongoCartRepository.findByCustomerEcifId(customerEcifId);
		if (cartDocument != null) {
			mongoCartRepository.delete(cartDocument);
			log.info("Successfully deleted the shopping cart by customerId: {}", customerEcifId);
			return true;
		}

		return false;
	}

	private List<String> split(String str) {
		return Stream.of(str.split(","))
				.map(String::new)
				.collect(Collectors.toList());
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
				.id(UUID.randomUUID().toString())
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
