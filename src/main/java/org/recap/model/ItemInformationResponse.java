package org.recap.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ItemInformationResponse extends AbstractResponseItem{
    private String expirationDate;
    private String titleIdentifier;
    private String dueDate;
    private String circulationStatus;
    private String securityMarker;
    private String feeType;
    private String transactionDate;
    private String holdQueueLength;
    private String holdPickupDate;
    private String recallDate;
    private String owner;
    private String mediaType;
    private String permanentLocation;
    private String currentLocation;
    private String bibID;
    private String ISBN;
    private String LCCN;
    private String currencyType;
    private String callNumber;
    private String itemType;
    private List<String> bibIds;
    private String source;
    private String createdDate;
    private String updatedDate;
    private String deletedDate;
    private boolean isDeleted;
}
