/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.controller;

import com.abacus.kotak.api.bean.AttendanceRequestDto;
import static com.abacus.kotak.api.connection.MongoConnection.closeConnection;
import static com.abacus.kotak.api.connection.MongoConnection.createConnection;
import com.abacus.kotak.api.service.AttendanceRequestModal;
import com.abacus.kotak.api.utils.Convert;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoDatabase;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
@Path("attendance-request")
public class AttendanceRequest {

    public static MongoDatabase database = null;

    @Path("normal")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response normalPunch(InputStream incomingData) {

        JsonObject returnObject = new JsonObject();
        database = createConnection();
        try {
            Convert convertObj = new Convert();
            JSONObject incomingDataJSONObject = convertObj.covertInputStreamToJsonObject(incomingData);

            // set value into pojo class
            AttendanceRequestDto attendanceRequestDto = new AttendanceRequestDto();
            attendanceRequestDto.setIncomingObject(incomingDataJSONObject);

            AttendanceRequestModal modalObj = new AttendanceRequestModal();
            returnObject = modalObj.pushNormalPunch(attendanceRequestDto);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            closeConnection();
        }

        return Response
                .status(200)
                .entity(returnObject.toString())
                .build();
    }

    @Path("remote")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response remotePunch(InputStream incomingData) {

        JsonObject returnObject = new JsonObject();
        database = createConnection();
        try {
            Convert convertObj = new Convert();
            JSONObject incomingDataJSONObject = convertObj.covertInputStreamToJsonObject(incomingData);

            // set value into pojo class
            AttendanceRequestDto attendanceRequestDto = new AttendanceRequestDto();
            attendanceRequestDto.setIncomingObject(incomingDataJSONObject);

            AttendanceRequestModal modalObj = new AttendanceRequestModal();
            returnObject = modalObj.pushRemotePunch(attendanceRequestDto);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            closeConnection();
        }

        return Response
                .status(200)
                .entity(returnObject.toString())
                .build();
    }

    @Path("regularization")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response regularizationPunch(InputStream incomingData) {

        JsonObject returnObject = new JsonObject();
        database = createConnection();
        try {
            Convert convertObj = new Convert();
            JSONObject incomingDataJSONObject = convertObj.covertInputStreamToJsonObject(incomingData);

            // set value into pojo class
            AttendanceRequestDto attendanceRequestDto = new AttendanceRequestDto();
            attendanceRequestDto.setIncomingObject(incomingDataJSONObject);

            AttendanceRequestModal modalObj = new AttendanceRequestModal();
            returnObject = modalObj.pushRegularizationPunch(attendanceRequestDto);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            closeConnection();
        }

        return Response
                .status(200)
                .entity(returnObject.toString())
                .build();
    }

    @Path("short-break")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response shortBreakPunch(InputStream incomingData) {

        JsonObject returnObject = new JsonObject();
        database = createConnection();
        try {
            Convert convertObj = new Convert();
            JSONObject incomingDataJSONObject = convertObj.covertInputStreamToJsonObject(incomingData);

            // set value into pojo class
            AttendanceRequestDto attendanceRequestDto = new AttendanceRequestDto();
            attendanceRequestDto.setIncomingObject(incomingDataJSONObject);

            AttendanceRequestModal modalObj = new AttendanceRequestModal();
            returnObject = modalObj.pushShortBreakPunch(attendanceRequestDto);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            closeConnection();
        }

        return Response
                .status(200)
                .entity(returnObject.toString())
                .build();
    }

}
