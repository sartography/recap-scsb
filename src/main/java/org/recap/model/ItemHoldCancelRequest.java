package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ItemHoldCancelRequest extends AbstractRequestItem{
    private String patronIdentifier;
    private String bibId;
    private String pickupLocation;
    private String trackingId; // NYPL - trackingId
}
