package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel(value = "SearchItemResultRow", description = "Model for Displaying Item Result")
public class SearchItemResultRow implements Comparable<SearchItemResultRow> {

    @ApiModelProperty(name = "callNumber", value = "Call Number", position = 0)
    private String callNumber;
    @ApiModelProperty(name = "chronologyAndEnum", value = "Chronology And Enum", position = 1)
    private String chronologyAndEnum;
    @ApiModelProperty(name = "customerCode", value = "Customer Code", position = 2)
    private String customerCode;
    @ApiModelProperty(name = "barcode", value = "barcode", position = 3)
    private String barcode;
    @ApiModelProperty(name = "useRestriction", value = "use Restriction", position = 4)
    private String useRestriction;
    @ApiModelProperty(name = "collectionGroupDesignation", value = "collection Group Designation", position = 5)
    private String collectionGroupDesignation;
    @ApiModelProperty(name = "availability", value = "Availability", position = 6)
    private String availability;
    @ApiModelProperty(name = "selectedItem", value = "selected Item", position = 7)
    @Builder.Default
    private boolean selectedItem = false;
    @ApiModelProperty(name = "itemId", value = "Item Id", position = 8)
    private Integer itemId;
    @ApiModelProperty(name = "owningInstitutionItemId", value = "Owning Institution Item Id", position = 9)
    private String owningInstitutionItemId;
    @ApiModelProperty(name = "owningInstitutionHoldingsId", value = "Owning Institution Holdings Id", position = 10)
    private String owningInstitutionHoldingsId;

    @Override
    public int compareTo(SearchItemResultRow searchItemResultRow) {
        if (null != this.getChronologyAndEnum() && null != searchItemResultRow && null != searchItemResultRow.getChronologyAndEnum()) {
            return this.getChronologyAndEnum().compareTo(searchItemResultRow.getChronologyAndEnum());
        }
        return 0;
    }
}
