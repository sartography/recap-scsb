package org.recap.model;

import lombok.Data;

@Data
public abstract class AbstractResponseItem {
    private String itemBarcode;
    private String itemOwningInstitution = ""; // PUL, CUL, NYPL
    private String screenMessage;
    private boolean success;
    private String esipDataIn;
    private String esipDataOut;
}
