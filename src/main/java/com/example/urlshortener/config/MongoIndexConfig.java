package com.example.urlshortener.config;

import com.example.urlshortener.entity.ClickEvent;
import com.example.urlshortener.entity.UrlMapping;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Configuration
public class MongoIndexConfig {

    @Bean
    CommandLineRunner ensureMongoIndexes(MongoTemplate mongoTemplate) {
        return args -> {
            mongoTemplate.indexOps(UrlMapping.class)
                    .createIndex(new Index().on("code", Sort.Direction.ASC).unique());
            mongoTemplate.indexOps(UrlMapping.class)
                    .createIndex(new Index().on("normalizedUrl", Sort.Direction.ASC));
            mongoTemplate.indexOps(ClickEvent.class)
                    .createIndex(new Index().on("code", Sort.Direction.ASC));
            mongoTemplate.indexOps(ClickEvent.class)
                    .createIndex(new Index()
                            .on("code", Sort.Direction.ASC)
                            .on("clickedAt", Sort.Direction.DESC));
        };
    }
}
