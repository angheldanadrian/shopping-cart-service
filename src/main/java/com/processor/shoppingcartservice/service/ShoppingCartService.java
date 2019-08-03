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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

	private final String DATE_TIME_PATTERN = "yyy-MM-dd HH:mm";
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

	public List<CustomerProducts> findAll() {
		log.info("Getting all shopping cart records");

		return mongoCartRepository.findAll();
	}

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

	public Optional<CustomerProducts> updateShoppingCartRecords(final String customerEcifId,
                                                                final List<ProductModel> newProducts,
																final String modifiedBy) {
		if (customerEcifId == null) {
            log.warn("Failed to update the shopping cart records due to the fact that there is no cart assigned to the " +
                    "following customerId: {}", customerEcifId);
            return Optional.empty();
		}

		CustomerProducts customerProducts = mongoCartRepository.findByCustomerEcifId(customerEcifId);

        if (customerProducts == null) {
            log.error("The shopping cart with customerEcifId {} does not exists!", customerEcifId);
            return Optional.empty();
        } else {

            mongoCartRepository.delete(customerProducts);

            final List<Product> products = new ArrayList<>();
            products.addAll(customerProducts.getProducts());
            products.addAll(mapNewProducts(newProducts, modifiedBy));

            customerProducts.setProducts(products);

            customerProducts.setModifiedBy(modifiedBy);
            customerProducts.setModifiedDate(formatter.format(LocalDateTime.now()));

            log.info("Successfully updated the cart records");

            return Optional.of(mongoCartRepository.insert(customerProducts));
        }
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

    private List<Product> mapNewProducts(final List<ProductModel> newProductList, final String createdBy) {
	    return newProductList.stream().map(modelProduct -> Product.builder()
				.productCode(modelProduct.getProductCode())
				.productName(modelProduct.getProductName())
				.productBundleCode(modelProduct.getProductBundleCode())
				.productCategory(modelProduct.getProductCategory())
				.createdBy(createdBy)
				.productStatus(ShoppingCartStatus.OPEN.name())
				.id(UUID.randomUUID().toString())
				.createdDate(formatter.format(LocalDateTime.now()))
				.closedBy("")
				.closedDate("")
				.build()).collect(Collectors.toList());
    }

	private Optional<CustomerProducts> insertMongoCartDocument(final List<ProductModel> productModels,
															   final String createdBy,
															   final CustomerProfileType customerProfileType,
															   final String customerEcifId) {

		CustomerProducts newCartDocument = CustomerProducts.builder()
				.shopCartStatus(ShoppingCartStatus.OPEN)
				.customerEcifId(customerEcifId)
				.customerProfileType(customerProfileType)
				.createdDate(formatter.format(LocalDateTime.now()))
				.createdBy(createdBy)
				.modifiedDate(formatter.format(LocalDateTime.now()))
				.modifiedBy(createdBy)
				.products(collectProductDocument(productModels, createdBy))
				.build();

		return Optional.of(mongoCartRepository.insert(newCartDocument));
	}

	private List<Product> collectProductDocument(final List<ProductModel> productModels, final String createdBy) {
		return productModels.stream().map(productModel -> Product.builder()
				.id(UUID.randomUUID().toString())
				.closedBy("")
				.closedDate("")
				.createdBy(createdBy)
				.createdDate(formatter.format(LocalDateTime.now()))
				.productBundleCode(productModel.getProductBundleCode() != null ? productModel.getProductBundleCode() : "")
				.productCategory(productModel.getProductCategory())
				.productCode(productModel.getProductCode())
				.productName(productModel.getProductName())
				.productStatus(ShoppingCartStatus.OPEN.name())
				.build()).collect(Collectors.toList());
	}
}
