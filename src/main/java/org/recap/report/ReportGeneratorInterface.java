package org.recap.report;


import org.codehaus.jettison.json.JSONArray;

/**
 * Created by peris on 8/17/16.
 */
public interface ReportGeneratorInterface {

    boolean isInterested(String reportType);

    boolean isTransmitted(String transmissionType);

    String generateReport(JSONArray reportEntities, String fileName);
}
