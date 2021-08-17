package com.csi.meghnacooler.Technician;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by Jahid on 25/2/19.
 */
public class Requisition {
    String productName;
    String orderQuantity;
    String productId;
    String status;
    JSONArray jsonArray;
    /// new
    List<String> requisitionName;
    String requestQuantity;
    String approvedQuantity;
    public Requisition(List<String> requisitionName, String productName, String orderQuantity, String productId, String status,
                       JSONArray jsonArrayProductLsts, String requestQuantity, String approvedQuantity) {
        this.requisitionName = requisitionName;
        this.productName = productName;
        this.orderQuantity = orderQuantity;
        this.productId = productId;
        this.status = status;
        this.jsonArray = jsonArrayProductLsts;
        this.requestQuantity = requestQuantity;
        this.approvedQuantity = approvedQuantity;
    }


    public List<String> getRequisitionName() {
        return requisitionName;
    }

    public void setRequisitionName(List<String> requisitionName) {
        this.requisitionName = requisitionName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        this.orderQuantity = orderQuantity;
    }
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public String getRequestQuantity() {
        return requestQuantity;
    }

    public void setRequestQuantity(String requestQuantity) {
        this.requestQuantity = requestQuantity;
    }

    public String getApprovedQuantity() {
        return approvedQuantity;
    }

    public void setApprovedQuantity(String approvedQuantity) {
        this.approvedQuantity = approvedQuantity;
    }
}
