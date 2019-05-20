/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.utils;

import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 *
 * @author abacus1
 */
public class SendNotificationMobile {

    public void sendNotificationSenior(String seniorToken, String userName) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("to", seniorToken);
            json.addProperty("collapse_key", "type_a");

            JsonObject notificationBody = new JsonObject();
            notificationBody.addProperty("body", "New approval request by " + userName);
            notificationBody.addProperty("title", "New approval request by " + userName);

            json.add("data", notificationBody);

            HttpResponse<String> response = Unirest.post("https://fcm.googleapis.com/fcm/send")
                    .header("Authorization", "key= AIzaSyDPCiE4_KysqE2necy9dokclU9ocINEsSw")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body(json.toString())
                    .asString();
        } catch (UnirestException ex) {
            System.out.println(ex);
        }
    }

    public void sendNotificationUser(String userToken, String requestType, String ApprovalStatus) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("to", userToken);
            json.addProperty("collapse_key", "type_a");

            JsonObject notificationBody = new JsonObject();
            notificationBody.addProperty("body", "Attendance Request Status");
            notificationBody.addProperty("title", "Your" + " " + requestType + " request has been " + ApprovalStatus);

            json.add("data", notificationBody);

            HttpResponse<String> response = Unirest.post("https://fcm.googleapis.com/fcm/send")
                    .header("Authorization", "key= AIzaSyDPCiE4_KysqE2necy9dokclU9ocINEsSw")
                    .header("Content-Type", "application/json")
                    .header("cache-control", "no-cache")
                    .body(json.toString())
                    .asString();
        } catch (UnirestException ex) {
            System.out.println(ex);
        }
    }
}
