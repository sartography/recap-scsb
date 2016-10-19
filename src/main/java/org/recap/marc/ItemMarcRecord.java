package org.recap.marc;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;

import java.util.List;

/**
 * Created by chenchulakshmig on 14/10/16.
 */
public class ItemMarcRecord {
    Record itemRecord;

    public Record getItemRecord() {
        return itemRecord;
    }

    public void setItemRecord(Record itemRecord) {
        this.itemRecord = itemRecord;
    }
}
