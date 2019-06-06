package org.recap.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatronInformationResponse extends AbstractResponseItem {
    private String patronIdentifier;
    private String patronName;
    private String email;
    private String birthDate;
    private String phone;
    private String permanentLocation;
    private String pickupLocation;
    private int chargedItemsCount;
    private int chargedItemsLimit;
    private String feeLimit;
    private String feeType;
    private int holdItemsCount;
    private int holdItemsLimit;
    private int unavailableHoldsCount;
    private int fineItemsCount;
    private String feeAmount;
    private String homeAddress;
    private List<String> items;
    private String itemType;
    private int overdueItemsCount;
    private int overdueItemsLimit;
    private String pacAccessType;
    private String patronGroup;
    private String patronType;
    private String dueDate;
    private String expirationDate;
    private String status;
}
