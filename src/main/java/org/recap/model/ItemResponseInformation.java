package org.recap.model;


import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@SuperBuilder
public class ItemResponseInformation {

    private String patronBarcode;
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
