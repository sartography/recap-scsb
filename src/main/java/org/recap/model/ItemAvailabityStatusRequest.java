package org.recap.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ItemAvailabityStatusRequest {
    @Singular
    private List<String> barcodes;
}
