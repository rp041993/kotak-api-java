/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.utils;

import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 *
 * @author abacus1
 */
public interface GetUserDetails {

    String checkEmployeeEmailExists(String email);

    Document getUserDetails(String uid, MongoDatabase database);
    
}
