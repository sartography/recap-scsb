package org.recap.report;

import org.apache.camel.ProducerTemplate;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.ReCAPConstants;
import org.recap.csv.DeAccessionSummaryRecord;
import org.recap.util.DeAccessionSummaryRecordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenchulakshmig on 13/10/16.
 */
@Component
public class FSDeAccessionReportGenerator implements ReportGeneratorInterface {

    @Autowired
    ProducerTemplate producerTemplate;

    @Override
    public boolean isInterested(String reportType) {
        return reportType.equalsIgnoreCase(ReCAPConstants.DEACCESSION_SUMMARY_REPORT) ? true : false;
    }

    @Override
    public boolean isTransmitted(String transmissionType) {
        return transmissionType.equalsIgnoreCase(ReCAPConstants.FILE_SYSTEM) ? true : false;
    }

    public String generateReport(JSONArray reportEntities, String fileName) {
        String generatedFileName = null;
        List<DeAccessionSummaryRecord> deAccessionSummaryRecordList = new ArrayList<>();
        DeAccessionSummaryRecordGenerator deAccessionSummaryRecordGenerator = new DeAccessionSummaryRecordGenerator();
        for (int i = 0; i < reportEntities.length(); i++) {
            try {
                JSONObject reportEntity = reportEntities.getJSONObject(i);
                DeAccessionSummaryRecord deAccessionSummaryRecord = deAccessionSummaryRecordGenerator.prepareDeAccessionSummaryReportRecord(reportEntity);
                deAccessionSummaryRecordList.add(deAccessionSummaryRecord);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        producerTemplate.sendBodyAndHeader(ReCAPConstants.FS_DE_ACCESSION_SUMMARY_REPORT_Q, deAccessionSummaryRecordList, "fileName", fileName);

        DateFormat df = new SimpleDateFormat(ReCAPConstants.DATE_FORMAT_FOR_FILE_NAME);
        generatedFileName = fileName + "-" + df.format(new Date()) + ".csv";
        return generatedFileName;
    }
}
