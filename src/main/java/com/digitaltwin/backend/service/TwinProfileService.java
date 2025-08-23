package com.digitaltwin.backend.service;

import com.digitaltwin.backend.model.TwinProfile;
import com.digitaltwin.backend.repository.TwinProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TwinProfileService {

    @Autowired
    private TwinProfileRepository twinProfileRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private UserService userService;

    public String generateProfile(List<String> userAnswers) {
        try{
            // Generate the profile summary using AIService
            String profileSummary = aiService.generateTwinProfile(userAnswers);

            // Create a new TwinProfile object with the generated summary
            TwinProfile profile = TwinProfile.builder()
                    .userId(userService.getCurrentUserEmail()) // Replace with actual user ID
                    .profileSummary(profileSummary)
                    .createdAt(java.time.LocalDateTime.now())
                    .build();

            // Save the generated profile to the database
            twinProfileRepository.save(profile);

            return  profileSummary;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate twin profile: " + e.getMessage(), e);
        }
    }

    public String getProfile() {
        try{
            TwinProfile profile = twinProfileRepository.findByUserId(userService.getCurrentUserEmail())
                    .orElseThrow(() -> new RuntimeException("Twin profile not found"));
            return profile.getProfileSummary();
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve twin profile: " + e.getMessage(), e);
        }
    }
}
