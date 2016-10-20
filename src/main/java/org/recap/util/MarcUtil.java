package org.recap.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marc4j.MarcReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.*;
import org.recap.marc.BibMarcRecord;
import org.recap.marc.HoldingsMarcRecord;
import org.recap.marc.ItemMarcRecord;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchulakshmig on 17/10/16.
 */
@Component
public class MarcUtil {

    public List<Record> readMarcXml(String marcXmlString) {
        List<Record> recordList = new ArrayList<>();
        InputStream in = new ByteArrayInputStream(marcXmlString.getBytes());
        MarcReader reader = new MarcXmlReader(in);
        while (reader.hasNext()) {
            Record record = reader.next();
            recordList.add(record);
        }
        return recordList;
    }

    public String getDataFieldValue(Record record, String field, char subField) {
        DataField dataField = getDataField(record, field);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield(subField);
            if (subfield != null) {
                return subfield.getData();
            }
        }
        return null;
    }

    private String getDataFieldValue(DataField dataField, char subField) {
        Subfield subfield = dataField.getSubfield(subField);
        if (subfield != null) {
            return subfield.getData();
        }
        return null;
    }

    public String getControlFieldValue(Record record, String field) {
        List<ControlField> controlFields = record.getControlFields();
        if (CollectionUtils.isNotEmpty(controlFields)) {
            for (ControlField controlField : controlFields) {
                if (controlField != null && controlField.getTag().equalsIgnoreCase(field)) {
                    return controlField.getData();
                }
            }
        }
        return null;
    }

    public boolean isSubFieldExists(Record record, String field) {
        DataField dataField = getDataField(record, field);
        if (dataField != null) {
            List<Subfield> subfields = dataField.getSubfields();
            if (CollectionUtils.isNotEmpty(subfields)) {
                for (Subfield subfield : subfields) {
                    String data = subfield.getData();
                    if (StringUtils.isNotBlank(data)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public DataField getDataField(Record record, String field) {
        VariableField variableField = record.getVariableField(field);
        if (variableField != null) {
            DataField dataField = (DataField) variableField;
            if (dataField != null) {
                return dataField;
            }
        }
        return null;
    }

    public Character getInd1(Record record, String field, char subField) {
        DataField dataField = getDataField(record, field);
        if (dataField != null) {
            Subfield subfield = dataField.getSubfield(subField);
            if (subfield != null) {
                return dataField.getIndicator1();
            }
        }
        return null;
    }

    public BibMarcRecord buildBibMarcRecord(Record record) {
        Record bibRecord = record;
        List<VariableField> holdingsVariableFields = new ArrayList<>();
        List<VariableField> itemVariableFields = new ArrayList<>();
        String[] holdingsTags = {"852"};
        String[] itemTags = {"876"};
        holdingsVariableFields.addAll(bibRecord.getVariableFields(holdingsTags));
        itemVariableFields.addAll(bibRecord.getVariableFields(itemTags));

        if (CollectionUtils.isNotEmpty(holdingsVariableFields)) {
            for (VariableField holdingsVariableField : holdingsVariableFields) {
                bibRecord.removeVariableField(holdingsVariableField);
            }
        }
        if (CollectionUtils.isNotEmpty(itemVariableFields)) {
            for (VariableField itemVariableField : itemVariableFields) {
                bibRecord.removeVariableField(itemVariableField);
            }
        }
        BibMarcRecord bibMarcRecord = new BibMarcRecord();
        bibMarcRecord.setBibRecord(bibRecord);

        if (CollectionUtils.isNotEmpty(holdingsVariableFields)) {
            MarcFactory marcFactory = MarcFactory.newInstance();
            List<HoldingsMarcRecord> holdingsMarcRecordList = new ArrayList<>();
            for (VariableField holdingsVariableField : holdingsVariableFields) {
                List<ItemMarcRecord> itemMarcRecords = new ArrayList<>();
                DataField holdingsDataField = (DataField) holdingsVariableField;

                String holdingsData = getDataFieldValue(holdingsDataField, '0');
                if (StringUtils.isNotBlank(holdingsData)) {
                    HoldingsMarcRecord holdingsMarcRecord = new HoldingsMarcRecord();
                    Record holdingsRecord = marcFactory.newRecord();
                    holdingsRecord.getDataFields().add(holdingsDataField);
                    holdingsMarcRecord.setHoldingsRecord(holdingsRecord);

                    if (CollectionUtils.isNotEmpty(itemVariableFields)) {
                        for (VariableField itemVariableField : itemVariableFields) {
                            DataField itemDataField = (DataField) itemVariableField;
                            String itemData = getDataFieldValue(itemDataField, '0');
                            if (StringUtils.isNotBlank(itemData) && itemData.equalsIgnoreCase(holdingsData)) {
                                ItemMarcRecord itemMarcRecord = new ItemMarcRecord();
                                Record itemRecord = marcFactory.newRecord();
                                itemRecord.getDataFields().add(itemDataField);
                                itemMarcRecord.setItemRecord(itemRecord);
                                itemMarcRecords.add(itemMarcRecord);
                            }
                        }
                    }
                    holdingsMarcRecord.setItemMarcRecordList(itemMarcRecords);
                    holdingsMarcRecordList.add(holdingsMarcRecord);
                }
            }
            bibMarcRecord.setHoldingsMarcRecords(holdingsMarcRecordList);
        }
        return bibMarcRecord;
    }

    public String writeMarcXml(Record record) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MarcWriter marcWriter = new MarcXmlWriter(byteArrayOutputStream, true);
        marcWriter.write(record);
        marcWriter.close();
        String content = new String(byteArrayOutputStream.toByteArray());
        content = content.replaceAll("marcxml:", "");
        return content;
    }

}
