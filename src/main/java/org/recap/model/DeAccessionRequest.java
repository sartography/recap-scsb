package org.recap.model;

import java.util.List;

import lombok.Data;
import lombok.Singular;
import lombok.Builder;

@Data
@Builder
public class DeAccessionRequest {
    @Singular private List<DeAccessionItem> deAccessionItems;
    private String username;
}
