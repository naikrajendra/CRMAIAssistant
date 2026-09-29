package com.crmai.assistant.model;

import java.util.List;

public record CrmAccount(
        String id,
        String name,
        String industry,
        String region,
        String accountOwner,
        int healthScore,
        List<CrmAccountChange> changes) {
}
