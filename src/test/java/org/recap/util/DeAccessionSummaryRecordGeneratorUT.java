package org.recap.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.recap.ReCAPConstants;
import org.recap.csv.DeAccessionSummaryRecord;
import org.recap.model.ReportDataEntity;
import org.recap.model.ReportEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
public class DeAccessionSummaryRecordGeneratorUT {
    @Test
    public void prepareDeAccessionSummaryReportRecord() throws Exception {
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

        DeAccessionSummaryRecordGenerator deAccessionSummaryRecordGenerator = new DeAccessionSummaryRecordGenerator();
        DeAccessionSummaryRecord deAccessionSummaryRecord = deAccessionSummaryRecordGenerator.prepareDeAccessionSummaryReportRecord(reportJSON);
        assertNotNull(deAccessionSummaryRecord);

        assertEquals(deAccessionSummaryRecord.getDateOfDeAccession(), date);
        assertEquals(deAccessionSummaryRecord.getBarcode(), "123");
        assertEquals(deAccessionSummaryRecord.getStatus(), ReCAPConstants.FAILURE);
        assertEquals(deAccessionSummaryRecord.getReasonForFailure(), "Item Barcode doesn't exist in SCSB database.");
    }

}