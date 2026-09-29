package com.crmai.assistant.model;

public record TokenUsage(
        long promptTokens,
        long completionTokens,
        long totalTokens) {
}
