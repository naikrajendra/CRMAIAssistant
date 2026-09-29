package com.crmai.assistant.service;

import com.crmai.assistant.model.DailyBriefing;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.stereotype.Service;

@Service
public class ObservabilityService {

    private final MeterRegistry meterRegistry;

    public ObservabilityService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordBriefing(DailyBriefing briefing) {
        if (briefing == null || meterRegistry == null) {
            return;
        }

        meterRegistry.counter("crm.ai.briefings.generated", "account", briefing.accountId()).increment();
        meterRegistry.gauge("crm.ai.confidence.score", Tags.of("account", briefing.accountId()),
                briefing.confidenceScore());
        meterRegistry.summary("crm.ai.token.usage", Tags.of("account", briefing.accountId(), "type", "prompt"))
                .record(briefing.tokenUsage().promptTokens());
        meterRegistry.summary("crm.ai.token.usage", Tags.of("account", briefing.accountId(), "type", "completion"))
                .record(briefing.tokenUsage().completionTokens());
        meterRegistry.summary("crm.ai.token.usage", Tags.of("account", briefing.accountId(), "type", "total"))
                .record(briefing.tokenUsage().totalTokens());
    }
}
