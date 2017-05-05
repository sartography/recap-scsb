package org.recap.model;

import java.util.Date;

/**
 * Created by rajeshbabuk on 5/4/17.
 */
public class ScheduleJobResponse {

    private String message;
    private Date nextRunTime;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getNextRunTime() {
        return nextRunTime;
    }

    public void setNextRunTime(Date nextRunTime) {
        this.nextRunTime = nextRunTime;
    }
}
