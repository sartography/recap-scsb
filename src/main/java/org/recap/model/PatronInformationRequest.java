package org.recap.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Builder;

@Data
@EqualsAndHashCode
@Builder
@ApiModel(value = "PatronInformationRequest", description = "Model for Requesting Patron Information")
public class PatronInformationRequest{
    @ApiModelProperty(name = "patronIdentifier", position = 0, required = true)
    private String patronIdentifier;
    @ApiModelProperty(name = "itemOwningInstitution", position = 1, required = true)
    private String itemOwningInstitution; // PUL, CUL, NYPL
}
