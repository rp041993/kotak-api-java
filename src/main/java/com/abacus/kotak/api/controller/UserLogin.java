/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.controller;

import com.abacus.kotak.api.bean.GenrateOtpDto;
import com.abacus.kotak.api.bean.ValidateOtpDto;
import static com.abacus.kotak.api.connection.MongoConnection.closeConnection;
import static com.abacus.kotak.api.connection.MongoConnection.createConnection;
import com.abacus.kotak.api.service.UserLoginModal;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoDatabase;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author abacus1
 */
@Path("login")
public class UserLogin {

    public static MongoDatabase database = null;

    @Path("otp-genration")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response genrateOtp(@QueryParam("email") String email) {
        JsonObject resObject = new JsonObject();
        database = createConnection();
        try {
            GenrateOtpDto genrateOtpDto = new GenrateOtpDto();
            genrateOtpDto.setEmail(email);

            UserLoginModal userLoginModal = new UserLoginModal();
            resObject=userLoginModal.genrateOtpIfUserExist(genrateOtpDto);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            closeConnection();
        }

        return Response
                .status(200)
                .entity(resObject.toString())
                .build();
    }

    @Path("otp-validation")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateOtp(@QueryParam("email") String email, @QueryParam("otp") String otp) {

        database = createConnection();
        JsonObject responseObject = null;
        try {
            ValidateOtpDto validateOtpDto = new ValidateOtpDto();
            validateOtpDto.setEmail(email);
            validateOtpDto.setOtp(otp);

            UserLoginModal userLoginModal = new UserLoginModal();
            responseObject = userLoginModal.validateUserOtp(validateOtpDto);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            closeConnection();
        }

        return Response
                .status(200)
                .entity(responseObject.toString())
                .build();
    }
}
