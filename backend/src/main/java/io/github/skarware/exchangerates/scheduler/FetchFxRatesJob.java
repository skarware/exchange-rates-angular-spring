package io.github.skarware.exchangerates.scheduler;

import io.github.skarware.exchangerates.service.FetchFxRatesJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FetchFxRatesJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FetchFxRatesJobService jobService;

    public void execute(JobExecutionContext context) {
        logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

        // Get new FxRates from API
        jobService.executeFetchAddFxRates();

        logger.info("Next job scheduled @ {}", context.getNextFireTime());
    }
}
