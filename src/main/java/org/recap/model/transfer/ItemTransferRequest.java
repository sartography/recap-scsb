package org.recap.model.transfer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemTransferRequest {
    private ItemSource source;
    private ItemDestination destination;
}
