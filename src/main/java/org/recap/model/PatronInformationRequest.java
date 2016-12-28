package org.recap.model;

import java.util.List;

/**
 * Created by sudhishk on 26/12/16.
 */
public class PatronInformationRequest{

    String patronIdentifier = "";
    private String itemOwningInstitution=""; // PUL, CUL, NYPL

    public String getPatronIdentifier() {
        return patronIdentifier;
    }

    public void setPatronIdentifier(String patronIdentifier) {
        this.patronIdentifier = patronIdentifier;
    }

    public String getItemOwningInstitution() {
        return itemOwningInstitution;
    }

    public void setItemOwningInstitution(String itemOwningInstitution) {
        this.itemOwningInstitution = itemOwningInstitution;
    }
}
