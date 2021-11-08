package com.ailleron.translation.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MangoConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "javaboss";
    }

    @Override
    public @Bean MongoClient mongoClient() {
        return MongoClients.create("mongodb://javaboss:javaboss@mongo:27017/javaboss");
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
