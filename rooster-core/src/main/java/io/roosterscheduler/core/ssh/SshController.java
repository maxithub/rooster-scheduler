package io.roosterscheduler.core.ssh;

import io.roosterscheduler.core.AbstractJobDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.quartz.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class SshController {
    private final Scheduler scheduler;

    @Setter
    @Getter
    static class JobDefinition extends AbstractJobDefinition {
        private String host;
        private Integer port;
        private String user;
        private String password;
        private String privateKey;
        private String command;
        private Long connectTimeout;
        private Long authTimeout;
        private Long openChannelTimeout;
        private Long executionTimeout;
    }

    @PostMapping("/ssh")
    public ResponseEntity<JobDefinition> createJob(@RequestBody JobDefinition jobDefinition)
            throws SchedulerException {
        JobDataMap map = new JobDataMap();
        map.put("host", jobDefinition.host);
        map.put("port", jobDefinition.port);
        map.put("user", jobDefinition.user);
        map.put("password", jobDefinition.password);
        map.put("privateKey", jobDefinition.privateKey);
        map.put("command", jobDefinition.command);
        JobDetail job = JobBuilder.newJob(SshJob.class)
                .withIdentity(UUID.randomUUID().toString(), jobDefinition.getJobGroup())
                .withDescription(jobDefinition.getJobDescription())
                .usingJobData(map)
                .storeDurably()
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(job)
                .withIdentity(job.getKey().getName(), jobDefinition.getTriggerGroup())
                .withDescription(jobDefinition.getTriggerDescription())
                .startAt(Date.from(jobDefinition.getTriggerStartAt()))
                .withSchedule(CronScheduleBuilder.cronSchedule(jobDefinition.getTriggerCronExpression()))
                .build();
        scheduler.scheduleJob(job, trigger);
        return ResponseEntity.ok(jobDefinition);
    }
}
