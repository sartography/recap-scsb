package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by sudhishk on 15/12/16.
 */
@ApiModel(value = "ItemCheckInRequest", description = "Model for Requesting Checkin request")
public class ItemCheckInRequest extends AbstractRequestItem {

    @ApiModelProperty(name = "patronIdentifier", position = 2, required = true)
    private String patronIdentifier;

    public String getPatronIdentifier() {
        return patronIdentifier;
    }

    public void setPatronIdentifier(String patronIdentifier) {
        this.patronIdentifier = patronIdentifier;
    }
}
