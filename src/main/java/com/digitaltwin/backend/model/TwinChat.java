package com.digitaltwin.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("twin_chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndex(name = "user_session_time_idx", def = "{'userId': 1, 'sessionId': 1, 'timestamp': 1}")
public class TwinChat {

    @Id
    private String id;

    private String userId;
    private String sessionId; // reference to TwinChatSession

    private String question;
    private String aiResponse;
    private LocalDateTime timestamp;
}
