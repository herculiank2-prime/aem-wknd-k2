package com.adobe.aem.guides.wknd.core.schedulers;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class, immediate = true, property = {"scheduler.name=mysite-daily-report"})
@Designate(ocd = DailyReportSchedulerConfig.class)
public class DailyReportScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(DailyReportScheduler.class);

    private boolean enabled;

    private static final String JOB_NAME = "mysite-daily-report";

    @Reference
    private Scheduler scheduler;

    @Activate
    @Modified
    protected void activate(DailyReportSchedulerConfig config) {
        this.enabled   = config.enabled();
        LOG.debug("Daily report scheduler configured. enabled={} ", enabled);
        // Remove old schedule when configuration changes
        scheduler.unschedule(JOB_NAME);
        if (!enabled) {
            LOG.info("Daily report scheduler is disabled.");
            return;
        }
        ScheduleOptions options = scheduler.EXPR(config.scheduler_expression());
        options.name(JOB_NAME);
        options.canRunConcurrently(false);
        scheduler.schedule(this, options);
        LOG.debug("Daily report scheduler scheduled. cron={} ", config.scheduler_expression());
    }

    @Override
    public void run() {
        if (!enabled) {
            LOG.debug("Scheduler disabled by configuration — skipping run.");
            return;
        }
        LOG.debug("Generating daily report");
    }
}
