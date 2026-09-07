package com.adobe.aem.guides.wknd.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "MySite - Daily Report Scheduler",
        description = "Generates the marketing summary report on a schedule.")
public @interface DailyReportSchedulerConfig {

    @AttributeDefinition(
            name = "Cron expression",
            description = "When to run. Default: every minute.")
    String scheduler_expression() default "0 * * * * ?";

    @AttributeDefinition(
            name = "Enabled",
            description = "Turn the scheduler on or off without a deployment.")
    boolean enabled() default false;

}
