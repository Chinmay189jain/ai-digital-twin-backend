package com.digitaltwin.backend.controller;

import com.digitaltwin.backend.dto.ChatSessionListItem;
import com.digitaltwin.backend.dto.TwinAnswerResponse;
import com.digitaltwin.backend.dto.TwinQuestionRequest;
import com.digitaltwin.backend.service.TwinChatService;
import com.digitaltwin.backend.service.TwinChatSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/twin")
public class ChatController {

    @Autowired
    TwinChatService twinChatService;

    @Autowired
    TwinChatSessionService twinChatSessionService;

    // Endpoint to get a response from the AI based on user input
    @PostMapping("/chat")
    public ResponseEntity<TwinAnswerResponse> getChatResponse(@RequestBody TwinQuestionRequest twinQuestionRequest) {
        TwinAnswerResponse answer = twinChatService.getResponse(twinQuestionRequest.getSessionId(), twinQuestionRequest.getUserQuestion());
        return ResponseEntity.ok(answer);
    }

    // Endpoint to get chat history with optional search query
    @GetMapping("/chat/sessions")
    public ResponseEntity<List<ChatSessionListItem>> getAllSessions(@RequestParam (required = false) String searchQuery) {
        return ResponseEntity.ok(twinChatSessionService.getAllSessions(searchQuery));
    }

    // Endpoint to get chat history for a specific session
    @GetMapping("/chat/{sessionId}")
    public ResponseEntity<List<TwinAnswerResponse>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(twinChatService.getChatHistory(sessionId));
    }

    @DeleteMapping("chat/session/{sessionId}" )
    public ResponseEntity<String> deleteChatSession(@PathVariable String sessionId) {
        twinChatService.deleteChatSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
