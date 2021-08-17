package com.csi.meghnacooler.HeadOffice;

/**
 * Created by Jahid on 27/2/19.
 */
public class Stock {
    String name;
    String quantity;
    String category;

    public Stock(String name, String quantity, String category) {
        this.name = name;
        this.quantity = quantity;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
