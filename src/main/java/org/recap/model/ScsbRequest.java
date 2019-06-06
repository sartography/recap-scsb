package org.recap.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ScsbRequest {
    private String reportType;
    private String transmissionType;
    private Date dateFrom;
    private Date dateTo;
}
