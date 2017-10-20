package org.throwable;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.throwable.support.HelloWorldJob;

/**
 * @author throwable
 * @version v1.0
 * @description
 * @since 2017/10/20 15:48
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception{
        LOGGER.info("Initializing...");
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler scheduler = sf.getScheduler();

        LOGGER.info("Load jobDetail...");
        JobDetail job = JobBuilder.newJob(HelloWorldJob.class).withIdentity("job1","group1").build();

        LOGGER.info("Load trigger...");
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity("job1","group1")
                .startNow().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(5).repeatForever()).build();

        LOGGER.info("Start scheduling...");
        scheduler.scheduleJob(job,trigger);

        scheduler.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}
