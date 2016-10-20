package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.marc4j.marc.Record;
import org.recap.ReCAPConstants;
import org.recap.etl.MarcToBibEntityConverter;
import org.recap.model.BibliographicEntity;
import org.recap.model.ReportEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchulakshmig on 20/10/16.
 */
@Component
public class AccessionService {

    Logger log = Logger.getLogger(AccessionService.class);

    @Value("${ils.princeton.bibdata}")
    String ilsprincetonBibData;

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.persistence.url}")
    String scsbPersistenceUrl;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Autowired
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Autowired
    MarcUtil marcUtil;

    public String getOwningInstitution(String customerCode) {
        String owningInstitution = null;
        try {
            String responseObject = new RestTemplate().getForObject(serverProtocol + scsbPersistenceUrl + "customerCode/search/findByCustomerCode?customerCode=" + customerCode, String.class);
            JSONObject jsonResponse = new JSONObject(responseObject).getJSONObject("_embedded");
            JSONObject institution = jsonResponse.getJSONObject("institutionEntity");
            owningInstitution = institution.getString("institutionCode");
        } catch (HttpClientErrorException ex) {
            owningInstitution = null;
        } catch (JSONException e) {
            log.error("Exception " + e);
        }
        return owningInstitution;
    }

    public String processRequest(String itemBarcode, String owningInstitution) {
        String response = null;
        RestTemplate restTemplate = new RestTemplate();

        String ilsBibDataURL = getILSBibDataURL(owningInstitution);
        String bibDataResponse = null;
        if (StringUtils.isNotBlank(ilsBibDataURL)) {
            try {
                bibDataResponse = restTemplate.getForObject(ilsBibDataURL + itemBarcode, String.class);
            } catch (HttpClientErrorException ex) {
                response = "Item Barcode not found";
                return response;
            } catch (Exception e) {
                response = ilsBibDataURL + "Service is Unavailable.";
                return response;
            }
        }
        List<Record> records = new ArrayList<>();
        if (StringUtils.isNotBlank(bibDataResponse)) {
            records = marcUtil.readMarcXml(bibDataResponse);
        }
        if (CollectionUtils.isNotEmpty(records)) {
            try {
                for (Record record : records) {
                    Map responseMap = marcToBibEntityConverter.convert(record, owningInstitution);
                    BibliographicEntity bibliographicEntity = (BibliographicEntity) responseMap.get("bibliographicEntity");
                    List<ReportEntity> reportEntityList = (List<ReportEntity>) responseMap.get("reportEntities");
                    if (CollectionUtils.isNotEmpty(reportEntityList)) {
                        restTemplate.postForLocation(serverProtocol + scsbPersistenceUrl + "report/create", reportEntityList);
                    }
                    if (bibliographicEntity != null) {
                        Integer bibliographicId = restTemplate.postForObject(serverProtocol + scsbPersistenceUrl + "bibliographic/create", bibliographicEntity, Integer.class);
                        if (bibliographicId != null) {
                            restTemplate.postForLocation(serverProtocol + scsbSolrClientUrl + "solrIndexer/indexByBibliographicId", bibliographicId);
                            response = ReCAPConstants.SUCCESS;
                        }
                    }
                }
            } catch (Exception e) {
                response = e.getMessage();
            }
        }
        return response;
    }

    private String getILSBibDataURL(String owningInstitution) {
        if (owningInstitution.equalsIgnoreCase(ReCAPConstants.PRINCETON)) {
            return ilsprincetonBibData;
        }
        return null;
    }
}
