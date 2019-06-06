package org.recap.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleJobRequest {
    private Integer jobId;
    private String jobName;
    private String cronExpression;
    private String scheduleType;
}
