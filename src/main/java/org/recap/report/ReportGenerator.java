package org.recap.report;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.ReCAPConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenchulakshmig on 13/10/16.
 */
@Component
public class ReportGenerator {
    Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.persistence.url}")
    String scsbPersistenceUrl;

    @Autowired
    FSDeAccessionReportGenerator fsDeAccessionReportGenerator;

    @Autowired
    FTPDeAccessionReportGenerator ftpDeAccessionReportGenerator;

    List<ReportGeneratorInterface> reportGenerators;

    public String generateReport(String reportType, String transmissionType, Date from, Date to) {
        String generatedReportFileName = null;

        try {
            JSONArray reportEntities = getReportEntities(reportType, from, to);
            if (reportEntities != null && reportEntities.length() > 0) {

                for (Iterator<ReportGeneratorInterface> iterator = getReportGenerators().iterator(); iterator.hasNext(); ) {
                    ReportGeneratorInterface reportGeneratorInterface = iterator.next();
                    if (reportGeneratorInterface.isInterested(reportType) && reportGeneratorInterface.isTransmitted(transmissionType)) {
                        generatedReportFileName = reportGeneratorInterface.generateReport(reportEntities, ReCAPConstants.DEACCESSION_REPORT);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception " + e);
        }
        return generatedReportFileName;
    }

    public JSONArray getReportEntities(String reportType, Date from, Date to) throws JSONException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            RestTemplate restTemplate = new RestTemplate();
            String responseObject = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "report/search/findByTypeAndDateRange?type={type}&from={from}&to={to}",
                    String.class, reportType, simpleDateFormat.format(from), simpleDateFormat.format(to));
            JSONObject jsonResponse = new JSONObject(responseObject).getJSONObject("_embedded");
            return jsonResponse.getJSONArray("report");
        } catch (Exception e) {
            logger.error("Exception " + e);
            return null;
        }
    }

    public List<ReportGeneratorInterface> getReportGenerators() {
        if (CollectionUtils.isEmpty(reportGenerators)) {
            reportGenerators = new ArrayList<>();
            reportGenerators.add(fsDeAccessionReportGenerator);
            reportGenerators.add(ftpDeAccessionReportGenerator);
        }
        return reportGenerators;
    }
}
