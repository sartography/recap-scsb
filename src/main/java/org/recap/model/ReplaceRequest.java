package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@Data
@EqualsAndHashCode
@Builder
public class ReplaceRequest {
    private String replaceRequestByType;
    private String requestStatus;
    private String requestIds;
    private String startRequestId;
    private String endRequestId;
    private String fromDate;
    private String toDate;
}
