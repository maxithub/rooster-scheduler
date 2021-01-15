package io.roosterscheduler.core.ssh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(SshConfiguration.Properties.class)
public class SshConfiguration {
    @ConfigurationProperties("rooster.ssh")
    @Data
    public static class Properties {
        private Timeouts timeouts = new Timeouts();
    }

    @Data
    public static class Timeouts {
        private Duration connect = Duration.ofSeconds(5);
        private Duration auth = Duration.ofSeconds(5);
        private Duration openChannel = Duration.ofSeconds(5);
        private Duration execution = Duration.ofHours(1);
    }
}
