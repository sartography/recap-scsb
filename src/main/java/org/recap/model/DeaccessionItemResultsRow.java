package org.recap.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeaccessionItemResultsRow {
    private Integer itemId;
    private String deaccessionDate;
    private String title;
    private String deaccessionOwnInst;
    private String itemBarcode;
    private String cgd;
    private String deaccessionNotes;
    private String deaccessionCreatedBy;
}
