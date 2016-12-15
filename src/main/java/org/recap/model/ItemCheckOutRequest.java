package org.recap.model;

/**
 * Created by sudhishk on 15/12/16.
 */
public class ItemCheckOutRequest extends AbstractRequestItem {

    private String patronIdentifier;

    public String getPatronIdentifier() {
        return patronIdentifier;
    }

    public void setPatronIdentifier(String patronIdentifier) {
        this.patronIdentifier = patronIdentifier;
    }



    
}
