package org.recap.controller;

import lombok.extern.slf4j.Slf4j;
import org.recap.ReCAPConstants;
import org.recap.service.RestHeaderService;
import org.recap.model.ScheduleJobRequest;
import org.recap.model.ScheduleJobResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by rajeshbabuk on 5/4/17.
 */
@Slf4j
@RestController
@RequestMapping("/scheduleService")
public class ScheduleJobsController extends ReCAPController {

    /**
     *  This method is exposed as scheduler service for other microservices to schedule or reschedule or unschedule a job.
     *
     * @param scheduleJobRequest the schedule job request
     * @return the schedule job response
     */
    @RequestMapping(value="/scheduleJob", method = RequestMethod.POST)
    public ScheduleJobResponse scheduleJob(@RequestBody ScheduleJobRequest scheduleJobRequest) {
        ScheduleJobResponse scheduleJobResponse = null;
        try {
            HttpEntity<ScheduleJobRequest> httpEntity = new HttpEntity<>(scheduleJobRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<ScheduleJobResponse> responseEntity = getRestTemplate().exchange(getScsbScheduleUrl() + ReCAPConstants.URL_SCHEDULE_JOBS, HttpMethod.POST, httpEntity, ScheduleJobResponse.class);
            scheduleJobResponse = responseEntity.getBody();
        } catch (Exception e) {
            log.error(ReCAPConstants.LOG_ERROR,e);
            ScheduleJobResponse.builder().message(e.getMessage()).build();
        }
        return scheduleJobResponse;
    }
}
