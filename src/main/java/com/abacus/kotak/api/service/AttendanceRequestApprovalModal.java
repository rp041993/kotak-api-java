/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.service;

import com.abacus.kotak.api.bean.AttendanceRequestApprovalDto;
import static com.abacus.kotak.api.controller.AttendanceRequest.database;
import com.abacus.kotak.api.dao.AttendanceRequestApprovalDao;
import com.abacus.kotak.api.dao.GetUserDetailImpl;
import com.abacus.kotak.api.utils.SendNotificationMobile;
import com.google.gson.JsonObject;
import org.bson.Document;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public class AttendanceRequestApprovalModal {

    public JsonObject approvePunches(AttendanceRequestApprovalDto attendanceRequestapprovalDto) {
        JsonObject json = new JsonObject();
        JSONObject obj = attendanceRequestapprovalDto.getIncomingObject();
        String punchType = (String) obj.get("type");
        try {
            if (punchType.equals("Remote")
                    || punchType.equals("Attendance Regularization")
                    || punchType.equals("ShortBreak")) {

                String approvedBy = "disapproved_by";
                String approvalStatus = "DECLINED";
                String approvedAt = "disapproved_at";

                if (obj.get("app_disapp_flag").equals("approve")) {
                    approvedBy = "approved_by";
                    approvedAt = "approved_at";
                    approvalStatus = "APPROVED";
                }

                // fetch user Details
                GetUserDetailImpl getUserDetails = new GetUserDetailImpl();
                Document user = getUserDetails.getUserDetails(obj.get("user_id").toString(), database);

                // Approve/Decline record punchinout_timestamp node
                AttendanceRequestApprovalDao attendanceRequestApprovalDao = new AttendanceRequestApprovalDao();
                boolean updateResponse = attendanceRequestApprovalDao.updatePunchInOutTimestamp(
                        attendanceRequestapprovalDto, approvedBy,
                        approvalStatus, approvedAt);

                if (updateResponse) {
                    json.addProperty("status", "OK");
                    json.addProperty("message", approvalStatus);
                    //send notification to user
                    if (user.containsKey("token_id")) {
                        SendNotificationMobile sendNotificationMobile = new SendNotificationMobile();
                        sendNotificationMobile.sendNotificationUser(user.getString("token_id"),
                                punchType, approvalStatus);
                    }
                } else {
                    json.addProperty("status", "ERROR");
                    json.addProperty("message", "Something went wrong!");
                }

            }
        } catch (Exception e) {
            System.out.println(e);
            json.addProperty("status", "ERROR");
            json.addProperty("message", "Something went wrong!");
        }
        return json;
    }
}
