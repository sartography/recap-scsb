package org.recap.model;

import java.util.Date;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ScheduleJobResponse {
    private String message;
    private Date nextRunTime;
}
