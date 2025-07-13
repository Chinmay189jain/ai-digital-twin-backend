package com.digitaltwin.backend.repository;

import com.digitaltwin.backend.model.TwinChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TwinChatRepository extends MongoRepository<TwinChat, String> {

    // Custom query to find a TwinChat by userId
    List<TwinChat> findByUserIdOrderByTimestampDesc(String userId);

}
