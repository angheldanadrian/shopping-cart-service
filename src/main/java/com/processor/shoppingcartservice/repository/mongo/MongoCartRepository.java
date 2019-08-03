package com.processor.shoppingcartservice.repository.mongo;

import com.processor.shoppingcartservice.document.mongo.CustomerProducts;
import org.mapstruct.Named;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Named("mongoCartRepository")
public interface MongoCartRepository extends MongoRepository<CustomerProducts, String> {
	List<CustomerProducts> findAllByCreatedDate(String startdate);

	CustomerProducts findByCustomerEcifId(String customerEcifId);
}
