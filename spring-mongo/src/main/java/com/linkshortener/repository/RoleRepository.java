package com.linkshortener.repository;

import com.linkshortener.entity.ERole;
import com.linkshortener.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}