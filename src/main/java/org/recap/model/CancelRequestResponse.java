package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class CancelRequestResponse {
    private String screenMessage;
    private boolean success;
}
