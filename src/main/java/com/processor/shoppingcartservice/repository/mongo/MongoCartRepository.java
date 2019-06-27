package com.processor.shoppingcartservice.repository.mongo;

import com.processor.shoppingcartservice.document.mongo.MongoCartDocument;
import org.mapstruct.Named;
import org.springframework.data.mongodb.repository.MongoRepository;

@Named("mongoCartRepository")
public interface MongoCartRepository extends MongoRepository<MongoCartDocument, String> {
}
