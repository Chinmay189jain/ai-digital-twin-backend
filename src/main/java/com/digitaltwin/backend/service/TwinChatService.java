package com.digitaltwin.backend.service;

import com.digitaltwin.backend.model.TwinChat;
import com.digitaltwin.backend.repository.TwinChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TwinChatService {

    @Autowired
    private TwinChatRepository twinChatRepository;

    public TwinChat save(String userId, String question, String aiResponse) {
        TwinChat twinChat = TwinChat.builder()
                .userId(userId)
                .question(question)
                .aiResponse(aiResponse)
                .timestamp(LocalDateTime.now())
                .build();

        return twinChatRepository.save(twinChat);
    }

    public List<TwinChat> getChatHistory(String userId) {
        return twinChatRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
