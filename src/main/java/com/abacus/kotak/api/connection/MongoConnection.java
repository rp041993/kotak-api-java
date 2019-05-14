package com.abacus.kotak.api.connection;

import com.abacus.kotak.api.credentials.Credentails;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;

/**
 *
 * @author abacus1
 */
public class MongoConnection {

//    private static Mongo mongoClient = null;
//  create connection
    static MongoClient mongoClient = null;

    public static MongoDatabase createConnection() {
        MongoDatabase database = null;
        try {
            Credentails credentails = new Credentails();
            MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(credentails.getUserName(), credentails.getDBName(), credentails.getPassword().toCharArray());
            mongoClient = new MongoClient(new ServerAddress(credentails.getHost(), 27017), Arrays.asList(mongoCredential));
            database = mongoClient.getDatabase(credentails.getDBName());
        } catch (Exception e) {
            System.out.println("MongoClient=" + e);
        }
        return database;
    }

    public static void closeConnection() {
        try {
            mongoClient.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

//   close connection 
//    public static String closeConnection() {
//        try {
//            mongoClient.close();
//        } catch (Exception e) {
//        }
//        return "success";
//    }
}
