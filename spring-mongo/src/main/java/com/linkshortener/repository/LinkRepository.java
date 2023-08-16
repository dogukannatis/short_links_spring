package com.linkshortener.repository;

import com.linkshortener.entity.Link;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LinkRepository extends MongoRepository<Link, String> {



}
