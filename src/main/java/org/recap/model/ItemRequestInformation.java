package org.recap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Data
@Builder
public class ItemRequestInformation {
    @Singular
    private List<String> itemBarcodes;
    private String titleIdentifier;
    private String itemOwningInstitution; // PUL, CUL, NYPL
    private String patronBarcode;
    private String emailAddress;
    private String requestingInstitution; // PUL, CUL, NYPL
    private String requestType; // Retrieval,EDD, Hold, Recall, Borrow Direct
    private String deliveryLocation;
    private String requestNotes;
    private String trackingId; // NYPL - trackingId
    private String author; // NYPL - author
    private String callNumber; // NYPL - callNumber

    /**
     * EDD Request
     */
    private String startPage;
    private String endPage;
    private String chapterTitle;
    private String bibId;
    private String username;
    private String issue;
    private String volume;

    /**
     * Is owning institution item boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    public boolean isOwningInstitutionItem() {
        boolean bSuccess;
        bSuccess = itemOwningInstitution.equalsIgnoreCase(requestingInstitution);
        return bSuccess;
    }
}