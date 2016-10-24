package org.recap.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.ReCAPConstants;
import org.recap.model.ScsbRequest;
import org.recap.report.ReportGenerator;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
public class ScsbControllerUT extends BaseControllerUT {

    @InjectMocks
    ScsbController scsbController = new ScsbController();

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Mock
    ReportGenerator reportGenerator;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDeAccessionSummaryReport() throws Exception {
        ScsbRequest scsbRequest = new ScsbRequest();
        scsbRequest.setDateFrom(new Date());
        scsbRequest.setDateTo(new Date());
        scsbRequest.setTransmissionType(ReCAPConstants.FILE_SYSTEM);
        scsbRequest.setReportType(ReCAPConstants.DEACCESSION_SUMMARY_REPORT);
        when(reportGenerator.generateReport(scsbRequest.getReportType(), scsbRequest.getTransmissionType(), scsbController.getFromDate(scsbRequest.getDateFrom()),
                scsbController.getToDate(scsbRequest.getDateTo()))).thenReturn("DeAccession_Report");
        String result = scsbController.generateReport(scsbRequest, bindingResult, model);
        assertTrue(result.contains("DeAccession_Report"));
    }

}