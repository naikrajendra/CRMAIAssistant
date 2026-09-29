package com.crmai.assistant.service;

import com.crmai.assistant.model.CrmAccount;
import com.crmai.assistant.model.DailyBriefing;
import com.crmai.assistant.model.TokenUsage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class DailyBriefingService {

    private static final Logger log = LoggerFactory.getLogger(DailyBriefingService.class);

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;
    private final BriefingEvaluationService evaluationService;
    private final ObservabilityService observabilityService;
    private final CrmAccountDataService crmAccountDataService;
    private final DailyTrendService dailyTrendService;
    private List<DailyBriefing> latestBriefings = List.of();

    @Autowired
    public DailyBriefingService(
            ChatClient chatClient,
            ObjectMapper objectMapper,
            BriefingEvaluationService evaluationService,
            ObservabilityService observabilityService,
            CrmAccountDataService crmAccountDataService,
            DailyTrendService dailyTrendService) {
        this.chatClient = chatClient;
        this.objectMapper = objectMapper;
        this.evaluationService = evaluationService;
        this.crmAccountDataService = crmAccountDataService;
        this.observabilityService = observabilityService;
        this.dailyTrendService = dailyTrendService;
    }

    public DailyBriefingService(ChatClient chatClient, ObjectMapper objectMapper,
            BriefingEvaluationService evaluationService, ObservabilityService observabilityService) {
        this(chatClient, objectMapper, evaluationService, observabilityService, null, null);
    }

    public List<CrmAccount> loadAccounts() {
        if (crmAccountDataService == null) {
            return List.of();
        }
        return crmAccountDataService.loadAccounts();
    }

    public List<DailyBriefing> generateDailyBriefings() {
        if (crmAccountDataService == null) {
            return List.of();
        }

        List<DailyBriefing> briefings = crmAccountDataService.loadAccounts().stream()
                .map(this::generateBriefingForAccount)
                .toList();

        latestBriefings = briefings;

        if (dailyTrendService != null) {
            dailyTrendService.recordRun(briefings);
        }

        return briefings;
    }

    public List<DailyBriefing> getLatestBriefings() {
        return latestBriefings;
    }

    public DailyBriefing generateBriefingForAccount(CrmAccount account) {
        String prompt = buildPrompt(account);
        String body = generateDraft(account, prompt);
        double confidence = evaluationService != null
                ? evaluationService.calculateConfidence(account, body)
                : 0.8;

        TokenUsage usage = evaluationService != null
                ? evaluationService.estimateTokenUsage(prompt, body)
                : new TokenUsage(128, 192, 320);

        DailyBriefing briefing = new DailyBriefing(
                account.id(),
                account.name(),
                "Daily CRM brief for " + account.name(),
                body,
                confidence,
                usage,
                Instant.now());

        if (observabilityService != null) {
            observabilityService.recordBriefing(briefing);
        }

        return briefing;
    }

    private String buildPrompt(CrmAccount account) {
        StringBuilder builder = new StringBuilder();
        builder.append(
                "You are a CRM account brief writer. Compose a concise executive email for the account owner.\n");
        builder.append("Account: ").append(account.name()).append(" (ID: ").append(account.id()).append(")\n");
        builder.append("Industry: ").append(account.industry()).append(" | Region: ").append(account.region())
                .append(" | Health Score: ").append(account.healthScore()).append("\n");
        builder.append("Owner: ").append(account.accountOwner()).append("\n");
        builder.append("Recent CRM changes:\n");
        for (var change : account.changes()) {
            builder.append("- ").append(change.type()).append(" | ").append(change.description())
                    .append(" | Amount: $")
                    .append(String.format("%,.2f", change.amount()))
                    .append(" | Priority: ")
                    .append(change.priority())
                    .append("\n");
        }
        builder.append(
                "Return a short formatted email with subject and body, no markdown, and include a clear call to action.\n");
        return builder.toString();
    }

    private String generateDraft(CrmAccount account, String prompt) {
        if (chatClient == null) {
            return createFallbackDraft(account);
        }

        try {
            String content = chatClient.prompt()
                    .system("You are an enterprise CRM account brief writer focused on concise, actionable email updates.")
                    .user(prompt)
                    .call()
                    .content();
            if (content == null || content.isBlank()) {
                return createFallbackDraft(account);
            }
            return content;
        } catch (Exception ex) {
            log.warn("Spring AI generation failed for account {}. Falling back to local draft. Cause: {}",
                    account.name(), ex.getMessage());
            return createFallbackDraft(account);
        }
    }

    private String createFallbackDraft(CrmAccount account) {
        StringBuilder builder = new StringBuilder();
        builder.append("Subject: Daily CRM Briefing - ").append(account.name()).append("\n\n");
        builder.append("Hi ").append(account.accountOwner()).append(",\n\n");
        builder.append("Here is today’s summary for ").append(account.name()).append(".\n");
        builder.append("- Health score remains at ").append(account.healthScore())
                .append(" and is trending positively.\n");
        for (var change : account.changes()) {
            builder.append("- ").append(change.type()).append(": ").append(change.description()).append(" (Priority: ")
                    .append(change.priority()).append(")\n");
        }
        builder.append(
                "Recommended next step: confirm the top opportunity owner, align on pricing approval, and schedule a working session with the customer success team.\n\n");
        builder.append("Regards,\nCRM Intelligence Assistant");
        return builder.toString();
    }
}
