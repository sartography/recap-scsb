package org.recap.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ScheduleJobResponse {
    private String message;
    private Date nextRunTime;
}
