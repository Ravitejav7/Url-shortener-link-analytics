package com.example.urlshortener.repository;

import java.util.List;

import com.example.urlshortener.entity.ClickEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClickEventRepository extends MongoRepository<ClickEvent, String> {

    long countByCode(String code);

    List<ClickEvent> findTop20ByCodeOrderByClickedAtDesc(String code);
}
