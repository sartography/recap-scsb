package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ItemCreateBibRequest extends AbstractRequestItem{
    private String patronIdentifier;
    private String titleIdentifier;
}