package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class DeAccessionItem {
    private String itemBarcode;
    private String deliveryLocation;
}
