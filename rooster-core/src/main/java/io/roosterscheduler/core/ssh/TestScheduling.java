package io.roosterscheduler.core.ssh;

import org.quartz.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.UUID;

@Configuration
public class TestScheduling {
    @Bean
    public CommandLineRunner setupTestScheduling(Scheduler scheduler) {
        return args -> {
            JobDataMap map = new JobDataMap();
            map.put("host", "192.168.31.205");
            map.put("port", "22");
            map.put("user", "max");
//                map.put("password", "<secret>");
            String key = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
                    "-----END OPENSSH PRIVATE KEY-----";
            map.put("privateKey", key);
            map.put("command", "/home/max/test.sh");
            JobDetail job = JobBuilder.newJob(SshJob.class)
                    .withIdentity(UUID.randomUUID().toString(), "ssh-jobs")
                    .withDescription("Test SSH job")
                    .usingJobData(map)
                    .storeDurably()
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(job)
                    .withIdentity(job.getKey().getName(), "ssh-triggers")
                    .withDescription( "Send Email Trigger")
                    .startAt(new Date())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                    .build();
            scheduler.scheduleJob(job, trigger);
        };
    }
}
