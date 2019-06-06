package org.recap.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ReportsResponse {
    private long accessionPrivatePulCount;
    private long accessionPrivateCulCount;
    private long accessionPrivateNyplCount;
    private long accessionSharedPulCount;
    private long accessionSharedCulCount;
    private long accessionSharedNyplCount;
    private long accessionOpenPulCount;
    private long accessionOpenCulCount;
    private long accessionOpenNyplCount;
    private long deaccessionPrivatePulCount;
    private long deaccessionPrivateCulCount;
    private long deaccessionPrivateNyplCount;
    private long deaccessionSharedPulCount;
    private long deaccessionSharedCulCount;
    private long deaccessionSharedNyplCount;
    private long deaccessionOpenPulCount;
    private long deaccessionOpenCulCount;
    private long deaccessionOpenNyplCount;
    private long openPulCgdCount;
    private long openCulCgdCount;
    private long openNyplCgdCount;
    private long sharedPulCgdCount;
    private long sharedCulCgdCount;
    private long sharedNyplCgdCount;
    private long privatePulCgdCount;
    private long privateCulCgdCount;
    private long privateNyplCgdCount;

    @Builder.Default
    private String totalRecordsCount = "0";
    @Builder.Default
    private Integer totalPageCount = 0;
    private String message;
    @Singular
    private List<DeaccessionItemResultsRow> deaccessionItemResultsRows;

    @Builder.Default
    private String incompleteTotalRecordsCount = "0";
    @Builder.Default
    private Integer incompleteTotalPageCount = 0;
    @Builder.Default
    private Integer incompletePageNumber = 0;
    @Builder.Default
    private Integer incompletePageSize = 10;
    @Singular
    private List<IncompleteReportResultsRow> incompleteReportResultsRows;
}
