package com.ussddev.ussdproject.repository;

import com.ussddev.ussdproject.model.UssdSession;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends MongoRepository<UssdSession, String> {

    Optional<UssdSession> findByMsisdn(String msisdn);

    List<UssdSession> findByExpirationDateBefore(LocalDateTime date);

}
