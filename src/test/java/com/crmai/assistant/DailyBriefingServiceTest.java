package com.crmai.assistant;

import com.crmai.assistant.model.CrmAccount;
import com.crmai.assistant.model.CrmAccountChange;
import com.crmai.assistant.model.DailyBriefing;
import com.crmai.assistant.service.DailyBriefingService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DailyBriefingServiceTest {

    @Test
    void shouldGenerateBriefingForAccountWithConfidenceScore() {
        CrmAccount account = new CrmAccount(
                "ACCT-1001",
                "BluePeak Logistics",
                "Logistics",
                "North America",
                "Ava Patel",
                82,
                List.of(
                        new CrmAccountChange("LEAD", "High-intent new inbound lead from enterprise account expansion",
                                12_500.0, "Outbound"),
                        new CrmAccountChange("OPPORTUNITY", "Renewal opportunity closing within 30 days", 240_000.0,
                                "High"),
                        new CrmAccountChange("QUOTE", "Custom freight quote delivered and awaiting pricing approval",
                                96_000.0, "Medium")));

        DailyBriefingService service = new DailyBriefingService(null, null, null, null);

        DailyBriefing briefing = service.generateBriefingForAccount(account);

        assertNotNull(briefing);
        assertEquals("ACCT-1001", briefing.accountId());
        assertTrue(briefing.subject().contains("BluePeak Logistics"));
        assertTrue(briefing.confidenceScore() >= 0.50);
        assertTrue(briefing.confidenceScore() <= 0.99);
        assertNotNull(briefing.tokenUsage());
    }
}
