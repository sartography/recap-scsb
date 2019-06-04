package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemHoldRequest extends AbstractRequestItem {
    private String patronIdentifier;
    private String bibId;
    private String pickupLocation;
    private String trackingId; // NYPL - trackingId
    private String title; // NYPL - title
    private String author; // NYPL - author
    private String callNumber; // NYPL - callNumber
}
