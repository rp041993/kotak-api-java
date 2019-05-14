/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.dao;

import com.abacus.kotak.api.bean.AttendanceReportSingleUserControllerDto;
import com.abacus.kotak.api.controller.AttendanceReportSingleUserController;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import static com.mongodb.client.model.Sorts.ascending;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public class AttendanceReportSingleUserControllerDao {

    public Iterator getSingleUserPunches(AttendanceReportSingleUserControllerDto reportDto) {

        Iterator it = null;
        MongoDatabase database = AttendanceReportSingleUserController.database;
        try {
            MongoCollection collection = database.getCollection("punchinout_timestamp");
            Document doc = new Document("user_id", reportDto.getUserId())
                    .append("dateMillis", new Document("$gte", reportDto.getFromDate()).append("$lte", reportDto.getToDate()));
            Document field = new Document("_id", "1");

            Bson sort = ascending("timestamp");
            FindIterable<Document> iterDoc = collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            ).sort(sort);

            it = iterDoc.iterator();
        } catch (Exception e) {
            System.out.println(e);
        } 
        return it;
    }
    
     public Iterator getAllUserPunches(AttendanceReportSingleUserControllerDto reportDto) {

        Iterator it = null;
        MongoDatabase database = AttendanceReportSingleUserController.database;
        try {
            MongoCollection collection = database.getCollection("punchinout_timestamp");
            Document doc = new Document("dateMillis", new Document("$gte", reportDto.getFromDate()).append("$lte", reportDto.getToDate()));
            Document field = new Document("_id", "1");

            FindIterable<Document> iterDoc = collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            );

            it = iterDoc.iterator();
        } catch (Exception e) {
            System.out.println(e);
        } 
        return it;
    }

    //  Get intervention vise report
    public Iterator getInterventionUserPunches(AttendanceReportSingleUserControllerDto reportDto) {

        Iterator it = null;
        ArrayList<String> userList = (ArrayList<String>) reportDto.getIncomingObject().get("users");
        MongoDatabase database = AttendanceReportSingleUserController.database;
        try {
            MongoCollection collection = database.getCollection("punchinout_timestamp");
            Document doc = new Document("user_id", new Document("$in", userList))
                    .append("dateMillis", new Document("$gte", reportDto.getFromDate()).append("$lte", reportDto.getToDate()));
            Document field = new Document("_id", "1");

            Bson sort = ascending("timestamp");
            FindIterable<Document> iterDoc = collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            ).sort(sort);

            it = iterDoc.iterator();
        } catch (Exception e) {
            System.out.println(e);
        } 
        return it;
    }

    //    getUserDetails
    public Document getUserDetails(String userId) {

        Document iterDoc = null;
        MongoDatabase database = AttendanceReportSingleUserController.database;
        try {
            MongoCollection collection = database.getCollection("users");
            Document doc = new Document("_id", userId);
            Document field = new Document("_id", "1");

            iterDoc = (Document) collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            ).first();

        } catch (Exception e) {
            System.out.println(e);
        } 
        return iterDoc;
    }

    // Find user shift details
    public Document getUserShiftDetails(String userId) {

        Document iterDoc = null;
        MongoDatabase database = AttendanceReportSingleUserController.database;
        try {
            MongoCollection collection = database.getCollection("user_shift_mapping");
            Document doc = new Document("_id", userId);
            Document field = new Document("_id", "1");

            iterDoc = (Document) collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            ).first();

        } catch (Exception e) {
            System.out.println(e);
        } 
        return iterDoc;
    }

    // Get shifts
    public HashMap<String, JSONObject> getShifts() {
        HashMap<String, JSONObject> responseShiftMap = new HashMap<>();
        MongoDatabase database = AttendanceReportSingleUserController.database;
        try {
            MongoCollection collection = database.getCollection("shifts");
            Document doc = new Document();
            FindIterable<Document> iterDoc = collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            );

            Iterator it = iterDoc.iterator();
            while (it.hasNext()) {
                Document docObject = (Document) it.next();
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("shiftInTime", docObject.get("from_time_in_hours_format"));
                jsonObj.put("shiftOutTime", docObject.get("to_time_in_hours_format"));
                responseShiftMap.put(docObject.getString("name"), jsonObj);
            }
        } catch (Exception e) {
            System.out.println(e);
        } 
        return responseShiftMap;
    }

    // Get HolidayList
    public ArrayList<Long> getHolidays() {
        ArrayList<Long> responseList = new ArrayList<>();
        MongoDatabase database = AttendanceReportSingleUserController.database;
        try {
            MongoCollection collection = database.getCollection("holidays");
            Document doc = new Document();
            FindIterable<Document> iterDoc = collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            );

            Iterator it = iterDoc.iterator();
            while (it.hasNext()) {
                Document docObject = (Document) it.next();
                responseList.add(docObject.getLong("dateMillis"));
            }
        } catch (Exception e) {
            System.out.println(e);
        } 
        return responseList;
    }
}
