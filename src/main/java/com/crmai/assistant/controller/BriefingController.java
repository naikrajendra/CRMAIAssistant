package com.crmai.assistant.controller;

import com.crmai.assistant.model.CrmAccount;
import com.crmai.assistant.model.DailyBriefing;
import com.crmai.assistant.service.DailyBriefingService;
import com.crmai.assistant.service.DailyTrendService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class BriefingController {

    private final DailyBriefingService dailyBriefingService;
    private final DailyTrendService dailyTrendService;

    public BriefingController(DailyBriefingService dailyBriefingService, DailyTrendService dailyTrendService) {
        this.dailyBriefingService = dailyBriefingService;
        this.dailyTrendService = dailyTrendService;
    }

    @GetMapping("/accounts")
    public List<CrmAccount> getAccounts() {
        return dailyBriefingService.loadAccounts();
    }

    @PostMapping("/briefings/generate")
    public List<DailyBriefing> generateBriefings() {
        return dailyBriefingService.generateDailyBriefings();
    }

    @GetMapping("/dashboard/summary")
    public Map<String, Object> dashboardSummary() {
        List<DailyBriefing> briefings = dailyBriefingService.getLatestBriefings();
        if (briefings.isEmpty()) {
            briefings = dailyBriefingService.generateDailyBriefings();
        }

        double averageConfidence = briefings.stream()
                .mapToDouble(DailyBriefing::confidenceScore)
                .average()
                .orElse(0.0);
        long averageTokens = Math.round(briefings.stream()
                .mapToLong(b -> b.tokenUsage() == null ? 0 : b.tokenUsage().totalTokens())
                .average()
                .orElse(0.0));

        List<Map<String, Object>> trend = briefings.stream()
                .limit(10)
                .map(b -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("account", b.accountName());
                    item.put("confidence", b.confidenceScore());
                    item.put("tokens", b.tokenUsage() == null ? 0 : b.tokenUsage().totalTokens());
                    return item;
                })
                .collect(Collectors.toList());

        return Map.of(
                "totalAccounts", briefings.size(),
                "averageConfidence", averageConfidence,
                "averageTokens", averageTokens,
                "trend", trend);
    }

    @GetMapping("/dashboard/history")
    public Map<String, Object> dashboardHistory() {
        return Map.of("history", dailyTrendService.getRecentHistory().stream()
                .map(point -> Map.of(
                        "date", point.date().toString(),
                        "confidence", point.averageConfidence(),
                        "tokens", point.averageTokens()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "CRMAIAssistant");
    }
}
