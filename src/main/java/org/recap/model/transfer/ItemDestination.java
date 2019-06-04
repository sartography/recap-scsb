package org.recap.model.transfer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ItemDestination extends Destination {
    private String owningInstitutionItemId;
}
