package org.recap.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkRequestResponse {
    private Integer bulkRequestId;
    private String screenMessage;
    private boolean success;
}
