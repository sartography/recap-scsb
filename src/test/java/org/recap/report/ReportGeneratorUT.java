package org.recap.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.ReportDataEntity;
import org.recap.model.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
public class ReportGeneratorUT extends BaseTestCase {

    @Value("${scsb.report.directory}")
    private String reportDirectory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    RestTemplate mockRestTemplate;

    @Autowired
    FSDeAccessionReportGenerator fsDeAccessionReportGenerator;

    @Autowired
    FTPDeAccessionReportGenerator ftpDeAccessionReportGenerator;

    @Test
    public void testDeAccessionSummaryReport() throws Exception {
        String generatedReportFileName = generateReport(new Date(), new Date(), ReCAPConstants.DEACCESSION_SUMMARY_REPORT, ReCAPConstants.FILE_SYSTEM);
        Thread.sleep(2000);

        assertNotNull(generatedReportFileName);
        File directory = new File(reportDirectory);
        assertTrue(directory.isDirectory());

        boolean directoryContains = new File(directory, generatedReportFileName).exists();
        assertTrue(directoryContains);
    }

    private JSONArray getJSONReportEntities() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(ReCAPConstants.DEACCESSION_REPORT);
        reportEntity.setType(ReCAPConstants.DEACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity dateReportDataEntity = new ReportDataEntity();
        dateReportDataEntity.setHeaderName(ReCAPConstants.DATE_OF_DEACCESSION);
        String date = formatter.format(new Date());
        dateReportDataEntity.setHeaderValue(date);
        reportDataEntities.add(dateReportDataEntity);

        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
        barcodeReportDataEntity.setHeaderName(ReCAPConstants.BARCODE);
        barcodeReportDataEntity.setHeaderValue("123");
        reportDataEntities.add(barcodeReportDataEntity);

        ReportDataEntity statusReportDataEntity = new ReportDataEntity();
        statusReportDataEntity.setHeaderName(ReCAPConstants.STATUS);
        statusReportDataEntity.setHeaderValue(ReCAPConstants.FAILURE);
        reportDataEntities.add(statusReportDataEntity);

        ReportDataEntity reasonForFailureReportDataEntity = new ReportDataEntity();
        reasonForFailureReportDataEntity.setHeaderName(ReCAPConstants.REASON_FOR_FAILURE);
        reasonForFailureReportDataEntity.setHeaderValue("Item Barcode doesn't exist in SCSB database.");
        reportDataEntities.add(reasonForFailureReportDataEntity);

        reportEntity.setReportDataEntities(reportDataEntities);

        ObjectMapper objectMapper = new ObjectMapper();
        String report = objectMapper.writeValueAsString(reportEntity);

        JSONObject reportJSON = new JSONObject(report);

        JSONArray reportEntities = new JSONArray();
        reportEntities.put(reportJSON);
        return reportEntities;
    }

    public List<ReportGeneratorInterface> getGenerators() {
        List<ReportGeneratorInterface> reportGenerators = new ArrayList<>();
        reportGenerators.add(fsDeAccessionReportGenerator);
        reportGenerators.add(ftpDeAccessionReportGenerator);
        return reportGenerators;
    }

    private String generateReport(Date dateFrom, Date dateTo, String reportType, String transmissionType) {

        String generatedReportFileName = null;
        Calendar cal = Calendar.getInstance();
        if (dateFrom != null) {
            cal.setTime(dateFrom);
        } else {
            cal.setTime(new Date());
        }
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date from = cal.getTime();
        if (dateTo != null) {
            cal.setTime(dateTo);
        } else {
            cal.setTime(new Date());
        }
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date to = cal.getTime();

        MockReportGenerator mockReportGenerator = new MockReportGenerator();
        generatedReportFileName = mockReportGenerator.generateReport(reportType, transmissionType, from, to);
        return generatedReportFileName;
    }

    private class MockReportGenerator extends ReportGenerator {
        @Override
        public JSONArray getReportEntities(String reportType, Date from, Date to) throws JSONException {
            try {
                return getJSONReportEntities();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<ReportGeneratorInterface> getReportGenerators() {
            return getGenerators();
        }
    }
}