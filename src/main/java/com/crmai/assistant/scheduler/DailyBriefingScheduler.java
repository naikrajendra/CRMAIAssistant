package com.crmai.assistant.scheduler;

import com.crmai.assistant.model.DailyBriefing;
import com.crmai.assistant.service.DailyBriefingService;
import com.crmai.assistant.service.EmailDispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyBriefingScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyBriefingScheduler.class);

    private final DailyBriefingService dailyBriefingService;
    private final EmailDispatchService emailDispatchService;

    public DailyBriefingScheduler(DailyBriefingService dailyBriefingService,
            EmailDispatchService emailDispatchService) {
        this.dailyBriefingService = dailyBriefingService;
        this.emailDispatchService = emailDispatchService;
    }

    @Scheduled(cron = "${crm.daily-briefing.cron:0 0 4 * * *}")
    public void sendDailyBriefings() {
        List<DailyBriefing> briefings = dailyBriefingService.generateDailyBriefings();
        log.info("Generated {} account briefings for delivery", briefings.size());
        briefings.forEach(emailDispatchService::sendEmail);
    }
}
