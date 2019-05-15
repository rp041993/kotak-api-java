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
public class AttendanceRequestDto {

    private JSONObject incomingObject;

    public JSONObject getIncomingObject() {
        return incomingObject;
    }

    public void setIncomingObject(JSONObject incomingObject) {
        this.incomingObject = incomingObject;
    }
}
