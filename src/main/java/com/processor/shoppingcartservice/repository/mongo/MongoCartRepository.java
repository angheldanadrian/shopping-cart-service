package com.processor.shoppingcartservice.repository.mongo;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import org.mapstruct.Named;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Named("mongoCartRepository")
public interface MongoCartRepository extends MongoRepository<MongoCartDocument, String> {
	List<MongoCartDocument> findAllByRDateOrCreatedDateOrEndDate(String rdate, String startdate, String endDate);
}
