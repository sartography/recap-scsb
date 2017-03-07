package org.recap.model;

/**
 * Created by sudhishk on 16/12/16.
 */
public class ItemCreateBibResponse extends AbstractResponseItem {

    private String bibId;
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBibId() {
        return bibId;
    }

    public void setBibId(String bibId) {
        this.bibId = bibId;
    }


}
