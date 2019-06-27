package com.processor.shoppingcartservice.configuration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.processor.shoppingcartservice.repository.mongo")
@Configuration
public class MongoDbConfig {

	@Value("${application.config.mongo.host}")
	private String mongoHost;

	@Value("${application.config.mongo.database}")
	private String mongoDatabase;

	@Bean
	public MongoClient mongo() {
		return new MongoClient(new MongoClientURI(mongoHost));
	}

	@Bean
	public MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongo(), mongoDatabase);
	}
}
