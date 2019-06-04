package org.recap.model.transfer;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class TransferResponse {
    private String message;
    @Singular
    private List<HoldingTransferResponse> holdingTransferResponses;
    @Singular
    private List<ItemTransferResponse> itemTransferResponses;
}
