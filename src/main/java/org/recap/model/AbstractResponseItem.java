package org.recap.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
public abstract class AbstractResponseItem {
    private String itemBarcode;
    private String itemOwningInstitution = ""; // PUL, CUL, NYPL
    private String screenMessage;
    private boolean success;
    private String esipDataIn;
    private String esipDataOut;
}
