package com.csi.meghnacooler.Sqlite;

/**
 * Created by Jahid on 5/2/19.
 */
public class Datagetset {
    String productName;
    String quantity;
    String productId;
    public Datagetset(String productName, String quantity, String productId) {
        this.productName = productName;
        this.quantity = quantity;
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


}
