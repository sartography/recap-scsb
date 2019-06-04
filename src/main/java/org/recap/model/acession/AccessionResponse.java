package org.recap.model.acession;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessionResponse {
    private String itemBarcode;
    private String message;
}
