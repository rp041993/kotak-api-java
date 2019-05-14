/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.bean;

import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public class AttendanceReportSingleUserControllerDto {

    private String userId = "";
    private Long fromDate = 0L;
    private Long toDate = 0L;
    private JSONObject incomingObject;

    public JSONObject getIncomingObject() {
        return incomingObject;
    }

    public void setIncomingObject(JSONObject incomingObject) {
        this.incomingObject = incomingObject;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

}
