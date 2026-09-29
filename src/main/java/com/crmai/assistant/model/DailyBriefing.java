package com.crmai.assistant.model;

import java.time.Instant;

public record DailyBriefing(
        String accountId,
        String accountName,
        String subject,
        String body,
        double confidenceScore,
        TokenUsage tokenUsage,
        Instant generatedAt
) {
}
