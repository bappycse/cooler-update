package com.csi.meghnacooler.Technician;

import org.json.JSONArray;

/**
 * Created by Jahid on 20/2/19.
 */
public class Complain {
    String id;
    String ticketNo;
    String date;
    String status;
    String region_name;
    String priority;
    String contactName;
    String contactNumber;
    String contactEmail;
    String note;
    String shopId;
    String shopName;
    String shopContactPerson;
    String shopPhoneNumber;
    String shopAddress;
    String complainType;
    String coolerSerial;
    JSONArray problem;
    String feedBack;
    String technicianId;
    String technicianName;
    String workshopName;
    String workshopAddress;
    String workshopMobile;
    String workshopAssignDate;
    String workshopDeliveryDate;
    String workshopInfo;
    JSONArray solved;

    public Complain(String id, String ticketNo, String date, String status, String region_name, String priority, String contactName, String contactNumber, String contactEmail,
                    String note, String shopId, String shopName, String shopContactPerson, String shopPhoneNumber, String shopAddress,String complainType,String coolerSerial,
                    JSONArray problem,String feedBack,String technicianId, String technicianName, String workshopName,String workshopAddress,String workshopMobile,String workshopAssignDate,
                    String workshopDeliveryDate,String workshopInfo,JSONArray solved) {
        this.id = id;
        this.ticketNo = ticketNo;
        this.date = date;
        this.status = status;
        this.region_name = region_name;
        this.priority = priority;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
        this.note = note;
        this.shopId = shopId;
        this.shopName = shopName;
        this.shopContactPerson = shopContactPerson;
        this.shopPhoneNumber = shopPhoneNumber;
        this.shopAddress = shopAddress;
        this.complainType = complainType;
        this.coolerSerial = coolerSerial;
        this.problem = problem;
        this.feedBack = feedBack;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.workshopName = workshopName;
        this.workshopAddress = workshopAddress;
        this.workshopMobile = workshopMobile;
        this.workshopAssignDate = workshopAssignDate;
        this.workshopDeliveryDate = workshopDeliveryDate;
        this.workshopInfo = workshopInfo;
        this.solved = solved;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShopId() {
        return shopId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
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

    public void setShopContactPerson(String shopContactPerson) { this.shopContactPerson = shopContactPerson; }

    public String getShopPhoneNumber() {
        return shopPhoneNumber;
    }

    public void setShopPhoneNumber(String shopPhoneNumber) { this.shopPhoneNumber = shopPhoneNumber; }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getComplainType() {
        return complainType;
    }

    public void setComplainType(String complainType) {
        this.complainType = complainType;
    }

    public String getCoolerSerial() {
        return coolerSerial;
    }

    public void setCoolerSerial(String coolerSerial) {
        this.coolerSerial = coolerSerial;
    }

    public JSONArray getProblem() {
        return problem;
    }

    public void setProblem(JSONArray problem) {
        this.problem = problem;
    }

    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public String getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(String technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianName() { return technicianName; }

    public void setTechnicianName(String technicianName) { this.technicianName = technicianName; }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName;
    }

    public String getWorkshopAddress() {
        return workshopAddress;
    }

    public void setWorkshopAddress(String workshopAddress) { this.workshopAddress = workshopAddress; }

    public String getWorkshopMobile() {
        return workshopMobile;
    }

    public void setWorkshopMobile(String workshopMobile) {
        this.workshopMobile = workshopMobile;
    }

    public String getWorkshopAssignDate() {
        return workshopAssignDate;
    }

    public void setWorkshopAssignDate(String workshopAssignDate) { this.workshopAssignDate = workshopAssignDate;}

    public String getWorkshopDeliveryDate() {
        return workshopDeliveryDate;
    }

    public void setWorkshopDeliveryDate(String workshopDeliveryDate) { this.workshopDeliveryDate = workshopDeliveryDate; }

    public String getWorkshopInfo() {
        return workshopInfo;
    }

    public void setWorkshopInfo(String workshopInfo) {
        this.workshopInfo = workshopInfo;
    }

    public JSONArray getSolved() {
        return solved;
    }

    public void setSolved(JSONArray solved) {
        this.solved = solved;
    }

}
