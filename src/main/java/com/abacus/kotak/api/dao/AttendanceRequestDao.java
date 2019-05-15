/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.dao;

import com.abacus.kotak.api.bean.AttendanceRequestDto;
import com.abacus.kotak.api.controller.AttendanceRequest;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;
import java.util.Calendar;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public class AttendanceRequestDao {

    public ObjectId insertNormalPunch(Document user, long date, String seniorId, AttendanceRequestDto attendanceRequestDto) {
        ObjectId response = null;
        MongoDatabase database = AttendanceRequest.database;
        JSONObject obj = attendanceRequestDto.getIncomingObject();
        try {
            Calendar fullDate = Calendar.getInstance();
            fullDate.setTimeInMillis(new Date().getTime());

            MongoCollection collection = database.getCollection("punchinout_timestamp");

            Document doc = new Document("address", obj.get("address"))
                    .append("display_name", obj.get("display_name"))
                    .append("user_id", obj.get("user_id"))
                    .append("timestamp", obj.get("timestamp"))
                    .append("user_name", user.getString("full_name"))
                    .append("status", obj.get("status"))
                    .append("date", obj.get("date"))
                    .append("time", obj.get("time"))
                    .append("type", obj.get("type"))
                    .append("accesspoint_id", obj.get("accesspoint_id"))
                    .append("BSSID", obj.get("BSSID"))
                    .append("SSID", obj.get("SSID"))
                    .append("applied_on", new Date().getTime())
                    .append("senior_id", seniorId)
                    .append("year", String.valueOf(fullDate.get(Calendar.YEAR)))
                    .append("month", String.valueOf(fullDate.get(Calendar.MONTH) + 1))
                    .append("date", String.valueOf(fullDate.get(Calendar.DATE)))
                    .append("dateMillis", date);

            collection.insertOne(doc);
            response = (ObjectId) doc.get("_id");

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

    public ObjectId insertRemotePunch(Document user, long date, String seniorId, AttendanceRequestDto attendanceRequestDto, String address) {
        ObjectId response = null;
        MongoDatabase database = AttendanceRequest.database;
        JSONObject obj = attendanceRequestDto.getIncomingObject();
        try {
            Calendar fullDate = Calendar.getInstance();
            fullDate.setTimeInMillis(new Date().getTime());

            MongoCollection collection = database.getCollection("punchinout_timestamp");

            Document doc = new Document("address", address)
                    .append("approval_status", "PENDING")
                    .append("display_name", address)
                    .append("user_id", obj.get("user_id"))
                    .append("latitude", obj.get("latitude"))
                    .append("longitude", obj.get("longitude"))
                    .append("timestamp", obj.get("timestamp"))
                    .append("user_name", user.getString("full_name"))
                    .append("status", obj.get("status"))
                    .append("date", obj.get("date"))
                    .append("time", obj.get("time"))
                    .append("type", obj.get("type"))
                    .append("applied_on", new Date().getTime())
                    .append("senior_id", seniorId)
                    .append("year", String.valueOf(fullDate.get(Calendar.YEAR)))
                    .append("month", String.valueOf(fullDate.get(Calendar.MONTH) + 1))
                    .append("date", String.valueOf(fullDate.get(Calendar.DATE)))
                    .append("dateMillis", date);

            collection.insertOne(doc);
            response = (ObjectId) doc.get("_id");

        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

    public ObjectId insertRegularizationPunch(Document user, long date, String seniorId, AttendanceRequestDto attendanceRequestDto) {
        ObjectId response = null;
        MongoDatabase database = AttendanceRequest.database;
        JSONObject obj = attendanceRequestDto.getIncomingObject();
        try {
            Calendar fullDate = Calendar.getInstance();
            fullDate.setTimeInMillis(new Date().getTime());

            MongoCollection collection = database.getCollection("punchinout_timestamp");

            Document doc = new Document("address", obj.get("address"))
                    .append("approval_status", "PENDING")
                    .append("display_name", obj.get("display_name"))
                    .append("user_id", obj.get("user_id"))
                    .append("latitude", obj.get("latitude"))
                    .append("longitude", obj.get("longitude"))
                    .append("timestamp", obj.get("timestamp"))
                    .append("user_name", user.getString("full_name"))
                    .append("status", obj.get("status"))
                    .append("date", obj.get("date"))
                    .append("time", obj.get("time"))
                    .append("type", obj.get("type"))
                    .append("applied_on", new Date().getTime())
                    .append("senior_id", seniorId)
                    .append("year", String.valueOf(fullDate.get(Calendar.YEAR)))
                    .append("month", String.valueOf(fullDate.get(Calendar.MONTH) + 1))
                    .append("date", String.valueOf(fullDate.get(Calendar.DATE)))
                    .append("dateMillis", date);

            collection.insertOne(doc);
            response = (ObjectId) doc.get("_id");

            // Update regularization count
            long regularizationCount = 1;
            if (user.containsKey("regularizationCount")) {
                regularizationCount += user.getLong("regularizationCount");
            }

            MongoCollection userCollection = database.getCollection("users");
//            
//            Document updateField = new Document();
//            updateField.append("regularizationCount", regularizationCount);
//            Document setQuery = new Document("$set", updateField);
            userCollection.updateOne(eq("onboard_id", user.getString("onboard_id")),
                    new Document("$set", new Document("regularizationCount", regularizationCount)));
//            userCollection.updateOne(setQuery, userDoc, new UpdateOptions().upsert(false));
        } catch (Exception e) {
            System.out.println(e);
        }
        return response;
    }

}
