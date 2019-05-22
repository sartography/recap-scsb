package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ItemCreateBibResponse extends AbstractResponseItem {
    private String bibId;
    private String itemId;
}
