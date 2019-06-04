package org.recap.model.transfer;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransferRequest {
    private String institution;
    private List<HoldingsTransferRequest> holdingTransfers;
    private List<ItemTransferRequest> itemTransfers;
}
