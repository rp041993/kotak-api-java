/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.utils;

import com.mongodb.client.MongoDatabase;
import java.util.HashMap;
import org.bson.Document;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public interface GetShiftDetails {

    Document getUserShiftDetails(String userId, MongoDatabase database);

    HashMap<String, JSONObject> getShifts(MongoDatabase database);
}
