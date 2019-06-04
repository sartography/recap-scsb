package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemRefileResponse extends AbstractResponseItem {
    private String screenMessage;
    private boolean success;
}
