package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemCreateBibRequest extends AbstractRequestItem {
    private String patronIdentifier;
    private String titleIdentifier;
}