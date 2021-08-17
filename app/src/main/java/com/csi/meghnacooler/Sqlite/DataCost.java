package com.csi.meghnacooler.Sqlite;

/**
 * Created by Jahid on 14/3/19.
 */
public class DataCost {
    String productName;
    String productId;
    String quantity;
    String amount;
    String unitPrice;
    String title;
    String note;

    public DataCost(String productName, String productId, String quantity, String amount, String unitPrice, String title, String note) {
        this.productName = productName;
        this.productId = productId;
        this.quantity = quantity;
        this.amount = amount;
        this.unitPrice = unitPrice;
        this.title = title;
        this.note = note;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
