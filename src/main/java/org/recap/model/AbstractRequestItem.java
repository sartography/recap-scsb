package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Created by sudhishk on 15/12/16.
 */
@ApiModel(value = "AbstractRequestItem", description = "Model for Request")
public abstract class AbstractRequestItem {

    @ApiModelProperty(name = "itemBarcodes", position = 0, required = true)
    private List<String> itemBarcodes;

    @ApiModelProperty(name = "itemOwningInstitution", position = 1, required = true)
    private String itemOwningInstitution=""; // PUL, CUL, NYPL

    public List<String> getItemBarcodes() {
        return itemBarcodes;
    }

    public void setItemBarcodes(List<String> itemBarcodes) {
        this.itemBarcodes = itemBarcodes;
    }

    public String getItemOwningInstitution() {
        return itemOwningInstitution;
    }

    public void setItemOwningInstitution(String itemOwningInstitution) {
        this.itemOwningInstitution = itemOwningInstitution;
    }
}
