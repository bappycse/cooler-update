package com.csi.meghnacooler.Technician;

/**
 * Created by Jahid on 12/3/19.
 */
public class Product {
    String id;
    String productName;
    String quantity;
    String approved_quantity;

    public Product(String id, String productName, String quantity, String approved_quantity) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.approved_quantity = approved_quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getApproved_quantity() {
        return approved_quantity;
    }

    public void setApproved_quantity(String approved_quantity) {
        this.approved_quantity = approved_quantity;
    }
}
