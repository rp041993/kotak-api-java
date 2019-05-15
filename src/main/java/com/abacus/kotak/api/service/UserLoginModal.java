/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.service;

import com.abacus.kotak.api.bean.GenrateOtpDto;
import com.abacus.kotak.api.bean.ValidateOtpDto;
import com.abacus.kotak.api.dao.GetUserDetailImpl;
import com.abacus.kotak.api.utils.SendEmailToUser;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author abacus1
 */
public class UserLoginModal {

    public JsonObject genrateOtpIfUserExist(GenrateOtpDto genrateOtpDto) {
        JsonObject json = new JsonObject();
        GetUserDetailImpl getUserDetailImpl = new GetUserDetailImpl();
        String employeeId = getUserDetailImpl.checkEmployeeEmailExists(genrateOtpDto.getEmail());
        long time = currentDateMillis();
        String responseOtp = "";
        if (!employeeId.equals("")) {
            String otp = getUserDetailImpl.checkLastOtpExistTime(employeeId, time);
            if (!otp.equals("")) {
                // Send email to user with otp
                responseOtp = otp;
            } else {
                // Genrate Otp
                String randomOtp = genrateotp();
                // update otp and time in otp_data
                getUserDetailImpl.updateOtpToUser(employeeId, randomOtp, time,genrateOtpDto.getEmail().toLowerCase());
                responseOtp = randomOtp;
            }
            String body = "Your new otp for login:\n" + "------------------------\n" + "\nOTP:" + responseOtp;
//            SendEmailToUser email = new SendEmailToUser();
            SendEmailToUser.sendMessage(genrateOtpDto.getEmail().toLowerCase(), "New OTP for Login", body);
            json.addProperty("status", "OK");
        } else {
            json.addProperty("status", "ERROR");
            json.addProperty("message", "Either Your account is not active or you are not registered user!");
        }
        return json;
    }

    public JsonObject validateUserOtp(ValidateOtpDto validateOtpDto) {
        JsonObject obj = new JsonObject();
        GetUserDetailImpl getUserDetailImpl = new GetUserDetailImpl();
        String uid=getUserDetailImpl.verifyOtp(validateOtpDto);
        if(!uid.equals("")){
            obj.addProperty("status", "Ok");
            obj.addProperty("onboard_id",uid );
            obj.addProperty("token","" );
        }
        else{
            obj.addProperty("status", "Error");
            obj.addProperty("message", "Invalid Otp");
        }
        return obj;
    }

    public Long currentDateMillis() {
        long millis = 0;
        try {
            Calendar fullDate = Calendar.getInstance();
            fullDate.setTimeInMillis(new Date().getTime());
            String d = String.valueOf(fullDate.get(Calendar.YEAR)) + "/" + String.valueOf(fullDate.get(Calendar.MONTH) + 1) + "/" + String.valueOf(fullDate.get(Calendar.DATE));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = sdf.parse(d);
            millis = date.getTime();
        } catch (ParseException ex) {
            System.out.println(ex);
        }

        return millis;
    }

    public String genrateotp() {
        String otp = "";
        Random rnd = new Random();
        otp = String.valueOf(100000 + rnd.nextInt(900000));
        return otp;
    }

}
