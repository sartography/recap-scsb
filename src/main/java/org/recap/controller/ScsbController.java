package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.model.ScsbRequest;
import org.recap.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenchulakshmig on 12/10/16.
 */
@Controller
public class ScsbController {

    @Autowired
    ReportGenerator reportGenerator;

    Logger logger = LoggerFactory.getLogger(ScsbController.class);

    @RequestMapping("/")
    public String scsb(Model model) {
        ScsbRequest scsbRequest = new ScsbRequest();
        model.addAttribute("scsbRequest", scsbRequest);
        return "scsb";
    }

    @ResponseBody
    @RequestMapping(value = "/reports", method = RequestMethod.POST)
    public String generateReport(@Valid @ModelAttribute("scsbRequest") ScsbRequest scsbRequest,
                                 BindingResult result,
                                 Model model) {
        String generatedReportFileName = null;
        Calendar cal = Calendar.getInstance();
        Date dateFrom = scsbRequest.getDateFrom();
        if (dateFrom != null) {
            cal.setTime(dateFrom);
        } else {
            cal.setTime(new Date());
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date from = cal.getTime();
        Date dateTo = scsbRequest.getDateTo();
        if (dateTo != null) {
            cal.setTime(dateTo);
        } else {
            cal.setTime(new Date());
        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date to = cal.getTime();
        generatedReportFileName = reportGenerator.generateReport(scsbRequest.getReportType(), scsbRequest.getTransmissionType(), from, to);
        if (StringUtils.isBlank(generatedReportFileName)) {
            logger.error("Report wasn't generated! Please contact help desk!");
        } else {
            logger.info("Report successfully generated!" + " : " + generatedReportFileName);
        }
        String status = "The Generated Report File Name : " + generatedReportFileName;
        return status;
    }
}
