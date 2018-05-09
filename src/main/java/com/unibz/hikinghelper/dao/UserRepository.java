package com.unibz.hikinghelper.dao;

import com.unibz.hikinghelper.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

	public User findByUsername(String name);

}