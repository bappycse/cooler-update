package com.csi.meghnacooler.HeadOffice;

/**
 * Created by Jahid on 26/2/19.
 */
public class Ticket {
    String ticketNo;
    String status;
    String priority;
    String shopName;
    String shopContactPerson;
    String shopMobileNo;
    String shopAddress;
    String date;
    String statusFilter;
    public Ticket(String ticketNo, String status, String priority, String shopName, String shopContactPerson, String shopMobileNo, String shopAddress, String date, String statusFilter) {
        this.ticketNo = ticketNo;
        this.status = status;
        this.priority = priority;
        this.shopName = shopName;
        this.shopContactPerson = shopContactPerson;
        this.shopMobileNo = shopMobileNo;
        this.shopAddress = shopAddress;
        this.date = date;
        this.statusFilter = statusFilter;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopContactPerson() {
        return shopContactPerson;
    }

    public void setShopContactPerson(String shopContactPerson) {
        this.shopContactPerson = shopContactPerson;
    }

    public String getShopMobileNo() {
        return shopMobileNo;
    }

    public void setShopMobileNo(String shopMobileNo) {
        this.shopMobileNo = shopMobileNo;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatusFilter() {
        return statusFilter;
    }

    public void setStatusFilter(String statusFilter) {
        this.statusFilter = statusFilter;
    }

}
