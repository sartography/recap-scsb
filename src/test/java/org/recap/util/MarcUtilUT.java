package org.recap.util;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.marc4j.marc.Record;
import org.recap.marc.BibMarcRecord;
import org.recap.marc.HoldingsMarcRecord;
import org.recap.marc.ItemMarcRecord;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by chenchulakshmig on 17/10/16.
 */
public class MarcUtilUT {

    private List<Record> getRecords() throws Exception{
        URL resource = getClass().getResource("singleRecord.xml");
        File file = new File(resource.toURI());
        String marcXmlString = FileUtils.readFileToString(file, "UTF-8");
        MarcUtil marcUtil = new MarcUtil();
        return marcUtil.readMarcXml(marcXmlString);
    }

    @Test
    public void readMarcXml() throws Exception {
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        assertNotNull(record);
    }

    @Test
    public void getDataFieldValue() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        assertNotNull(record);
        String fieldValue = marcUtil.getDataFieldValue(record, "876", 'p');
        assertEquals(fieldValue, "32101095533293");
    }

    @Test
    public void getControlFieldValue() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        assertNotNull(record);
        String controlFieldValue = marcUtil.getControlFieldValue(record, "001");
        assertEquals(controlFieldValue, "9919400");
    }

    @Test
    public void isSubFieldExists() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        assertNotNull(record);
        assertTrue(marcUtil.isSubFieldExists(record, "245"));
        assertFalse(marcUtil.isSubFieldExists(record, "102"));
    }

    @Test
    public void getInd1() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        assertNotNull(record);
        Character ind1 = marcUtil.getInd1(record, "876", 'h');
        assertTrue(ind1 == '0');
    }

    @Test
    public void buildBibMarcRecord() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        assertNotNull(record);

        BibMarcRecord bibMarcRecord = marcUtil.buildBibMarcRecord(record);
        assertNotNull(bibMarcRecord);
        Record bibRecord = bibMarcRecord.getBibRecord();
        assertNotNull(bibRecord);
        assertFalse(marcUtil.isSubFieldExists(record, "852"));
        assertFalse(marcUtil.isSubFieldExists(record, "876"));

        List<HoldingsMarcRecord> holdingsMarcRecords = bibMarcRecord.getHoldingsMarcRecords();
        assertNotNull(holdingsMarcRecords);
        assertTrue(holdingsMarcRecords.size() == 1);
        HoldingsMarcRecord holdingsMarcRecord = holdingsMarcRecords.get(0);
        assertNotNull(holdingsMarcRecord);
        Record holdingsRecord = holdingsMarcRecord.getHoldingsRecord();
        assertNotNull(holdingsRecord);
        assertTrue(marcUtil.isSubFieldExists(holdingsRecord, "852"));
        assertFalse(marcUtil.isSubFieldExists(holdingsRecord, "245"));
        assertFalse(marcUtil.isSubFieldExists(holdingsRecord, "876"));

        List<ItemMarcRecord> itemMarcRecordList = holdingsMarcRecord.getItemMarcRecordList();
        assertNotNull(itemMarcRecordList);
        assertTrue(itemMarcRecordList.size() == 1);
        ItemMarcRecord itemMarcRecord = itemMarcRecordList.get(0);
        assertNotNull(itemMarcRecord);
        Record itemRecord = itemMarcRecord.getItemRecord();
        assertNotNull(itemRecord);
        assertFalse(marcUtil.isSubFieldExists(itemRecord, "852"));
        assertFalse(marcUtil.isSubFieldExists(itemRecord, "245"));
        assertTrue(marcUtil.isSubFieldExists(itemRecord, "876"));
    }

    @Test
    public void writeMarcXml() throws Exception {
        MarcUtil marcUtil = new MarcUtil();
        List<Record> records = getRecords();
        assertNotNull(records);
        assertEquals(records.size(), 1);
        Record record = records.get(0);
        assertNotNull(record);

        String content = marcUtil.writeMarcXml(record);
        assertNotNull(content);
        assertTrue(content.length() > 0);
    }

}