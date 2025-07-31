package com.digitaltwin.backend.service;

import com.digitaltwin.backend.dto.AIRequest;
import com.digitaltwin.backend.dto.AIResponse;
import com.digitaltwin.backend.model.TwinChat;
import com.digitaltwin.backend.model.TwinChatResponse;
import com.digitaltwin.backend.model.TwinProfile;
import com.digitaltwin.backend.repository.TwinChatRepository;
import com.digitaltwin.backend.repository.TwinProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static com.digitaltwin.backend.util.ConstantsTemplate.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIService {

    @Autowired
    private TwinProfileRepository twinProfileRepository;

    @Autowired
    private TwinChatRepository twinChatRepository;

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    public final WebClient webClient;

    public AIService(@Value("${grok.api.url}") String apiUrl,
                     @Value("${grok.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String generateTwinProfile(List<String> userAnswers) {

        String prompt = String.join(" ", userAnswers);

        // Create the AIRequest object with the system context and user prompt
        AIRequest request = new AIRequest(
            AI_MODEL,
                List.of(
                        new AIRequest.Message(ROLE_SYSTEM, PROFILE_GENERATION_CONTEXT),
                        new AIRequest.Message(ROLE_USER, PROFILE_PROMPT_PREFIX + prompt + PROFILE_PROMPT_SUFFIX)
                ),
            0.7
        );

        try {
            // Make the API call to Grok AI to generate the profile summary
            AIResponse response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("GrokAI Error: " + errorBody)))
                    )
                    .bodyToMono(AIResponse.class)
                    .block();

            if(response!=null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                // Extract the generated profile summary from the response
                String profileSummary = response.getChoices().getFirst().getMessage().getContent();
                logger.info("Generated profile summary: {}", profileSummary);

                // Create a new TwinProfile object with the generated summary
                TwinProfile profile = TwinProfile.builder()
                        .userId(getCurrentUserEmail()) // Replace with actual user ID
                        .profileSummary(profileSummary)
                        .createdAt(java.time.LocalDateTime.now())
                        .build();

                // Save the generated profile to the database
                twinProfileRepository.save(profile);

                return profileSummary;
            } else{
                logger.error("Empty or null response from grok AI while generating profile");
                throw new RuntimeException("Failed to generate twin profile");
            }

        } catch (WebClientResponseException.TooManyRequests e) {
            throw new RuntimeException("Rate limit exceeded. Try again later." + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error during Grok api call: " + e.getMessage());
        }
    }

    public String respondAsTwin(String userQuestion) {

        TwinProfile profile = twinProfileRepository.findByUserId(getCurrentUserEmail())
                .orElseThrow(() -> new RuntimeException("Twin profile not found"));

        // Create the AIRequest object with the system context and user question
        AIRequest request = new AIRequest(
                AI_MODEL,
                List.of(
                        new AIRequest.Message(ROLE_SYSTEM, SYSTEM_TWIN_CONTEXT),
                        new AIRequest.Message(ROLE_USER, TWIN_USER_IDENTITY_PREFIX + profile.getProfileSummary()),
                        new AIRequest.Message(ROLE_USER, USER_TWIN_INSTRUCTIONS + userQuestion)
                ),
                0.7
        );

        try {
            // Make the API call to Grok AI to get the response for user question
            AIResponse response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("GrokAI Error: " + errorBody)))
                    )
                    .bodyToMono(AIResponse.class)
                    .block();

            if(response!=null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                // Extract the generated answer from the response
                String answer = response.getChoices().getFirst().getMessage().getContent();
                logger.info("Generated answer from user question: {}", answer);

                TwinChat chat = TwinChat.builder()
                        .userId(getCurrentUserEmail()) // temporary until JWT is added
                        .question(userQuestion)
                        .aiResponse(answer)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();

                // Save the chat interaction to the database
                twinChatRepository.save(chat);

                return answer;
            } else{
                logger.error("Empty or null response from grok AI while generating answer");
                throw new RuntimeException("Failed to generate answer for user question");
            }

        } catch (WebClientResponseException.TooManyRequests e) {
            throw new RuntimeException("Rate limit exceeded. Try again later.");
        } catch (Exception e) {
            throw new RuntimeException("Error during Grok api call: " + e.getMessage());
        }
    }

    // This method retrieves the chat history for the current user
    public List<TwinChatResponse> getTwinChatHistory() {
        String email = getCurrentUserEmail();

        List<TwinChat> chats = twinChatRepository.findByUserIdOrderByTimestampDesc(email);

        return chats.stream()
                .map(chat -> new TwinChatResponse(chat.getQuestion(), chat.getAiResponse(), chat.getTimestamp()))
                .collect(Collectors.toList());
    }

    // This method retrieves the current user's email from the security context
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // This gives the email from JWT
    }
}
