package org.recap.model;

import java.util.List;

import lombok.Data;
import lombok.Singular;
import lombok.Builder;

@Data
@Builder
public class SearchRecordsResponse {
	@Singular
	private List<SearchResultRow> searchResultRows;
	@Builder.Default
	private Integer totalPageCount = 0;
	@Builder.Default
	private String totalBibRecordsCount = "0";
	@Builder.Default
	private String totalItemRecordsCount = "0";
	@Builder.Default
	private String totalRecordsCount = "0";
	private boolean showTotalCount;
	private String errorMessage;
}
