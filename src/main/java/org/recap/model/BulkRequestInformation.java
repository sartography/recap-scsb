package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class BulkRequestInformation {
    private String requestingInstitution;
    private String patronBarcode;
}
