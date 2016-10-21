package org.recap;

import org.junit.Test;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.recap.etl.MarcToBibEntityConverter;
import org.recap.util.AccessionService;
import org.recap.util.MarcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 13/10/16.
 */
public class BibInfoUT extends BaseTestCase {

    @Value("${server.protocol}")
    String serverProtocol;

    @Value("${scsb.persistence.url}")
    String scsbPersistenceUrl;

    @Value("${scsb.solr.client.url}")
    String scsbSolrClientUrl;

    @Autowired
    MarcToBibEntityConverter marcToBibEntityConverter;

    @Autowired
    AccessionService accessionService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void processNewBibNewHoldingsNewItemByItemBarcode() throws Exception {
        String itemBarcode = "32101095533293";
        String customerCode = "PB";

        String owningInstitution = accessionService.getOwningInstitution(customerCode);
        assertNotNull(owningInstitution);
        assertTrue(owningInstitution.equalsIgnoreCase("PUL"));

        String response = accessionService.processRequest(itemBarcode, owningInstitution);
        assertNotNull(response);
        assertTrue(response.equalsIgnoreCase(ReCAPConstants.SUCCESS));
    }

    @Test
    public void getBoundWithBibInfoByBarcode() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String barcode = "32101070300312";
        String response = restTemplate.getForObject("https://bibdata-dev.princeton.edu/barcode/" + barcode, String.class);
        assertNotNull(response);
        List<Record> records = new MarcUtil().readMarcXml(response);
        assertNotNull(records);
        assertTrue(records.size() > 1);
        for (Record record : records) {
            DataField dataField = (DataField) record.getVariableField("876");
            assertNotNull(dataField);
            Subfield subfield = dataField.getSubfield('p');
            assertNotNull(subfield);
            assertEquals(subfield.getData(), barcode);
        }
    }

}
