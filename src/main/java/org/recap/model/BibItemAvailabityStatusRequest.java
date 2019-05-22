package org.recap.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class BibItemAvailabityStatusRequest {
    private String bibliographicId;
    private String institutionId;
}
