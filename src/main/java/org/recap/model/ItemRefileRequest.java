package org.recap.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ItemRefileRequest {
    @Singular
    private List<String> itemBarcodes;
    @Singular
    private List<Integer> requestIds;
}
