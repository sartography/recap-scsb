package org.recap.model;


import java.util.List;

import lombok.Data;
import lombok.Singular;
import lombok.Builder;

@Data
@Builder
public class ItemResponseInformation {
    private String patronBarcode;
    @Singular private List<String> itemBarcodes;
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
