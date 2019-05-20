/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.dao;

import com.abacus.kotak.api.bean.AttendanceRequestApprovalDto;
import com.abacus.kotak.api.controller.AttendanceRequestApproval;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Calendar;
import java.util.Date;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public class AttendanceRequestApprovalDao {

    // Insert Normal Punch
    public boolean updatePunchInOutTimestamp(AttendanceRequestApprovalDto attendanceRequestApprovalDto, String approvedBy,
            String approvalStatus, String approvedAt) {
        boolean response = true;
        MongoDatabase database = AttendanceRequestApproval.database;
        JSONObject obj = attendanceRequestApprovalDto.getIncomingObject();
        try {
            Calendar fullDate = Calendar.getInstance();
            fullDate.setTimeInMillis(new Date().getTime());

            MongoCollection collection = database.getCollection("punchinout_timestamp");
            Document doc = new Document("key", obj.get("key").toString());
            Document updateField = new Document();
            updateField.append("approval_status", approvalStatus);
            updateField.append(approvedAt, new Date().getTime());
            updateField.append(approvedBy, obj.get("senior_id").toString());
            Document setQuery = new Document("$set", updateField);

            collection.updateOne(doc, setQuery);

        } catch (Exception e) {
            response = false;
            System.out.println(e);
        }
        return response;
    }

}
