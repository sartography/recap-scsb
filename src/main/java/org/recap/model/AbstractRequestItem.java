package org.recap.model;

import java.util.List;

import lombok.Data;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class AbstractRequestItem {
    @Singular private List<String> itemBarcodes;
    private String itemOwningInstitution=""; // PUL, CUL, NYPL
}
