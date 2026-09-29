package com.crmai.assistant.service;

import com.crmai.assistant.model.DailyBriefing;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Service
public class DailyTrendService {

    private static final int MAX_HISTORY_DAYS = 14;

    private final Deque<DailyTrendPoint> history = new ArrayDeque<>();

    public void recordRun(List<DailyBriefing> briefings) {
        if (briefings == null || briefings.isEmpty()) {
            return;
        }

        double averageConfidence = briefings.stream()
                .mapToDouble(DailyBriefing::confidenceScore)
                .average()
                .orElse(0.0);

        long averageTokens = Math.round(briefings.stream()
                .mapToLong(b -> b.tokenUsage() == null ? 0 : b.tokenUsage().totalTokens())
                .average()
                .orElse(0.0));

        DailyTrendPoint point = new DailyTrendPoint(LocalDate.now(), averageConfidence, averageTokens);
        history.addLast(point);

        while (history.size() > MAX_HISTORY_DAYS) {
            history.pollFirst();
        }
    }

    public List<DailyTrendPoint> getRecentHistory() {
        return new ArrayList<>(history);
    }

    public record DailyTrendPoint(LocalDate date, double averageConfidence, long averageTokens) {
    }
}
