package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemCreateBibResponse extends AbstractResponseItem {
    private String bibId;
    private String itemId;
}
