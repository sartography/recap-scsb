package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import lombok.Data;
import lombok.Singular;
import lombok.Builder;

@Data
@Builder
@ApiModel(value="SearchResultRow", description="Model for Displaying Search Result")
public class SearchResultRow {

    @ApiModelProperty(name= "bibId", value= "Bibliographic Id",position = 0)
    private Integer bibId;
    @ApiModelProperty(name= "title", value= "Book Title",position = 1)
    private String title;
    @ApiModelProperty(name= "author", value= "Author",position = 2)
    private String author;
    @ApiModelProperty(name= "publisher", value= "Publisher",position = 3)
    private String publisher;
    @ApiModelProperty(name= "publisherDate", value= "Publisher Date",position = 4)
    private String publisherDate;
    @ApiModelProperty(name= "owningInstitution", value= "Owning Institution",position = 5)
    private String owningInstitution;
    @ApiModelProperty(name= "customerCode", value= "Customer Code",position = 6)
    private String customerCode;
    @ApiModelProperty(name= "collectionGroupDesignation", value= "Collection Group Designation",position = 7)
    private String collectionGroupDesignation;
    @ApiModelProperty(name= "useRestriction", value= "use Restriction",position = 8)
    private String useRestriction;
    @ApiModelProperty(name= "barcode", value= "barcode",position = 9)
    private String barcode;
    @ApiModelProperty(name= "summary Holdings", value= "summary Holdings",position = 10)
    private String summaryHoldings;
    @ApiModelProperty(name= "availability", value= "availability",position = 11)
    private String availability;
    @ApiModelProperty(name= "leaderMaterialType", value= "Leader Material Type",position = 12)
    private String leaderMaterialType;
    @ApiModelProperty(name= "selected", value= "selected",position = 13)
    @Builder.Default private boolean selected = false;
    @ApiModelProperty(name= "showItems", value= "Show Items",position = 14)
    @Builder.Default private boolean showItems = false;
    @ApiModelProperty(name= "selectAllItems", value= "Select All Items",position = 15)
    @Builder.Default private boolean selectAllItems = false;
    @ApiModelProperty(name= "searchItemResultRows", value= "Item Results",position = 16)
    @Singular private List<SearchItemResultRow> searchItemResultRows;
    @ApiModelProperty(name= "itemId", value= "Item Id",position = 17)
    private Integer itemId;
    @ApiModelProperty(name= "owningInstitutionBibId", value= "Owning Institution Bib Id",position = 18)
    private String owningInstitutionBibId;
    @ApiModelProperty(name= "owningInstitutionHoldingsId", value= "Owning Institution Holdings Id",position = 19)
    private String owningInstitutionHoldingsId;
    @ApiModelProperty(name= "owningInstitutionItemId", value= "Owning Institution Item Id",position = 20)
    private String owningInstitutionItemId;

    private Integer requestPosition;
    private Integer patronBarcode;
    private String requestingInstitution;
    private String deliveryLocation;
    private String requestType;
    private String requestNotes;
}