package io.roosterscheduler.core;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public abstract class AbstractJobDefinition {
    protected String jobGroup;
    protected String jobDescription;
    protected String triggerGroup;
    protected String triggerDescription;
    protected Instant triggerStartAt;
    protected String triggerCronExpression;
}
