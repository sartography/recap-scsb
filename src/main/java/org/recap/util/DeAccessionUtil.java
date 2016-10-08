package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.recap.ReCAPConstants;
import org.recap.model.DeAccessionDBResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    public List<DeAccessionDBResponseEntity> deAccessionItemsInDB(String itemBarcodes) {
        RestTemplate restTemplate = new RestTemplate();
        List<String> itemBarcodeList = Arrays.asList(itemBarcodes.split(","));
        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = new ArrayList<>();
        DeAccessionDBResponseEntity deAccessionDBResponseEntity;
        List<String> responseItemBarcodeList = new ArrayList<>();

        String responseObject = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "item/search/findByBarcodeIn?barcodes=" + itemBarcodes, String.class);
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

                        new RestTemplate().getForObject(serverProtocol + scsbPersistenceUrl + "item/search/markItemAsDeleted?itemId=" + itemId, int.class);

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
        Map<String, String> owningInstitutionBibIdWithTitles = new HashMap<>();
        for (int i = 0; i < bibliographicEntities.length(); i++) {
            JSONObject bibliographicEntity = bibliographicEntities.getJSONObject(i);
            String owningInstitutionBibId = bibliographicEntity.getString("owningInstitutionBibId");
            String title = "title";//TODO
            owningInstitutionBibIdWithTitles.put(owningInstitutionBibId, title);
        }
        deAccessionDBResponseEntity.setOwningInstitutionBibIdWithTitle(owningInstitutionBibIdWithTitles);
    }

    private List<Integer> processBibs(JSONArray bibliographicEntities) throws JSONException {
        List<Integer> bibliographicIds = new ArrayList<>();
        for (int i = 0; i < bibliographicEntities.length(); i++) {
            JSONObject bibliographicEntity = bibliographicEntities.getJSONObject(i);
            Integer owningInstitutionId = bibliographicEntity.getInt("owningInstitutionId");
            String owningInstitutionBibId = bibliographicEntity.getString("owningInstitutionBibId");
            Long nonDeletedItemCount = new RestTemplate().getForObject(serverProtocol + scsbPersistenceUrl + "bibliographic/search/getNonDeletedItemsCount?owningInstitutionId={owningInstitutionId}&owningInstitutionBibId={owningInstitutionBibId}", Long.class, owningInstitutionId, owningInstitutionBibId);
            if (nonDeletedItemCount == 1) {
                bibliographicIds.add(bibliographicEntity.getInt("bibliographicId"));
            }
        }
        if (CollectionUtils.isNotEmpty(bibliographicIds)) {
            new RestTemplate().getForObject(serverProtocol + scsbPersistenceUrl + "bibliographic/search/markBibsAsDeleted?bibliographicIds=" + StringUtils.join(bibliographicIds, ","), int.class);
        }
        return bibliographicIds;
    }

    private List<Integer> processHoldings(JSONArray holdingsEntities) throws JSONException {
        List<Integer> holdingIds = new ArrayList<>();
        for (int i = 0; i < holdingsEntities.length(); i++) {
            JSONObject holdingsEntity = holdingsEntities.getJSONObject(i);
            Integer owningInstitutionId = holdingsEntity.getInt("owningInstitutionId");
            String owningInstitutionHoldingsId = holdingsEntity.getString("owningInstitutionHoldingsId");
            Long nonDeletedItemsCount = new RestTemplate().getForObject(serverProtocol + scsbPersistenceUrl + "holdings/search/getNonDeletedItemsCount?owningInstitutionId={owningInstitutionId}&owningInstitutionHoldingsId={owningInstitutionHoldingsId}", Long.class, owningInstitutionId, owningInstitutionHoldingsId);
            if (nonDeletedItemsCount == 1) {
                holdingIds.add(holdingsEntity.getInt("holdingsId"));
            }
        }
        if (CollectionUtils.isNotEmpty(holdingIds)) {
            new RestTemplate().getForObject(serverProtocol + scsbPersistenceUrl + "holdings/search/markHoldingsAsDeleted?holdingIds=" + StringUtils.join(holdingIds, ","), int.class);
        }
        return holdingIds;
    }
}
