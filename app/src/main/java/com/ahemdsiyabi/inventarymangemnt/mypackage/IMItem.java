package com.ahemdsiyabi.inventarymangemnt.mypackage;

import java.io.Serializable;

public class IMItem implements Serializable {
    private String itemId;
    private String itemName;
    private String itemPrice;
    private String itemQTY;
    private String itemImg;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemQTY() {
        return itemQTY;
    }

    public void setItemQTY(String itemQTY) {
        this.itemQTY = itemQTY;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }
}
