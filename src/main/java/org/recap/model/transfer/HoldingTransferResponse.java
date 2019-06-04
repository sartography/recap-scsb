package org.recap.model.transfer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldingTransferResponse {
    private String message;
    private HoldingsTransferRequest holdingsTransferRequest;
}
