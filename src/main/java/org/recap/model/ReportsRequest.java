package org.recap.model;

import java.util.List;

import lombok.Data;
import lombok.Singular;
import lombok.Builder;

@Data
@Builder
public class ReportsRequest {
    private String accessionDeaccessionFromDate;
    private String accessionDeaccessionToDate;
    @Singular private List<String> owningInstitutions;
    @Singular private List<String> collectionGroupDesignations;
    private String deaccessionOwningInstitution;
    private String incompleteRequestingInstitution;
    private boolean export;
    @Builder.Default private Integer pageNumber = 0;
    @Builder.Default private Integer pageSize = 10;
    @Builder.Default private Integer incompletePageNumber = 0;
    @Builder.Default private Integer incompletePageSize = 10;
}
