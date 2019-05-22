package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class AccessionRequest {
    private String itemBarcode;
    private String customerCode;
}
