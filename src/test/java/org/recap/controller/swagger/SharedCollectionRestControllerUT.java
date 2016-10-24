package org.recap.controller.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.recap.controller.BaseControllerUT;
import org.recap.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
public class SharedCollectionRestControllerUT extends BaseControllerUT {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.persistence.url}")
    String scsbPersistenceUrl;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Test
    public void itemAvailabilityStatus() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);

        RestTemplate restTemplate = new RestTemplate();
        String createUrl = serverProtocol + scsbPersistenceUrl + "bibliographic/create";
        Integer bibliographicId = restTemplate.postForObject(createUrl, bibliographicEntity, Integer.class);
        assertNotNull(bibliographicId);

        MvcResult savedResult = this.mockMvc.perform(get("/sharedCollection/itemAvailabilityStatus")
                .headers(getHttpHeaders())
                .param("itemBarcode", itemBarcode))
                .andExpect(status().isOk())
                .andReturn();

        String itemStatus = savedResult.getResponse().getContentAsString();
        assertNotNull(itemStatus);
        assertEquals(itemStatus, "Available");
    }

    @Test
    public void deAccession() throws Exception {
        Random random = new Random();
        String itemBarcode = String.valueOf(random.nextInt());
        BibliographicEntity bibliographicEntity = getBibEntityWithHoldingsAndItem(itemBarcode);

        RestTemplate restTemplate = new RestTemplate();
        String createUrl = serverProtocol + scsbPersistenceUrl + "bibliographic/create";
        Integer bibliographicId = restTemplate.postForObject(createUrl, bibliographicEntity, Integer.class);
        assertNotNull(bibliographicId);

        BibliographicEntity savedBibliographicEntity = restTemplate.getForObject(serverProtocol + scsbPersistenceUrl + "bibliographic/search/findByBibliographicId?bibliographicId={bibliographicId}", BibliographicEntity.class, bibliographicId);
        assertNotNull(savedBibliographicEntity);

        restTemplate.postForLocation(serverProtocol + scsbSolrClientUrl + "/solrIndexer/indexByBibliographicId", bibliographicId);

        Bib bib = restTemplate.getForObject(serverProtocol + scsbSolrClientUrl + "bibSolr/search/findByBibId?bibId=" + bibliographicId, Bib.class);
        assertNotNull(bib);
        assertEquals(bib.getBibId(), bibliographicId);

        DeAccessionRequest deAccessionRequest = new DeAccessionRequest();
        deAccessionRequest.setItemBarcodes(Arrays.asList(itemBarcode));

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/sharedCollection/deAccession")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(deAccessionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "{\"" + itemBarcode + "\":\"Success\"}");
    }

    @Test
    public void accession() throws Exception {

        AccessionRequest accessionRequest = new AccessionRequest();
        accessionRequest.setCustomerCode("PB");
        accessionRequest.setItemBarcode("32101095533293");

        ObjectMapper objectMapper = new ObjectMapper();
        MvcResult mvcResult = this.mockMvc.perform(post("/sharedCollection/accession")
                .headers(getHttpHeaders())
                .contentType(contentType)
                .content(objectMapper.writeValueAsString(accessionRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertEquals(result, "Success");
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
        itemEntity.setOwningInstitutionItemId(String.valueOf(random.nextInt()));
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

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api_key", "recap");
        return headers;
    }

}