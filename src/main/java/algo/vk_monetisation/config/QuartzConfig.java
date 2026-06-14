package algo.vk_monetisation.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import algo.vk_monetisation.scheduler.RetryPendingInnChecksJob;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail retryJobDetail() {
        return JobBuilder.newJob(RetryPendingInnChecksJob.class)
                .withIdentity("retryPendingInnChecks")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger retryJobTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"); // каждые 5 минут

        return TriggerBuilder.newTrigger()
                .forJob(retryJobDetail())
                .withIdentity("retryPendingInnChecksTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
