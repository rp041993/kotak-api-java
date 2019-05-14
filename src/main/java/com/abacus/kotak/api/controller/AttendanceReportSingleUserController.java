/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.controller;

import com.abacus.kotak.api.bean.AttendanceReportSingleUserControllerDto;
import static com.abacus.kotak.api.connection.MongoConnection.*;
import com.abacus.kotak.api.service.AttendanceReportSingleUserControllerModal;
import com.abacus.kotak.api.utils.Convert;
import com.mongodb.client.MongoDatabase;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
@Path("/attendance-report")
public class AttendanceReportSingleUserController {

    public static MongoDatabase database = null;

    @Path("single-user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttendanceReportSingleUser(@QueryParam("userId") String userId,
            @QueryParam("fromDate") Long fromDate, @QueryParam("toDate") Long toDate) {

        JSONArray returnList = new JSONArray();
        database = createConnection();
        try {
            AttendanceReportSingleUserControllerDto reportDto = new AttendanceReportSingleUserControllerDto();
            reportDto.setFromDate(fromDate);
            reportDto.setToDate(toDate);
            reportDto.setUserId(userId);

            AttendanceReportSingleUserControllerModal modalObject = new AttendanceReportSingleUserControllerModal();
            returnList = modalObject.getReportSingleUser(reportDto);

        } catch (Exception e) {
            System.out.println(e);
        }
        finally{
            closeConnection();
        }
//       return Response.status(200).entity(returnList).build();
        return Response
                .status(200)
                .entity(returnList.toString())
                .build();
    }

    @Path("intervention")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttendanceReportInterventionUser(InputStream incomingData) {

        JSONArray returnList = new JSONArray();
        database = createConnection();
        try {
            Convert convertObj = new Convert();
            JSONObject incomingDataJSONObject = convertObj.covertInputStreamToJsonObject(incomingData);

            AttendanceReportSingleUserControllerDto reportDto = new AttendanceReportSingleUserControllerDto();
            reportDto.setIncomingObject(incomingDataJSONObject);
            reportDto.setFromDate((Long) incomingDataJSONObject.get("fromDate"));
            reportDto.setToDate((Long) incomingDataJSONObject.get("toDate"));
            AttendanceReportSingleUserControllerModal modalObject = new AttendanceReportSingleUserControllerModal();
            returnList = modalObject.getReportInterventionUser(reportDto);

        } catch (Exception e) {
            System.out.println(e);
        }
        finally{
            closeConnection();
        }
//       return Response.status(200).entity(returnList).build();
        return Response
                .status(200)
                .entity(returnList.toString())
                .build();
    }

    @Path("full-list")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttendanceReportAllUser(
            @QueryParam("fromDate") Long fromDate, @QueryParam("toDate") Long toDate) {

        JSONArray returnList = new JSONArray();
        database = createConnection();
        try {
            AttendanceReportSingleUserControllerDto reportDto = new AttendanceReportSingleUserControllerDto();
            reportDto.setFromDate(fromDate);
            reportDto.setToDate(toDate);

            AttendanceReportSingleUserControllerModal modalObject = new AttendanceReportSingleUserControllerModal();
            returnList = modalObject.getReportAllUser(reportDto);

        } catch (Exception e) {
            System.out.println(e);
        }
        finally{
            closeConnection();
        }
//       return Response.status(200).entity(returnList).build();
        return Response
                .status(200)
                .entity(returnList.toString())
                .build();
    }
}
