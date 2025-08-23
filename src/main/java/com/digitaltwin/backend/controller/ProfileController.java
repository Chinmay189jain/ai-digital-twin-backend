package com.digitaltwin.backend.controller;

import com.digitaltwin.backend.service.TwinProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class ProfileController {

    @Autowired
    private TwinProfileService twinProfileService;

    //Generate twin profile based on user answers
    @PostMapping("/generate-profile")
    public ResponseEntity<String> generateTwinProfile(@RequestBody Map<String, List<String>> request) {
        List<String> userAnswers = request.get("answers");
        return ResponseEntity.ok(twinProfileService.generateProfile(userAnswers));
    }

    // Get the digital twin profile
    @GetMapping("/get-profile")
    public ResponseEntity<String> getTwinProfile() {
        return ResponseEntity.ok(twinProfileService.getProfile());
    }
}
