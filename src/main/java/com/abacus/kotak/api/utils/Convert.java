/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;

/**
 *
 * @author abacus1
 */
public class Convert {

    public org.json.simple.JSONObject covertInputStreamToJsonObject(InputStream inputStream) {
        StringBuilder Builder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = in.readLine()) != null) {
                Builder.append(line);
            }
        } catch (Exception e) {
            System.out.println("Error Parsing: - ");
        }
        JSONObject jsonObj = new JSONObject(Builder.toString());
        org.json.simple.JSONObject simpleJson = new org.json.simple.JSONObject(jsonObj.toMap());
        return simpleJson;
    }
}
