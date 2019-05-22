package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class IncompleteReportResultsRow {
    private String author;
    private String createdDate;
    private String title;
    private String owningInstitution;
    private String customerCode;
    private String barcode;
}
