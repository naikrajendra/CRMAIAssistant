package com.crmai.assistant.service;

import com.crmai.assistant.model.CrmAccount;
import com.crmai.assistant.model.CrmAccountChange;
import com.crmai.assistant.model.TokenUsage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BriefingEvaluationService {

    public double calculateConfidence(CrmAccount account, String body) {
        if (account == null || account.changes() == null || account.changes().isEmpty()) {
            return 0.62;
        }

        List<CrmAccountChange> changes = account.changes();
        int urgencyWeight = (int) changes.stream().filter(
                change -> "High".equalsIgnoreCase(change.priority()) || "Critical".equalsIgnoreCase(change.priority()))
                .count();
        int totalPotential = Math.max(1, changes.size() * 2);
        double coverage = Math.min(1.0, (changes.size() / 5.0) + (body != null && body.length() > 200 ? 0.2 : 0.0));
        double urgency = Math.min(1.0, urgencyWeight / 2.0);
        double healthFactor = Math.max(0.45, Math.min(1.0, account.healthScore() / 100.0));

        double score = (coverage * 0.45) + (urgency * 0.25) + (healthFactor * 0.30);
        return Math.min(0.99, Math.max(0.55, score));
    }

    public TokenUsage estimateTokenUsage(String prompt, String response) {
        long promptTokens = Math.max(64L, (long) Math.ceil((prompt == null ? 0 : prompt.length()) / 4.0));
        long completionTokens = Math.max(64L, (long) Math.ceil((response == null ? 0 : response.length()) / 4.0));
        return new TokenUsage(promptTokens, completionTokens, promptTokens + completionTokens);
    }
}
