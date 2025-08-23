package com.digitaltwin.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("twin_chat_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name = "user_updated_idx", def = "{'userId': 1, 'updatedAt': -1}")
public class TwinChatSession {

    @Id
    private String id;

    private String userId;     // unique identifier for the chat session, used in URLs and to group messages together

    private String title;             // taken from the first user message (trimmed to ~60 chars)
    private Integer messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
