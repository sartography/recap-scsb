package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.ReCAPConstants;
import org.recap.model.DeAccessionDBResponseEntity;
import org.recap.model.ReportDataEntity;
import org.recap.model.ReportEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chenchulakshmig on 28/9/16.
 */
@Component
public class DeAccessionUtil {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.persistence.url}")
    String scsbPersistenceUrl;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    public List<DeAccessionDBResponseEntity> deAccessionItemsInDB(List<String> itemBarcodeList) {
        RestTemplate restTemplate = new RestTemplate();
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity;
        List<String> responseItemBarcodeList = new ArrayList<>();

        String responseObject = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "item/search/findByBarcodeIn?barcodes=" + StringUtils.join(itemBarcodeList, ","), String.class);
        try {
            JSONObject jsonResponse = new JSONObject(responseObject).getJSONObject("_embedded");
            JSONArray itemEntities = jsonResponse.getJSONArray("item");
            String barcode = null;
            for (int i = 0; i < itemEntities.length(); i++) {
                try {
                    JSONObject itemEntity = itemEntities.getJSONObject(i);
                    barcode = itemEntity.getString("barcode");
                    responseItemBarcodeList.add(barcode);
                    if (itemEntity.getBoolean("deleted")) {
                        deAccessionDBResponseEntity = prepareFailureResponse(barcode, ReCAPConstants.REQUESTED_ITEM_DEACCESSIONED, itemEntity);
                        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                    } else {
                        JSONArray holdingEntites = itemEntity.getJSONArray("holdingsEntities");
                        JSONArray bibliographicEntities = itemEntity.getJSONArray("bibliographicEntities");
                        Integer itemId = itemEntity.getInt("itemId");
                        List<Integer> holdingIds = processHoldings(holdingEntites);

                        List<Integer> bibliographicIds = processBibs(bibliographicEntities);

                        restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "item/search/markItemAsDeleted?itemId=" + itemId, int.class);

                        deAccessionDBResponseEntity = prepareSuccessResponse(barcode, itemEntity, holdingIds, bibliographicIds);
                        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                    }
                } catch (Exception exception) {
                    deAccessionDBResponseEntity = prepareFailureResponse(barcode, "Exception " + exception, null);
                    deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                }
            }
            if (responseItemBarcodeList.size() != itemBarcodeList.size()) {
                for (String itemBarcode : itemBarcodeList) {
                    if (!responseItemBarcodeList.contains(itemBarcode)) {
                        deAccessionDBResponseEntity = prepareFailureResponse(itemBarcode, ReCAPConstants.ITEM_BARCDE_DOESNOT_EXIST, null);
                        deAccessionDBResponseEntities.add(deAccessionDBResponseEntity);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return deAccessionDBResponseEntities;
    }

    public List<ReportEntity> processAndSave(List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities) {
        RestTemplate restTemplate = new RestTemplate();
        List<ReportEntity> reportEntities = new ArrayList<>();
        ReportEntity reportEntity = null;
        if (CollectionUtils.isNotEmpty(deAccessionDBResponseEntities)) {
            for (DeAccessionDBResponseEntity deAccessionDBResponseEntity : deAccessionDBResponseEntities) {
                List<String> owningInstitutionBibIds = deAccessionDBResponseEntity.getOwningInstitutionBibIds();
                if (CollectionUtils.isNotEmpty(owningInstitutionBibIds)) {
                    for (String owningInstitutionBibId : owningInstitutionBibIds) {
                        reportEntity = generateReportEntity(deAccessionDBResponseEntity, owningInstitutionBibId);
                        reportEntities.add(reportEntity);
                    }
                } else {
                    reportEntity = generateReportEntity(deAccessionDBResponseEntity, null);
                    reportEntities.add(reportEntity);
                }
            }
            if (!CollectionUtils.isEmpty(reportEntities)) {
                restTemplate.postForLocation(serverProtocol + scsbPersistenceUrl + "report/create", reportEntities);
            }
        }
        return reportEntities;
    }

    private ReportEntity generateReportEntity(DeAccessionDBResponseEntity deAccessionDBResponseEntity, String owningInstitutionBibId) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setFileName(ReCAPConstants.DEACCESSION_REPORT);
        reportEntity.setType(ReCAPConstants.DEACCESSION_SUMMARY_REPORT);
        reportEntity.setCreatedDate(new Date());

        List<ReportDataEntity> reportDataEntities = new ArrayList<>();

        ReportDataEntity dateReportDataEntity = new ReportDataEntity();
        dateReportDataEntity.setHeaderName(ReCAPConstants.DATE_OF_DEACCESSION);
        dateReportDataEntity.setHeaderValue(formatter.format(new Date()));
        reportDataEntities.add(dateReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getInstitutionCode())) {
            reportEntity.setInstitutionName(deAccessionDBResponseEntity.getInstitutionCode());

            ReportDataEntity owningInstitutionReportDataEntity = new ReportDataEntity();
            owningInstitutionReportDataEntity.setHeaderName(ReCAPConstants.OWNING_INSTITUTION);
            owningInstitutionReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getInstitutionCode());
            reportDataEntities.add(owningInstitutionReportDataEntity);
        } else {
            reportEntity.setInstitutionName("NA");
        }

        ReportDataEntity barcodeReportDataEntity = new ReportDataEntity();
        barcodeReportDataEntity.setHeaderName(ReCAPConstants.BARCODE);
        barcodeReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getBarcode());
        reportDataEntities.add(barcodeReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(owningInstitutionBibId)) {
            ReportDataEntity owningInstitutionBibIdReportDataEntity = new ReportDataEntity();
            owningInstitutionBibIdReportDataEntity.setHeaderName(ReCAPConstants.OWNING_INSTITUTION_BIB_ID);
            owningInstitutionBibIdReportDataEntity.setHeaderValue(owningInstitutionBibId);
            reportDataEntities.add(owningInstitutionBibIdReportDataEntity);
        }

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getCollectionGroupCode())) {
            ReportDataEntity collectionGroupCodeReportDataEntity = new ReportDataEntity();
            collectionGroupCodeReportDataEntity.setHeaderName(ReCAPConstants.COLLECTION_GROUP_CODE);
            collectionGroupCodeReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getCollectionGroupCode());
            reportDataEntities.add(collectionGroupCodeReportDataEntity);
        }

        ReportDataEntity statusReportDataEntity = new ReportDataEntity();
        statusReportDataEntity.setHeaderName(ReCAPConstants.STATUS);
        statusReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getStatus());
        reportDataEntities.add(statusReportDataEntity);

        if (!org.springframework.util.StringUtils.isEmpty(deAccessionDBResponseEntity.getReasonForFailure())) {
            ReportDataEntity reasonForFailureReportDataEntity = new ReportDataEntity();
            reasonForFailureReportDataEntity.setHeaderName(ReCAPConstants.REASON_FOR_FAILURE);
            reasonForFailureReportDataEntity.setHeaderValue(deAccessionDBResponseEntity.getReasonForFailure());
            reportDataEntities.add(reasonForFailureReportDataEntity);
        }

        reportEntity.setReportDataEntities(reportDataEntities);
        return reportEntity;
    }

    private DeAccessionDBResponseEntity prepareSuccessResponse(String itemBarcode, JSONObject itemEntity, List<Integer> holdingIds, List<Integer> bibliographicIds) throws JSONException {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode(itemBarcode);
        deAccessionDBResponseEntity.setStatus(ReCAPConstants.SUCCESS);
        populateDeAccessionDBResponseEntity(itemEntity, deAccessionDBResponseEntity);
        deAccessionDBResponseEntity.setHoldingIds(holdingIds);
        deAccessionDBResponseEntity.setBibliographicIds(bibliographicIds);
        return deAccessionDBResponseEntity;
    }

    private DeAccessionDBResponseEntity prepareFailureResponse(String itemBarcode, String reasonForFailure, JSONObject itemEntity) {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode(itemBarcode);
        deAccessionDBResponseEntity.setStatus(ReCAPConstants.FAILURE);
        deAccessionDBResponseEntity.setReasonForFailure(reasonForFailure);
        if (itemEntity != null) {
            try {
                populateDeAccessionDBResponseEntity(itemEntity, deAccessionDBResponseEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return deAccessionDBResponseEntity;
    }

    private void populateDeAccessionDBResponseEntity(JSONObject itemEntity, DeAccessionDBResponseEntity deAccessionDBResponseEntity) throws JSONException {
        JSONObject institutionEntity = itemEntity.getJSONObject("institutionEntity");
        if (institutionEntity != null) {
            deAccessionDBResponseEntity.setInstitutionCode(institutionEntity.getString("institutionCode"));
        }
        JSONObject collectionGroupEntity = itemEntity.getJSONObject("collectionGroupEntity");
        if (collectionGroupEntity != null) {
            deAccessionDBResponseEntity.setCollectionGroupCode(collectionGroupEntity.getString("collectionGroupCode"));
        }
        deAccessionDBResponseEntity.setItemId(itemEntity.getInt("itemId"));
        JSONArray bibliographicEntities = itemEntity.getJSONArray("bibliographicEntities");
        List<String> owningInstitutionBibIds = new ArrayList<>();
        for (int i = 0; i < bibliographicEntities.length(); i++) {
            JSONObject bibliographicEntity = bibliographicEntities.getJSONObject(i);
            String owningInstitutionBibId = bibliographicEntity.getString("owningInstitutionBibId");
            owningInstitutionBibIds.add(owningInstitutionBibId);
        }
        deAccessionDBResponseEntity.setOwningInstitutionBibIds(owningInstitutionBibIds);
    }

    private List<Integer> processBibs(JSONArray bibliographicEntities) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        List<Integer> bibliographicIds = new ArrayList<>();
        for (int i = 0; i < bibliographicEntities.length(); i++) {
            JSONObject bibliographicEntity = bibliographicEntities.getJSONObject(i);
            Integer owningInstitutionId = bibliographicEntity.getInt("owningInstitutionId");
            String owningInstitutionBibId = bibliographicEntity.getString("owningInstitutionBibId");
            Long nonDeletedItemCount = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "bibliographic/search/getNonDeletedItemsCount?owningInstitutionId={owningInstitutionId}&owningInstitutionBibId={owningInstitutionBibId}", Long.class, owningInstitutionId, owningInstitutionBibId);
            if (nonDeletedItemCount == 1) {
                bibliographicIds.add(bibliographicEntity.getInt("bibliographicId"));
            }
        }
        if (CollectionUtils.isNotEmpty(bibliographicIds)) {
            restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "bibliographic/search/markBibsAsDeleted?bibliographicIds=" + StringUtils.join(bibliographicIds, ","), int.class);
        }
        return bibliographicIds;
    }

    private List<Integer> processHoldings(JSONArray holdingsEntities) throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        List<Integer> holdingIds = new ArrayList<>();
        for (int i = 0; i < holdingsEntities.length(); i++) {
            JSONObject holdingsEntity = holdingsEntities.getJSONObject(i);
            Integer owningInstitutionId = holdingsEntity.getInt("owningInstitutionId");
            String owningInstitutionHoldingsId = holdingsEntity.getString("owningInstitutionHoldingsId");
            Long nonDeletedItemsCount = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "holdings/search/getNonDeletedItemsCount?owningInstitutionId={owningInstitutionId}&owningInstitutionHoldingsId={owningInstitutionHoldingsId}", Long.class, owningInstitutionId, owningInstitutionHoldingsId);
            if (nonDeletedItemsCount == 1) {
                holdingIds.add(holdingsEntity.getInt("holdingsId"));
            }
        }
        if (CollectionUtils.isNotEmpty(holdingIds)) {
            restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "holdings/search/markHoldingsAsDeleted?holdingIds=" + StringUtils.join(holdingIds, ","), int.class);
        }
        return holdingIds;
    }

    public void deAccessionItemsInSolr(List<Integer> bibIds, List<Integer> holdingsIds, List<Integer> itemIds) {
        RestTemplate restTemplate = new RestTemplate();
        if (CollectionUtils.isNotEmpty(bibIds)) {
            restTemplate.getForObject(serverProtocol + scsbSolrClientUrl + "bibSolr/search/deleteByBibIdIn?bibIds=" + StringUtils.join(bibIds, ","), int.class);
        }
        if (CollectionUtils.isNotEmpty(holdingsIds)) {
            restTemplate.getForObject(serverProtocol + scsbSolrClientUrl + "holdingsSolr/search/deleteByHoldingsIdIn?holdingsIds=" + StringUtils.join(holdingsIds, ","), int.class);
        }
        if (CollectionUtils.isNotEmpty(itemIds)) {
            restTemplate.getForObject(serverProtocol + scsbSolrClientUrl + "itemSolr/search/deleteByItemIdIn?itemIds=" + StringUtils.join(itemIds, ","), int.class);
        }
    }

    public void checkAndCancelHoldsIfExists(Map<String, Integer> ownInstAndItemIdMap) {
        RestTemplate restTemplate = new RestTemplate();
        if (ownInstAndItemIdMap != null && ownInstAndItemIdMap.size() > 0) {
            try {
                Collection<Integer> itemIds = ownInstAndItemIdMap.values();
                String responseObject = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "requestItem/search/findByItemIdIn?itemIds=" + StringUtils.join(itemIds, ","), String.class);
                JSONObject jsonResponse = new JSONObject(responseObject).getJSONObject("_embedded");
                JSONArray requestItemEntities = jsonResponse.getJSONArray("requestItem");
                if (requestItemEntities.length() > 0) {
                    restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "requestItem/search/deleteByItemIdIn?itemIds=" + StringUtils.join(itemIds, ","), int.class);
                    //TODO
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
