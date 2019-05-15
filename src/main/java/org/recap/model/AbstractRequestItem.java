package org.recap.model;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@SuperBuilder
public abstract class AbstractRequestItem {
    private List<String> itemBarcodes;
    private String itemOwningInstitution=""; // PUL, CUL, NYPL
}
