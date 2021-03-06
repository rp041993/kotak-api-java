/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.service;

import com.abacus.kotak.api.bean.AttendanceRequestDto;
import static com.abacus.kotak.api.controller.AttendanceRequest.database;
import com.abacus.kotak.api.dao.AttendanceRequestDao;
import com.abacus.kotak.api.dao.GetUserDetailImpl;
import com.abacus.kotak.api.utils.SendNotificationMobile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author abacus1
 */
public class AttendanceRequestModal {

    // Normal Punch
    public JsonObject pushNormalPunch(AttendanceRequestDto attendanceRequestDto) {
        JsonObject json = new JsonObject();
        try {
            // get user Details
            GetUserDetailImpl getUserDetails = new GetUserDetailImpl();
            Document user = getUserDetails.getUserDetails(attendanceRequestDto.getIncomingObject().get("user_id").toString(), database);
            if (user != null) {
                String seniorLevel1 = getLastSeniorId(user.get("attendance_senior_level_I"));

                AttendanceRequestDao dao = new AttendanceRequestDao();
                ObjectId response = dao.insertNormalPunch(user, getCurrentDateMillis(new Date().getTime()), seniorLevel1, attendanceRequestDto);
                if (response != null) {
                    json.addProperty("status", "OK");
                    json.addProperty("message", "Punched successfully");
                    json.addProperty("key", response.toString());
                } else {
                    json.addProperty("status", "ERROR");
                    json.addProperty("message", "Something went wrong!");
                }
            } else {
                json.addProperty("status", "ERROR");
                json.addProperty("message", "User id doesn't match!");
            }
        } catch (Exception e) {
            json.addProperty("status", "ERROR");
            json.addProperty("message", "Something went wrong!");
        }
        return json;
    }

    // Remote Punch
    public JsonObject pushRemotePunch(AttendanceRequestDto attendanceRequestDto) {
        JsonObject json = new JsonObject();
        try {

            // get user Details
            GetUserDetailImpl getUserDetails = new GetUserDetailImpl();
            Document user = getUserDetails.getUserDetails(attendanceRequestDto.getIncomingObject().get("user_id").toString(), database);
            if (user != null) {

                long timestamp = Long.parseLong(attendanceRequestDto.getIncomingObject().get("timestamp").toString());
                // check address
                String displayName = attendanceRequestDto.getIncomingObject().get("display_name").toString();
                String address = "";
                if (displayName.equals("") || displayName.equals(" ") || displayName.equals(null)) {
                    //get Address
                    address = getAddressByLatLong(Float.parseFloat(attendanceRequestDto.getIncomingObject().get("latitude").toString()),
                            Float.parseFloat(attendanceRequestDto.getIncomingObject().get("longitude").toString()));
                } else {
                    address = displayName;
                }

                String seniorLevel1 = getLastSeniorId(user.get("attendance_senior_level_I"));
                // Get senior details
                Document seniorData = getUserDetails.getUserDetails(seniorLevel1, database);

                AttendanceRequestDao dao = new AttendanceRequestDao();
                ObjectId response = dao.insertRemotePunch(user, getCurrentDateMillis(timestamp), seniorLevel1, attendanceRequestDto, address);
                if (response != null) {

                    SendNotificationMobile sendNotificationMobile = new SendNotificationMobile();
                    sendNotificationMobile.sendNotificationSenior(seniorData.getString("token_id"), user.getString("full_name"));

                    json.addProperty("status", "OK");
                    json.addProperty("message", "Punched successfully");
                    json.addProperty("key", response.toString());
                } else {
                    json.addProperty("status", "ERROR");
                    json.addProperty("message", "Something went wrong!");
                }
            } else {
                json.addProperty("status", "ERROR");
                json.addProperty("message", "User id doesn't match!");
            }
        } catch (Exception e) {
            System.out.println(e);
            json.addProperty("status", "ERROR");
            json.addProperty("message", "Something went wrong!");
        }
        return json;
    }

    // Attendance Regularization
    public JsonObject pushRegularizationPunch(AttendanceRequestDto attendanceRequestDto) {
        JsonObject json = new JsonObject();
        try {
            long currentMonthTwentyOne = getDateInMillis("21", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            long previousMonthTwentyOne = getDateInMillis("21", String.valueOf(Calendar.getInstance().get(Calendar.MONTH)), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            long currentDateMillis = getCurrentDateMillis(new Date().getTime());
            long timestamp = Long.parseLong(attendanceRequestDto.getIncomingObject().get("timestamp").toString());
            // get user Details
            GetUserDetailImpl getUserDetails = new GetUserDetailImpl();
            Document user = getUserDetails.getUserDetails(attendanceRequestDto.getIncomingObject().get("user_id").toString(), database);

            AttendanceRequestDao dao = new AttendanceRequestDao();

            if (user != null) {
                String seniorLevel1 = getLastSeniorId(user.get("attendance_senior_level_I"));
                // Get senior details
                Document seniorData = getUserDetails.getUserDetails(seniorLevel1, database);

                if (currentDateMillis > currentMonthTwentyOne) {
                    // can apply only for current month 21 onwards
                    if (timestamp >= currentMonthTwentyOne) {
                        ObjectId response = dao.insertRegularizationPunch(user, getCurrentDateMillis(timestamp), seniorLevel1, attendanceRequestDto);
                        if (response != null) {

                            SendNotificationMobile sendNotificationMobile = new SendNotificationMobile();
                            sendNotificationMobile.sendNotificationSenior(seniorData.getString("token_id"), user.getString("full_name"));

                            json.addProperty("status", "OK");
                            json.addProperty("message", "Punched successfully");
                            json.addProperty("key", response.toString());
                        } else {
                            json.addProperty("status", "ERROR");
                            json.addProperty("message", "Something went wrong!");
                        }
                    } else {
                        json.addProperty("status", "ERROR");
                        json.addProperty("message", "You can apply till this month 21!");
                    }
                } else {
                    if (timestamp >= previousMonthTwentyOne) {
                        ObjectId response = dao.insertRegularizationPunch(user, getCurrentDateMillis(timestamp), seniorLevel1, attendanceRequestDto);
                        if (response != null) {
                            SendNotificationMobile sendNotificationMobile = new SendNotificationMobile();
                            sendNotificationMobile.sendNotificationSenior(seniorData.getString("token_id"), user.getString("full_name"));
                            json.addProperty("status", "OK");
                            json.addProperty("message", "Punched successfully");
                            json.addProperty("key", response.toString());
                        } else {
                            json.addProperty("status", "ERROR");
                            json.addProperty("message", "Something went wrong!");
                        }
                    } else {
                        json.addProperty("status", "ERROR");
                        json.addProperty("message", "You can apply till previous month 21!");
                    }
                }

            } else {
                json.addProperty("status", "ERROR");
                json.addProperty("message", "User id doesn't match!");
            }
        } catch (Exception e) {
            System.out.println(e);
            json.addProperty("status", "ERROR");
            json.addProperty("message", "Something went wrong!");
        }
        return json;
    }

    // Short-Break
    public JsonObject pushShortBreakPunch(AttendanceRequestDto attendanceRequestDto) {
        JsonObject json = new JsonObject();
        try {
            long currentDateMillis = getCurrentDateMillis(new Date().getTime());
            long currentMonthTwentyOne = getDateInMillis("21", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            long previousMonthTwentyOne = getDateInMillis("21", String.valueOf(Calendar.getInstance().get(Calendar.MONTH)), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

            long currentMonthTwenty = getDateInMillis("20", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            long nextMonthTwenty = getDateInMillis("20", String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 2), String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            // Requested Date
            long timestamp = Long.parseLong(attendanceRequestDto.getIncomingObject().get("timestamp").toString());

            long fromDate = 0l;
            long toDate = 0l;

            if (timestamp >= currentMonthTwentyOne) {
                fromDate = currentMonthTwentyOne;
                toDate = nextMonthTwenty;
            } else {
                fromDate = previousMonthTwentyOne;
                toDate = currentMonthTwenty;
            }
            // get user Details
            GetUserDetailImpl getUserDetails = new GetUserDetailImpl();
            Document user = getUserDetails.getUserDetails(attendanceRequestDto.getIncomingObject().get("user_id").toString(), database);
            if (user != null) {

                String seniorLevel1 = getLastSeniorId(user.get("attendance_senior_level_I"));
                // Get senior details
                Document seniorData = getUserDetails.getUserDetails(seniorLevel1, database);

                // get number of short-break applied in given range
                AttendanceRequestDao dao = new AttendanceRequestDao();
                long shortBreakCountInMonth = dao.checkShortBreakCountInGivenRange(user, fromDate, toDate);
                if (shortBreakCountInMonth < 2) {
                    // Check shortbreakCount for same day
                    long shortBreakCountAppliedDate = dao.checkShortBreakCountAppliedDate(user, getCurrentDateMillis(timestamp));
                    if (shortBreakCountAppliedDate == 0) {
                        // Add shortBreak
                        ObjectId response = dao.insertShortBreakPunch(user, getCurrentDateMillis(timestamp), seniorLevel1, attendanceRequestDto);
                        if (response != null) {

                            SendNotificationMobile sendNotificationMobile = new SendNotificationMobile();
                            sendNotificationMobile.sendNotificationSenior(seniorData.getString("token_id"), user.getString("full_name"));
                            
                            json.addProperty("status", "OK");
                            json.addProperty("message", "Punched successfully");
                            json.addProperty("key", response.toString());
                        } else {
                            json.addProperty("status", "ERROR");
                            json.addProperty("message", "Something went wrong!");
                        }
                    } else {
                        json.addProperty("status", "ERROR");
                        json.addProperty("message", "You can apply for only one short break per day!");
                    }
                } else {
                    json.addProperty("status", "ERROR");
                    json.addProperty("message", "You can apply for only two short breaks per month!");
                }
            } else {
                json.addProperty("status", "ERROR");
                json.addProperty("message", "User id doesn't match!");
            }
        } catch (Exception e) {
            json.addProperty("status", "ERROR");
            json.addProperty("message", "Something went wrong!");
        }
        return json;
    }

    public String getLastSeniorId(Object obj) {

        ObjectMapper oMapper = new ObjectMapper();
        // object -> Map
        Map<Long, String> map = oMapper.convertValue(obj, Map.class);
        NavigableMap<Long, String> sortedMap = new TreeMap<>(map);
        // add some entries
        Entry<Long, String> lastEntry = sortedMap.lastEntry();
        /**
         * Sorting Completed*
         */
        return lastEntry.getValue();
    }

    public long getCurrentDateMillis(long millis) {
        long dateMillis = 0l;
        try {
            // Date in dd/mm/yyyy
            Calendar fullDate = Calendar.getInstance();
            fullDate.setTimeInMillis(millis);
            String d = String.valueOf(fullDate.get(Calendar.DATE) < 10 ? "0" + fullDate.get(Calendar.DATE) : fullDate.get(Calendar.DATE))
                    + "/" + String.valueOf((fullDate.get(Calendar.MONTH) + 1) < 10 ? "0" + (fullDate.get(Calendar.MONTH) + 1) : (fullDate.get(Calendar.MONTH) + 1))
                    + "/" + String.valueOf(fullDate.get(Calendar.YEAR));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateMillis = sdf.parse(d).getTime();

        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return dateMillis;
    }

    public String getAddressByLatLong(float latitude, float longitude) {
        String address = "";
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&result_type=&key=AIzaSyAsRY8S3uhpEUJ9qVLgyqA4iSGE_07YO9g";

            URL urlObject = new URL(url);
            URLConnection yc = urlObject.openConnection();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new InputStreamReader(yc.getInputStream(), "UTF-8"));
            JSONArray results = (JSONArray) jsonObject.get("results");
            if (!results.isEmpty()) {
                JSONObject addressObject = (JSONObject) results.get(0);
                address = addressObject.get("formatted_address").toString();
            }
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            System.out.println(ex);
        }
        return address;
    }

    public long getDateInMillis(String date, String month, String year) {
        long dateMillis = 0l;
        try {
            // Date in dd/mm/yyyy
            Calendar fullDate = Calendar.getInstance();
            fullDate.setTimeInMillis(new Date().getTime());
            String d = date + "/" + month + "/" + year;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateMillis = sdf.parse(d).getTime();

        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return dateMillis;
    }

}
