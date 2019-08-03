package com.processor.shoppingcartservice.service;

import com.processor.shoppingcartservice.document.mongo.CustomerProducts;
import com.processor.shoppingcartservice.document.mongo.Product;
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


	public List<CustomerProducts> findAllByRDateOrCreatedDateOrEndDate(final String rdate, final String startdate,
                                                                       final String endDate) {
		log.info("Search document by rdate: {}, startDate: {}, endDate: {}", rdate, startdate, endDate);

		return mongoCartRepository.findAllByRDateOrCreatedDateOrEndDate(rdate, startdate, endDate);
	}

	public Optional<CustomerProducts> findByCustomerId(final String customerId) {
		log.info("Search document by customerId: {}", customerId);

		CustomerProducts customerProducts = mongoCartRepository.findByCustomerEcifId(customerId);
		if (customerProducts == null) {
			log.warn("Failed to find the shopping cat by customerId: {}! There is no shopping cart available for the " +
					"given id.", customerId);
			return Optional.empty();
		}

		return Optional.of(customerProducts);
	}

	public Optional<CustomerProducts> insertShoppingCartRecords(final String customerEcifId,
																final List<ProductModel> productModels,
																final String createdBy,
																final CustomerProfileType customerProfileType) {
		if (customerEcifId == null || productModels.size() == 0) {
			log.error("Failed to add records in the shopping cart. No shopping cart customerEcifId/products provided!");
			return Optional.empty();
		}

		CustomerProducts customerProducts = mongoCartRepository.findByCustomerEcifId(customerEcifId);
		if (customerProducts != null && createdBy.equals(customerProducts.getCreatedBy())) {
			log.error("The shopping cart with customerEcifId {} already exists!", customerEcifId);
			return Optional.empty();
		}
		log.info("Add records for the shopping cartId: {}", customerEcifId);

		return insertMongoCartDocument(productModels, createdBy, customerProfileType, customerEcifId);
	}

	public Optional<CustomerProducts> updateShoppingCartRecords(final String customerEcifId, final String productIds,
																final String modifiedBy) {
		if (customerEcifId == null) {
			log.error("Failed to update the shopping cart. No valid customerEcifId provided!");

		}

		CustomerProducts cartDocument = mongoCartRepository.findByCustomerEcifId(customerEcifId);

		List<Product> products = new ArrayList<>();
		if (cartDocument != null) {
			mongoCartRepository.delete(cartDocument);
			updateProduct(productIds, products, cartDocument.getProducts());
			if (products.size() == 0) {
				log.warn("Failed to update the shopping cart! Given product codes are not present in the shopping cart!");
			}

			cartDocument.setProducts(products);
			cartDocument.setModifiedBy(modifiedBy);
			cartDocument.setModifiedDate(LocalDateTime.now().toString());
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

		CustomerProducts cartDocument = mongoCartRepository.findByCustomerEcifId(customerEcifId);
		if (cartDocument != null) {
			mongoCartRepository.delete(cartDocument);
			log.info("Successfully deleted the shopping cart by customerId: {}", customerEcifId);
			return true;
		}

		return false;
	}

	private void updateProduct(String productIds, List<Product> mongoProductDocuments,
							   List<Product> products) {
		split(productIds).stream().forEach(id -> mongoProductDocuments.addAll(products.stream().filter(prduct -> id.equals(prduct.getId()))
				.map(updatedProduct -> Product.builder()
						.productStatus("updated")
						.id(updatedProduct.getId())
						.productCode(updatedProduct.getProductCode())
						.productName(updatedProduct.getProductName())
						.productCategory(updatedProduct.getProductCategory())
						.productBundleCode(updatedProduct.getProductBundleCode())
						.createdBy(updatedProduct.getCreatedBy())
						.createdDate(updatedProduct.getCreatedDate())
						.closedDate(updatedProduct.getClosedDate())
						.closedBy(updatedProduct.getClosedBy())
						.build()).collect(Collectors.toList())));
	}

	private List<String> split(String str) {
		return Stream.of(str.split(","))
				.map(String::new)
				.collect(Collectors.toList());
	}

	private Optional<CustomerProducts> updateMongoCartDocument(final List<ProductModel> productModels,
                                                               final Optional<CustomerProducts> cartDocument) {
		mongoCartRepository.delete(cartDocument.get());

		CustomerProducts updatedCartDocument = cartDocument.get();
		updatedCartDocument.setProducts(collectProductDocument(productModels));

		return Optional.of(mongoCartRepository.insert(updatedCartDocument));
	}

	private Optional<CustomerProducts> insertMongoCartDocument(final List<ProductModel> productModels,
															   final String createdBy,
															   final CustomerProfileType customerProfileType,
															   final String customerEcifId) {
		CustomerProducts newCartDocument = CustomerProducts.builder()
				.shopCartStatus(ShoppingCartStatus.OPEN)
				.customerEcifId(customerEcifId)
				.customerProfileType(customerProfileType)
				.createdDate(LocalDateTime.now().toString())
				.endDate(LocalDateTime.now().plusMinutes(15).toString())
				.rDate(LocalDate.now().toString())
				.createdBy(createdBy)
				.modifiedDate(LocalDateTime.now().toString())
				.modifiedBy(createdBy)
				.products(collectProductDocument(productModels))
				.build();

		return Optional.of(mongoCartRepository.insert(newCartDocument));
	}

	private List<Product> collectProductDocument(List<ProductModel> productModels) {
		return productModels.stream().map(productModel -> Product.builder()
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
