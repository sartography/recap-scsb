package org.recap.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessionRequest {
    private String itemBarcode;
    private String customerCode;
}
