package com.digitaltwin.backend.controller;

import com.digitaltwin.backend.model.TwinChat;
import com.digitaltwin.backend.service.TwinChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/twin/chat")
@CrossOrigin
public class TwinChatController {

    @Autowired
    private TwinChatService twinChatService;

    // Save a chat entry manually (before adding AI)
    @PostMapping
    public TwinChat saveChat(@RequestBody TwinChat chat) {
        return twinChatService.save(
                chat.getUserId(),
                chat.getQuestion(),
                chat.getAiResponse()
        );
    }

    // Get chat history
    @GetMapping("/{userId}")
    public List<TwinChat> getChatHistory(@PathVariable String userId) {
        return twinChatService.getChatHistory(userId);
    }
}
