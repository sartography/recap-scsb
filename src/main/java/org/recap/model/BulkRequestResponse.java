package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class BulkRequestResponse {
    private Integer bulkRequestId;
    private String screenMessage;
    private boolean success;
}
