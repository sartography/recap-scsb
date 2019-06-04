package org.recap.model.transfer;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class Destination {
    private String owningInstitutionBibId;
    private String owningInstitutionHoldingsId;
}
