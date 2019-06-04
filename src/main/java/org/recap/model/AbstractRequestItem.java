package org.recap.model;

import lombok.Data;

import java.util.List;

@Data
public abstract class AbstractRequestItem {
    private List<String> itemBarcodes;
    private String itemOwningInstitution = ""; // PUL, CUL, NYPL
}
