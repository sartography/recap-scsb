package org.recap.model;

import java.util.Date;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ScsbRequest {
    private String reportType;
    private String transmissionType;
    private Date dateFrom;
    private Date dateTo;
}
