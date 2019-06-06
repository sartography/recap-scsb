package org.recap.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BibItemAvailabityStatusRequest {
    private String bibliographicId;
    private String institutionId;
}
