package com.abacus.kotak.api.bean;

import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author abacus1
 */
public class AttendanceRequestApprovalDto {

    private JSONObject incomingObject;

    public JSONObject getIncomingObject() {
        return incomingObject;
    }

    public void setIncomingObject(JSONObject incomingObject) {
        this.incomingObject = incomingObject;
    }
}
