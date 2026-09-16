package com.hig.inovagab.repository;

import com.hig.inovagab.model.Strategy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StrategyRepository extends MongoRepository<Strategy, String> {
}
