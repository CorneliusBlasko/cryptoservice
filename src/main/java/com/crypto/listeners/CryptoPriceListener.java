package com.crypto.listeners;

import com.crypto.jobs.CryptoPriceJob;
import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CryptoPriceListener implements ServletContextListener {

    private final static Logger LOGGER = Logger.getLogger(CryptoPriceListener.class.getName());
    // Initiate a Schedule Factory
    private static final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    // Retrieve a scheduler from schedule factory (Lazy init)
    private Scheduler scheduler = null;

    public void contextInitialized(ServletContextEvent servletContextEvent) {

        LOGGER.info("----- Initializing quartz -----");
        try {
            scheduler = schedulerFactory.getScheduler();

            // Initiate JobDetail with job name, job group, and executable job class
            JobDetail jobDetail = JobBuilder.newJob(CryptoPriceJob.class)
                    .withIdentity("crypto_price_query", "query")
                    .build();

            // Initiate SimpleTrigger with its name and group name.
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(TriggerKey.triggerKey("priceTrigger", "cryptoTriggerGroup"))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInSeconds(3)
                            .repeatForever())
//                .startAt(DateBuilder.futureDate(1, DateBuilder.IntervalUnit.SECOND))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();
        }
        catch (SchedulerException se)
        {
            LOGGER.log(Level.SEVERE, "Exception: ", se);
        }
        catch (Exception e)
        {
            LOGGER.log(Level.SEVERE, "Exception: ", e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try
        {
            if (scheduler != null && scheduler.isStarted())
                scheduler.shutdown();
        }
        catch (SchedulerException e)
        {
            LOGGER.log(Level.SEVERE, "Exception: ", e);
        }
    }
}
