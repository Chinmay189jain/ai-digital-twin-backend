package com.digitaltwin.backend.controller;

import com.digitaltwin.backend.dto.TwinAnswerResponse;
import com.digitaltwin.backend.dto.TwinQuestionRequest;
import com.digitaltwin.backend.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AIController {

    @Autowired
    private AIService aiService;

    //Generate twin profile based on user answers
    @PostMapping("/generate-profile")
    public String generateTwinProfile(@RequestBody Map<String, List<String>> request) {
        List<String> userAnswers = request.get("answers");
        return aiService.generateTwinProfile(userAnswers);
    }

    //Ask a question to the AI as a digital twin
    @PostMapping("/ask")
    public ResponseEntity<TwinAnswerResponse> respondAsTwin(@RequestBody TwinQuestionRequest twinQuestionRequest) {
        String answer = aiService.respondAsTwin(twinQuestionRequest.getTwinIdentity(), twinQuestionRequest.getUserQuestion());
        return ResponseEntity.ok(new TwinAnswerResponse(answer));
    }
}
