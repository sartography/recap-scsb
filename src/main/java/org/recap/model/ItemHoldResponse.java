package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemHoldResponse extends AbstractResponseItem {
    private boolean available;
    private String transactionDate;
    private String institutionID;
    private String patronIdentifier;
    private String titleIdentifier;
    private String expirationDate;
    private String pickupLocation;
    private String queuePosition;
    private String bibId;
    private String ISBN;
    private String LCCN;
    private String trackingId;
    private String jobId;
    private String updatedDate;
    private String createdDate;
}
