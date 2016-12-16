package org.recap.model;

/**
 * Created by sudhishk on 15/12/16.
 */
public class ItemCreateBibRequest extends AbstractRequestItem{

    private String patronIdentifier;
    private String titleIdentifier;

    public String getPatronIdentifier() {
        return patronIdentifier;
    }

    public void setPatronIdentifier(String patronIdentifier) {
        this.patronIdentifier = patronIdentifier;
    }

    public String getTitleIdentifier() {
        return titleIdentifier;
    }

    public void setTitleIdentifier(String titleIdentifier) {
        this.titleIdentifier = titleIdentifier;
    }
}