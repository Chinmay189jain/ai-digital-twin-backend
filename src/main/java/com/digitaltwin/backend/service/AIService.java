package com.digitaltwin.backend.service;

import com.digitaltwin.backend.model.AIRequest;
import com.digitaltwin.backend.model.AIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static com.digitaltwin.backend.util.PromptTemplate.*;
import java.util.List;

@Service
public class AIService {

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

        AIRequest request = new AIRequest(
            "llama3-70b-8192",
                List.of(
                        new AIRequest.Message(ROLE_SYSTEM, PROFILE_GENERATION_CONTEXT),
                        new AIRequest.Message(ROLE_USER, PROFILE_PROMPT_PREFIX + prompt + PROFILE_PROMPT_SUFFIX)
                ),
            0.7
        );

        try {
            AIResponse response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("OpenAI Error: " + errorBody)))
                    )
                    .bodyToMono(AIResponse.class)
                    .block();

            return response.getChoices().get(0).getMessage().getContent();
        } catch (WebClientResponseException.TooManyRequests e) {
            throw new RuntimeException("Rate limit exceeded. Try again later.");
        } catch (Exception e) {
            throw new RuntimeException("Error during OpenAI call: " + e.getMessage());
        }
    }

    public String respondAsTwin(String twinIdentity, String userQuestion) {
        AIRequest request = new AIRequest(
                "llama3-70b-8192",
                List.of(
                        new AIRequest.Message(ROLE_SYSTEM, SYSTEM_TWIN_CONTEXT),
                        new AIRequest.Message(ROLE_USER, TWIN_USER_IDENTITY_PREFIX + twinIdentity),
                        new AIRequest.Message(ROLE_USER, USER_TWIN_INSTRUCTIONS + userQuestion)
                ),
                0.7
        );

        try {
            AIResponse response = webClient.post()
                    .uri("/openai/v1/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("OpenAI Error: " + errorBody)))
                    )
                    .bodyToMono(AIResponse.class)
                    .block();

            return response.getChoices().get(0).getMessage().getContent();
        } catch (WebClientResponseException.TooManyRequests e) {
            throw new RuntimeException("Rate limit exceeded. Try again later.");
        } catch (Exception e) {
            throw new RuntimeException("Error during OpenAI call: " + e.getMessage());
        }
    }
}
