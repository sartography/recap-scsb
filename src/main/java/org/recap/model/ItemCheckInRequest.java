package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ApiModel(value = "ItemCheckInRequest", description = "Model for Requesting Checkin request")
public class ItemCheckInRequest extends AbstractRequestItem {

    @ApiModelProperty(name = "patronIdentifier", position = 2, required = true)
    private String patronIdentifier;
}
