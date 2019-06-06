package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemCheckOutRequest extends AbstractRequestItem {
    private String patronIdentifier;
}
