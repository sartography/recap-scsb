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
        generatedReportFileName = reportGenerator.generateReport(scsbRequest.getReportType(), scsbRequest.getTransmissionType(), getFromDate(scsbRequest.getDateFrom()),
                getToDate(scsbRequest.getDateTo()));
        if (StringUtils.isBlank(generatedReportFileName)) {
            logger.error("Report wasn't generated! Please contact help desk!");
        } else {
            logger.info("Report successfully generated!" + " : " + generatedReportFileName);
        }
        String status = "The Generated Report File Name : " + generatedReportFileName;
        return status;
    }

    public Date getFromDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return  cal.getTime();
    }

    public Date getToDate(Date createdDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(createdDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}
