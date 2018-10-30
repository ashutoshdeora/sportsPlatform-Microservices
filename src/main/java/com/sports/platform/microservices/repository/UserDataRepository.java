package com.sports.platform.microservices.repository;

import com.sports.platform.microservices.domain.UserData;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the UserData entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserDataRepository extends MongoRepository<UserData, String> {

}
