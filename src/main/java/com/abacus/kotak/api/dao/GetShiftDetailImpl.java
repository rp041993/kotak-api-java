/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.dao;

import com.abacus.kotak.api.controller.AttendanceReportSingleUserController;
import com.abacus.kotak.api.utils.GetShiftDetails;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import java.util.HashMap;
import java.util.Iterator;
import org.bson.Document;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public class GetShiftDetailImpl implements GetShiftDetails {

    @Override
    public Document getUserShiftDetails(String userId, MongoDatabase database) {

        Document iterDoc = null;
        try {
            MongoCollection collection = database.getCollection("user_shift_mapping");
            Document doc = new Document("key", userId);
            Document field = new Document("_id", "1");

            iterDoc = (Document) collection.find(doc).projection(Projections.fields(
                    Projections.excludeId())
            ).first();

        } catch (Exception e) {
            System.out.println(e);
        }
        return iterDoc;
    }

    @Override
    public HashMap<String, JSONObject> getShifts(MongoDatabase database) {
        HashMap<String, JSONObject> responseShiftMap = new HashMap<>();
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

}
