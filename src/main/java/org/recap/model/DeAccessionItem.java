package org.recap.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeAccessionItem {
    private String itemBarcode;
    private String deliveryLocation;
}
