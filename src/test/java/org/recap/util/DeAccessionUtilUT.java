package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.recap.BaseTestCase;
import org.recap.ReCAPConstants;
import org.recap.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 28/9/16.
 */
public class DeAccessionUtilUT extends BaseTestCase {

    @Autowired
    DeAccessionUtil deAccessionUtil;

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.persistence.url}")
    String scsbPersistenceUrl;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Test
    public void deAccessionItemsInDB() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);

        RestTemplate restTemplate = new RestTemplate();
        String createUrl = serverProtocol + scsbPersistenceUrl + "bibliographic/create";
        Integer bibliographicId = restTemplate.postForObject(createUrl, bibliographicEntity, Integer.class);
        assertNotNull(bibliographicId);

        List<DeAccessionDBResponseEntity> deAccessionDBResponseEntities = deAccessionUtil.deAccessionItemsInDB(Arrays.asList(itemBarcode));
        assertNotNull(deAccessionDBResponseEntities);
        assertTrue(deAccessionDBResponseEntities.size()==1);
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = deAccessionDBResponseEntities.get(0);
        assertNotNull(deAccessionDBResponseEntity);
        assertEquals(deAccessionDBResponseEntity.getStatus(), ReCAPConstants.SUCCESS);
    }

    @Test
    public void processAndSave() throws Exception {
        DeAccessionDBResponseEntity deAccessionDBResponseEntity = new DeAccessionDBResponseEntity();
        deAccessionDBResponseEntity.setBarcode("12345");
        deAccessionDBResponseEntity.setStatus(ReCAPConstants.FAILURE);
        deAccessionDBResponseEntity.setReasonForFailure(ReCAPConstants.ITEM_BARCDE_DOESNOT_EXIST);

        List<ReportEntity> reportEntities = deAccessionUtil.processAndSave(Arrays.asList(deAccessionDBResponseEntity));
        assertNotNull(reportEntities);
        assertTrue(reportEntities.size()==1);
        ReportEntity reportEntity = reportEntities.get(0);
        assertNotNull(reportEntity);

        assertNotNull(reportEntity.getReportDataEntities());
        assertTrue(reportEntity.getReportDataEntities().size()==4);
    }

    private BibliographicEntity getBibEntityWithHoldingsAndItem(String itemBarcode) throws Exception {
        Random random = new Random();
        File bibContentFile = getBibContentFile();
        File holdingsContentFile = getHoldingsContentFile();
        String sourceBibContent = FileUtils.readFileToString(bibContentFile, "UTF-8");
        String sourceHoldingsContent = FileUtils.readFileToString(holdingsContentFile, "UTF-8");

        BibliographicEntity bibliographicEntity = new BibliographicEntity();
        bibliographicEntity.setContent(sourceBibContent.getBytes());
        bibliographicEntity.setCreatedDate(new Date());
        bibliographicEntity.setLastUpdatedDate(new Date());
        bibliographicEntity.setCreatedBy("tst");
        bibliographicEntity.setLastUpdatedBy("tst");
        bibliographicEntity.setOwningInstitutionId(1);
        bibliographicEntity.setOwningInstitutionBibId(String.valueOf(random.nextInt()));
        bibliographicEntity.setDeleted(false);

        HoldingsEntity holdingsEntity = new HoldingsEntity();
        holdingsEntity.setContent(sourceHoldingsContent.getBytes());
        holdingsEntity.setCreatedDate(new Date());
        holdingsEntity.setLastUpdatedDate(new Date());
        holdingsEntity.setCreatedBy("tst");
        holdingsEntity.setLastUpdatedBy("tst");
        holdingsEntity.setOwningInstitutionId(1);
        holdingsEntity.setOwningInstitutionHoldingsId(String.valueOf(random.nextInt()));
        holdingsEntity.setDeleted(false);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setLastUpdatedDate(new Date());
        String owningInstitutionItemId = String.valueOf(random.nextInt());
        itemEntity.setOwningInstitutionItemId(owningInstitutionItemId);
        itemEntity.setOwningInstitutionId(1);
        itemEntity.setBarcode(itemBarcode);
        itemEntity.setCallNumber("x.12321");
        itemEntity.setCollectionGroupId(1);
        itemEntity.setCallNumberType("1");
        itemEntity.setCustomerCode("123");
        itemEntity.setCreatedDate(new Date());
        itemEntity.setCreatedBy("tst");
        itemEntity.setLastUpdatedBy("tst");
        itemEntity.setItemAvailabilityStatusId(1);
        itemEntity.setDeleted(false);

        holdingsEntity.setItemEntities(Arrays.asList(itemEntity));
        bibliographicEntity.setHoldingsEntities(Arrays.asList(holdingsEntity));
        bibliographicEntity.setItemEntities(Arrays.asList(itemEntity));

        return bibliographicEntity;
    }

    private File getBibContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("BibContent.xml");
        return new File(resource.toURI());
    }

    private File getHoldingsContentFile() throws URISyntaxException {
        URL resource = getClass().getResource("HoldingsContent.xml");
        return new File(resource.toURI());
    }

    @Test
    public void testDeAccessionItemsInSolr(){
        RestTemplate restTemplate = new RestTemplate();
        Integer bibId = 584671;
        Integer holdingId = 596099;
        Integer itemId = 825171;
        deAccessionUtil.deAccessionItemsInSolr(Arrays.asList(bibId),Arrays.asList(holdingId),Arrays.asList(itemId));
        Bib bib = restTemplate.getForObject(serverProtocol + scsbSolrClientUrl + "bibSolr/search/findByBibId?bibId=" + bibId, Bib.class);
        assertNotNull(bib);
        assertEquals(bib.isDeletedBib(),true);
    }

}