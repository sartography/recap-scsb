package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ScheduleJobRequest {
    private Integer jobId;
    private String jobName;
    private String cronExpression;
    private String scheduleType;
}
