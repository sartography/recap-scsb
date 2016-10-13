package org.recap;

import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.MarcXmlReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 13/10/16.
 */
public class BibInfoUT extends BaseTestCase {

    @Test
    public void getBibInfoByBarcode() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String barcode = "32101095533293";
        String response = restTemplate.getForObject("https://bibdata-dev.princeton.edu/barcode/" + barcode, String.class);
        assertNotNull(response);
        List<Record> records = readMarcXml(response);
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        DataField dataField = (DataField) record.getVariableField("876");
        assertNotNull(dataField);
        Subfield subfield = dataField.getSubfield('p');
        assertNotNull(subfield);
        assertEquals(subfield.getData(), barcode);
    }

    @Test
    public void getBoundWithBibInfoByBarcode() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String barcode = "32101070300312";
        String response = restTemplate.getForObject("https://bibdata-dev.princeton.edu/barcode/" + barcode, String.class);
        assertNotNull(response);
        List<Record> records = readMarcXml(response);
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

    private List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
        }
        return recordList;
    }
}
