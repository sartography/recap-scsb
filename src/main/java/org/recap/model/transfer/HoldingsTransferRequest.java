package org.recap.model.transfer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HoldingsTransferRequest {
    private Source source;
    private Destination destination;
}
