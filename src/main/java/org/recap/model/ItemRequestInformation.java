package org.recap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Data
@EqualsAndHashCode
@SuperBuilder
public class ItemRequestInformation {

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
        if (itemOwningInstitution.equalsIgnoreCase(requestingInstitution)) {
            bSuccess = true;
        } else {
            bSuccess = false;
        }
        return bSuccess;
    }

    @Override
    public String toString() {
        return "ItemRequestInformation{" +
                "itemBarcodes=" + itemBarcodes +
                ", titleIdentifier='" + titleIdentifier + '\'' +
                ", itemOwningInstitution='" + itemOwningInstitution + '\'' +
                ", patronBarcode='" + patronBarcode + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", requestingInstitution='" + requestingInstitution + '\'' +
                ", requestType='" + requestType + '\'' +
                ", deliveryLocation='" + deliveryLocation + '\'' +
                ", requestNotes='" + requestNotes + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", author='" + author + '\'' +
                ", callNumber='" + callNumber + '\'' +
                ", startPage='" + startPage + '\'' +
                ", endPage='" + endPage + '\'' +
                ", chapterTitle='" + chapterTitle + '\'' +
                ", bibId='" + bibId + '\'' +
                ", username='" + username + '\'' +
                ", issue='" + issue + '\'' +
                ", volume='" + volume + '\'' +
                '}';
    }
}