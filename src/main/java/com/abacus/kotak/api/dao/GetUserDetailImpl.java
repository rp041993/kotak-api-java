/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.dao;

import com.abacus.kotak.api.bean.ValidateOtpDto;
import com.abacus.kotak.api.controller.UserLogin;
import com.abacus.kotak.api.utils.GetUserDetails;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Sorts.ascending;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author abacus1
 */
public class GetUserDetailImpl implements GetUserDetails {

    @Override
    public String checkEmployeeEmailExists(String email) {
        String userId = "";
        MongoDatabase database = UserLogin.database;
        try {
            MongoCollection collection = database.getCollection("users");
            Document doc = new Document("personal_email_id", email.toLowerCase())
                    .append("current_status", new Document("$ne", "Resigned"));

            Bson sort = ascending("timestamp");
            Document iterDoc = (Document) collection.find(doc).first();
            if (iterDoc != null) {
                userId = iterDoc.getString("_id");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return userId;
    }

    @Override
    public Document getUserDetails(String uid) {
        
    }
    
    public String checkLastOtpExistTime(String employeeId, long date) {
        String otp = "";
        MongoDatabase database = UserLogin.database;
        try {
            MongoCollection collection = database.getCollection("otp_data");
            Document doc = new Document("_id", employeeId)
                    .append("time", date);

            Document iterDoc = (Document) collection.find(doc).first();
            if (iterDoc != null) {
                otp = iterDoc.getString("otp");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return otp;
    }

    public void updateOtpToUser(String id, String otp, long time, String email) {
        MongoDatabase database = UserLogin.database;
        try {
            MongoCollection collection = database.getCollection("otp_data");
            Document doc = new Document("_id", id);
            Document updateField = new Document();
            updateField.append("_id", id);
            updateField.append("otp", otp);
            updateField.append("time", time);
            updateField.append("email", email);
            Document setQuery = new Document("$set", updateField);

            collection.updateOne(doc, setQuery, new UpdateOptions().upsert(true));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String verifyOtp(ValidateOtpDto validateOtpDto) {
        String uid = "";
        MongoDatabase database = UserLogin.database;
        try {
            MongoCollection collection = database.getCollection("otp_data");
            Document doc = new Document("email", validateOtpDto.getEmail().toLowerCase())
                    .append("otp", validateOtpDto.getOtp());

            Document iterDoc = (Document) collection.find(doc).first();
            if (iterDoc != null) {
                uid = iterDoc.getString("_id");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return uid;
    }
}
