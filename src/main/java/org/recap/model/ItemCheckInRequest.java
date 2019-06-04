package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ItemCheckInRequest", description = "Model for Requesting Checkin request")
public class ItemCheckInRequest extends AbstractRequestItem {
    @ApiModelProperty(name = "patronIdentifier", position = 2, required = true)
    private String patronIdentifier;
}
