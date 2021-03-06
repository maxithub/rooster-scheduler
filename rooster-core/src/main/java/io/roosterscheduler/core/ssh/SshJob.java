package io.roosterscheduler.core.ssh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.session.ClientSession;
import org.hibernate.engine.jdbc.ReaderInputStream;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.time.Duration;
import java.util.EnumSet;

import static org.apache.sshd.client.channel.ClientChannelEvent.CLOSED;
import static org.apache.sshd.common.util.security.SecurityUtils.loadKeyPairIdentities;
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
                    Iterable<KeyPair> keyPairIterator = loadKeyPairIdentities(session, null,
                            new ReaderInputStream(new StringReader(privateKey)), null);
                    if (keyPairIterator == null
                            || keyPairIterator.iterator() == null
                            || !keyPairIterator.iterator().hasNext()) {
                        throw new GeneralSecurityException("Failed to load the private key");
                    }
                    session.addPublicKeyIdentity(keyPairIterator.iterator().next());
                }
                session.auth().verify(authTimeout);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try (ClientChannel channel = session.createExecChannel(command)) {
                    channel.setOut(byteArrayOutputStream);
                    channel.open().verify(openChannelTimeout);
                    channel.waitFor(EnumSet.of(CLOSED), executionTimeout);
                    int exit = channel.getExitStatus();
                    String shellOutput = byteArrayOutputStream.toString();
                    log.info("------------- Shell Output -------------\n{}", shellOutput);
                    if (exit != 0) {
                        String message = String.format("Failed to execute command: %s, exit status: %d", command, exit);
                        throw new JobExecutionException(message);
                    }
                }
            } finally {
                client.stop();
            }
        } catch (IOException | GeneralSecurityException e) {
            throw new JobExecutionException(String.format("Failed to connect/operate %s@%s:%d",user, host, port), e);
        }
    }
}
