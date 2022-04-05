package com.test.persistence.repository;

import com.test.data.NeuronGraph;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NNDescriptionRepository extends MongoRepository<NeuronGraph, String> {
}
