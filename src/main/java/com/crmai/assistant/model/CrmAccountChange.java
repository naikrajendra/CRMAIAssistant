package com.crmai.assistant.model;

public record CrmAccountChange(
        String type,
        String description,
        double amount,
        String priority) {
}
