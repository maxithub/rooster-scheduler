package io.roosterscheduler.core.ssh;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.EnumSet;

import static org.apache.sshd.client.channel.ClientChannelEvent.CLOSED;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Component
@RequiredArgsConstructor
public class SshJob extends QuartzJobBean {
    private final SshConfiguration.Properties properties;

    private long getTimeoutWithDefault(JobDataMap jobDataMap, String key, Duration defaultValue) {
        Object timeout = jobDataMap.get(key);
        if (timeout != null) {
            try {
                if(timeout instanceof Long) {
                    return ((Long) timeout).longValue();
                }
                return Long.parseLong(timeout.toString());
            } catch (Exception e) { }
        }
        return defaultValue.toMillis();
    }

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        String host = jobDataMap.getString("host");
        int port = jobDataMap.getInt("port");
        String user = jobDataMap.getString("user");
        String password = jobDataMap.getString("password");
        String privateKey = jobDataMap.getString("privateKey");
        String command = jobDataMap.getString("command");
        long connectTimeout = getTimeoutWithDefault(jobDataMap, "connectTimeout",
                properties.getTimeouts().getConnect());
        long authTimeout = getTimeoutWithDefault(jobDataMap, "authTimeout",
                properties.getTimeouts().getConnect());
        long openChannelTimeout = getTimeoutWithDefault(jobDataMap, "openChannelTimeout",
                properties.getTimeouts().getOpenChannel());
        long executionTimeout = getTimeoutWithDefault(jobDataMap, "executionTimeout",
                properties.getTimeouts().getExecution());

        try (SshClient client = SshClient.setUpDefaultClient()) {
            client.start();
            try (ClientSession session = client.connect(user, host, port)
                    .verify(connectTimeout)
                    .getSession()) {
                if (hasText(password)) {
                    session.addPasswordIdentity(password);
                }
                if (hasText(privateKey)) {
//                    session.setPasswordIdentityProvider(FilePasswordProvider.EMPTY.getPassword());
                }
                session.auth().verify(authTimeout);

                try (ClientChannel channel = session.createExecChannel(command)) {
                    channel.setOut(System.out);
                    channel.open().verify(openChannelTimeout);
                    channel.waitFor(EnumSet.of(CLOSED), executionTimeout);
                    int exit = channel.getExitStatus();
                    if (exit != 0) {
                        String message = String.format("Failed to execute command: %s, exit status: %d", command, exit);
                        throw new JobExecutionException(message);
                    }
                }
            } finally {
                client.stop();
            }
        } catch (IOException e) {
            throw new JobExecutionException(String.format("Failed to connect/operate %s@%s:%d",user, host, port), e);
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        JobExecutionContext context = new JobExecutionContext() {

            @Override
            public Scheduler getScheduler() {
                return null;
            }

            @Override
            public Trigger getTrigger() {
                return null;
            }

            @Override
            public Calendar getCalendar() {
                return null;
            }

            @Override
            public boolean isRecovering() {
                return false;
            }

            @Override
            public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
                return null;
            }

            @Override
            public int getRefireCount() {
                return 0;
            }

            @Override
            public JobDataMap getMergedJobDataMap() {
                JobDataMap map = new JobDataMap();
                map.put("host", "192.168.31.205");
                map.put("port", "22");
                map.put("user", "max");
                map.put("password", "<PASSWORD>");
                map.put("command", "/home/max/test.sh");
                return map;
            }

            @Override
            public JobDetail getJobDetail() {
                return null;
            }

            @Override
            public Job getJobInstance() {
                return null;
            }

            @Override
            public Date getFireTime() {
                return null;
            }

            @Override
            public Date getScheduledFireTime() {
                return null;
            }

            @Override
            public Date getPreviousFireTime() {
                return null;
            }

            @Override
            public Date getNextFireTime() {
                return null;
            }

            @Override
            public String getFireInstanceId() {
                return null;
            }

            @Override
            public Object getResult() {
                return null;
            }

            @Override
            public void setResult(Object result) {

            }

            @Override
            public long getJobRunTime() {
                return 0;
            }

            @Override
            public void put(Object key, Object value) {

            }

            @Override
            public Object get(Object key) {
                return null;
            }
        };

        SshJob job = new SshJob(new SshConfiguration.Properties());
        job.executeInternal(context);
    }
}
