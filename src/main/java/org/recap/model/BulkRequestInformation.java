package org.recap.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkRequestInformation {
    private String requestingInstitution;
    private String patronBarcode;
}
