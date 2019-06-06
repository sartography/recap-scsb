package org.recap.model;


import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ItemResponseInformation {
    private String patronBarcode;
    @Singular
    private List<String> itemBarcodes;
    private String requestType;
    private String deliveryLocation;
    private String requestingInstitution;
    private String bibliographicId;
    private String expirationDate;
    private String screenMessage;
    private boolean success;
    private String emailAddress;
    private String titleIdentifier;
}
