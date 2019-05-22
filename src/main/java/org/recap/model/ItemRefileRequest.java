package org.recap.model;

import java.util.List;

import lombok.Data;
import lombok.Singular;
import lombok.Builder;

@Data
@Builder
public class ItemRefileRequest  {
    @Singular private List<String> itemBarcodes;
    @Singular private List<Integer> requestIds;
}
