package io.github.skarware.exchangerates.scheduler;

import io.github.skarware.exchangerates.config.AutoWiringSpringBeanJobFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.*;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
@ConditionalOnExpression("'${enable.spring.schedulerFactory}'=='true'")
public class SpringQuartzScheduler {

    // Fetch for new FxRates data every day 9 AM
    private static final String CRON_EXPRESSION = "0 0 9 * * ?";

    private final ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public SpringQuartzScheduler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        logger.info("Spring Quartz Scheduler Loaded...");
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job, DataSource dataSource) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
        schedulerFactory.setJobFactory(springBeanJobFactory());
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        schedulerFactory.setDataSource(dataSource);
        return schedulerFactory;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean(name = "FetchFxRatesJob")
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(FetchFxRatesJob.class);
        jobDetailFactory.setDescription("fetch FxRates from API...");
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public CronTriggerFactoryBean trigger(JobDetail job) {
        CronTriggerFactoryBean trigger = new CronTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setPriority(20);
        trigger.setCronExpression(CRON_EXPRESSION);
        trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW);
        return trigger;
    }
}
