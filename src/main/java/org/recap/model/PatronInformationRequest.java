package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by sudhishk on 26/12/16.
 */
@ApiModel(value = "PatronInformationRequest", description = "Model for Requesting Patron Information")
public class PatronInformationRequest{

    @ApiModelProperty(name = "patronIdentifier", position = 0, required = true)
    private String patronIdentifier = "";
    @ApiModelProperty(name = "itemOwningInstitution", position = 1, required = true)
    private String itemOwningInstitution=""; // PUL, CUL, NYPL

    public String getPatronIdentifier() {
        return patronIdentifier;
    }

    public void setPatronIdentifier(String patronIdentifier) {
        this.patronIdentifier = patronIdentifier;
    }

    public String getItemOwningInstitution() {
        return itemOwningInstitution;
    }

    public void setItemOwningInstitution(String itemOwningInstitution) {
        this.itemOwningInstitution = itemOwningInstitution;
    }
}
