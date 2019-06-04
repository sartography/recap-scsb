package org.recap.model.transfer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemTransferResponse {
    private String message;
    private ItemTransferRequest itemTransferRequest;
}
