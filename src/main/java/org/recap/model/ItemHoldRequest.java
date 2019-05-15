package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ItemHoldRequest extends AbstractRequestItem {
    private String patronIdentifier;
    private String bibId;
    private String pickupLocation;
    private String trackingId; // NYPL - trackingId
    private String title; // NYPL - title
    private String author; // NYPL - author
    private String callNumber; // NYPL - callNumber
}
