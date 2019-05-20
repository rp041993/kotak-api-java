/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abacus.kotak.api.service;

import com.abacus.kotak.api.dao.AttendanceReportSingleUserControllerDao;
import com.abacus.kotak.api.bean.AttendanceReportSingleUserControllerDto;
import com.abacus.kotak.api.controller.AttendanceReportSingleUserController;
import com.abacus.kotak.api.dao.GetShiftDetailImpl;
import com.google.gson.JsonObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author abacus1
 */
public class AttendanceReportSingleUserControllerModal {

    static JSONArray PunchDetails = new JSONArray();

    public JSONArray getReportSingleUser(AttendanceReportSingleUserControllerDto reportDto) {
        PunchDetails.clear();
        AttendanceReportSingleUserControllerDao reportDao = new AttendanceReportSingleUserControllerDao();
        Iterator punchIterator = reportDao.getSingleUserPunches(reportDto);
        IterateMap(genratePunchMap(punchIterator), reportDto);
        return PunchDetails;
//        return responseArray;
    }

    public JSONArray getReportInterventionUser(AttendanceReportSingleUserControllerDto reportDto) {
        PunchDetails.clear();
        AttendanceReportSingleUserControllerDao reportDao = new AttendanceReportSingleUserControllerDao();
        Iterator punchIterator = reportDao.getInterventionUserPunches(reportDto);
        HashMap<String, HashMap<Long, HashMap<String, ArrayList<Document>>>> map = genratePunchMap(punchIterator);
        IterateMap(map, reportDto);
        return PunchDetails;
//        return responseArray;
    }

    public JSONArray getReportAllUser(AttendanceReportSingleUserControllerDto reportDto) {
        PunchDetails.clear();
        AttendanceReportSingleUserControllerDao reportDao = new AttendanceReportSingleUserControllerDao();
        Iterator punchIterator = reportDao.getAllUserPunches(reportDto);
        IterateMap(genratePunchMap(punchIterator), reportDto);
        return PunchDetails;
//        return responseArray;
    }

    public static HashMap<String, HashMap<Long, HashMap<String, ArrayList<Document>>>> genratePunchMap(Iterator punchIterator) {

        HashMap<String, HashMap<Long, HashMap<String, ArrayList<Document>>>> map = new HashMap<>();

        while (punchIterator.hasNext()) {
            try {
                Document doc = (Document) punchIterator.next();
                long dateMillis = convertDateToMilliis(doc.getString("date") + "/" + doc.getString("month") + "/" + doc.getString("year"));
                // If map contains userkey
                if (map.containsKey(doc.getString("user_id"))) {
                    String userId = doc.getString("user_id");

                    // If userKey contains dateKey
                    if (map.get(userId).containsKey(dateMillis)) {

                        if (map.get(userId).get(dateMillis).containsKey("punch")) {
                            if (doc.containsKey("type")) {
                                if ((doc.getString("type").equalsIgnoreCase("Remote") && doc.getString("approval_status").equalsIgnoreCase("APPROVED"))
                                        || doc.getString("type").equalsIgnoreCase("Normal")
                                        || (doc.getString("type").equalsIgnoreCase("Attendance Regularization") && doc.getString("approval_status").equalsIgnoreCase("APPROVED"))) {
                                    map.get(userId).get(dateMillis).get("punch").add(doc);
                                }
                                if (doc.getString("type").equalsIgnoreCase("ShortBreak") && doc.getString("approval_status").equalsIgnoreCase("APPROVED")) {
                                    map.get(userId).get(dateMillis).get("punch").add(doc);
                                }
                            }
                        }

                        if (map.get(userId).get(dateMillis).containsKey("leave")) {
                            if (doc.containsKey("status")) {
                                if ((doc.getString("status").equalsIgnoreCase("Leave") || doc.getString("status").equalsIgnoreCase("Compoff"))
                                        && doc.getString("senior_level1_status").equalsIgnoreCase("APPROVED")
                                        && (doc.getString("senior_level2_status").equalsIgnoreCase("APPROVED") || doc.getString("senior_level2_status").equalsIgnoreCase("NA"))) {
                                    map.get(userId).get(dateMillis).get("leave").add(doc);
                                }
                            }
                        }
                        if (map.get(userId).get(dateMillis).containsKey("satOff")) {
                            if (doc.containsKey("status")) {
                                if (doc.getString("status").equalsIgnoreCase("Saturday_Off")
                                        && doc.getString("approval_status").equalsIgnoreCase("APPROVED")) {
                                    map.get(userId).get(dateMillis).get("satOff").add(doc);
                                }
                            }
                        }
                    } else {
                        HashMap<String, ArrayList<Document>> obj = createNestedInnerMap(doc);
                        if (obj.size() != 0) {
                            map.get(userId).put(dateMillis, obj);
                        }
                    }
                } else {
                    HashMap<Long, HashMap<String, ArrayList<Document>>> innerMap = new HashMap<>();
                    HashMap<String, ArrayList<Document>> obj = createNestedInnerMap(doc);
                    if (obj.size() != 0) {
                        innerMap.put(dateMillis, obj);
                        map.put(doc.getString("user_id"), innerMap);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static long convertDateToMilliis(String date) {
        Date dateMillis = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateMillis = sdf.parse(date);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return dateMillis.getTime();
    }

    private static HashMap<String, ArrayList<Document>> createNestedInnerMap(Document doc) {
        HashMap<String, ArrayList<Document>> nestedInnerMap = new HashMap<>();
        ArrayList<Document> arrayList = new ArrayList<Document>();
        arrayList.add(doc);
        try {
            // if Approved Punch
            if (doc.containsKey("type")) {
                if ((doc.getString("type").equalsIgnoreCase("Remote") && doc.getString("approval_status").equalsIgnoreCase("APPROVED"))
                        || doc.getString("type").equalsIgnoreCase("Normal")
                        || (doc.getString("type").equalsIgnoreCase("Attendance Regularization") && doc.getString("approval_status").equalsIgnoreCase("APPROVED"))) {
                    nestedInnerMap.put("punch", arrayList);
                }
                if (doc.getString("type").equalsIgnoreCase("ShortBreak") && doc.getString("approval_status").equalsIgnoreCase("APPROVED")) {
                    nestedInnerMap.put("punch", arrayList);
                }
            }
            if (doc.containsKey("status")) {
                // if Approved Leave
                if ((doc.getString("status").equalsIgnoreCase("Leave") || doc.getString("status").equalsIgnoreCase("Compoff"))
                        && doc.getString("senior_level1_status").equalsIgnoreCase("APPROVED")
                        && (doc.getString("senior_level2_status").equalsIgnoreCase("APPROVED") || doc.getString("senior_level2_status").equalsIgnoreCase("NA"))) {
                    nestedInnerMap.put("leave", arrayList);
                }
                if (doc.getString("status").equalsIgnoreCase("Saturday_Off")
                        && doc.getString("approval_status").equalsIgnoreCase("APPROVED")) {
                    nestedInnerMap.put("satOff", arrayList);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return nestedInnerMap;
    }

    private static void IterateMap(HashMap<String, HashMap<Long, HashMap<String, ArrayList<Document>>>> map, AttendanceReportSingleUserControllerDto reportDto) {
        int lateMark = 0;

        AttendanceReportSingleUserControllerDao daoObj = new AttendanceReportSingleUserControllerDao();

        GetShiftDetailImpl getShiftDetailImpl = new GetShiftDetailImpl();
        //Get Shifts
        HashMap<String, JSONObject> shifts = getShiftDetailImpl.getShifts(AttendanceReportSingleUserController.database);

        //Get HolidayList
        ArrayList holidayList = daoObj.getHolidays();

        //Get date range
        Date fromDate = new Date(reportDto.getFromDate());
        Date toDate = new Date(reportDto.getToDate());
        //  {userId : {dateMillis : {"leave":[{},{}] , "punch": [{},{}], satOff:[{},{}] }}}
        List<Long> dateRange = getDatesBetween(fromDate, toDate);

        Iterator<Map.Entry<String, HashMap<Long, HashMap<String, ArrayList<Document>>>>> itr = map.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, HashMap<Long, HashMap<String, ArrayList<Document>>>> userNode = itr.next();
            HashMap<Long, HashMap<String, ArrayList<Document>>> userNodeObject = userNode.getValue();

            //get user Details
            AttendanceReportSingleUserControllerDao reportDao = new AttendanceReportSingleUserControllerDao();
            Document userDetails = reportDao.getUserDetails(userNode.getKey());
            //  Push User details into punch array
            pushUserObject("Name", userDetails.getString("full_name"), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            pushUserObject("Intervention", userDetails.getString("intervention"), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            pushUserObject("Emp-Id", userDetails.getString("emp_id"), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            pushUserObject("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

            //get user_shift_mapping
            Document userShiftDetails = getShiftDetailImpl.getUserShiftDetails(userNode.getKey(), AttendanceReportSingleUserController.database);

            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'000Z'");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());

            int absentCount = 0;
            int halfDayCount = 0;
            int weeklyOffCount = 0;
            int fullDayCount = 0;
            int holidayCount = 0;
            int SatOffCount = 0;
            int presentCount = 0;
            int plCount = 0;
            int cslCount = 0;
            int compOffCount = 0;
            int lateMarkCount = 0;
            int specialLeaveCount = 0;
            int shortBreakCount = 0;
            int maternityLeaveCount = 0;
            int paternityLeaveCount = 0;
            //  {dateMillis : {"leave":[{},{}] , "punch": [{},{}], satOff:[{},{}] }}}
            //Iterate for sorting punch 
            for (Long date : dateRange) {

                // Date in dd/mm/yyyy
                Calendar fullDate = Calendar.getInstance();
                fullDate.setTimeInMillis(date);
                String d = String.valueOf(fullDate.get(Calendar.DATE) < 10 ? "0" + fullDate.get(Calendar.DATE) : fullDate.get(Calendar.DATE))
                        + "/" + String.valueOf((fullDate.get(Calendar.MONTH) + 1) < 10 ? "0" + (fullDate.get(Calendar.MONTH) + 1) : (fullDate.get(Calendar.MONTH) + 1))
                        + "/" + String.valueOf(fullDate.get(Calendar.YEAR));

                Date dateForDay = new Date(date);
                String dayName = dateForDay.toString().split(" ")[0];

                if (!userNodeObject.containsKey(date)) {
                    // Absent
                    if (dayName.equals("Sun")) {
                        weeklyOffCount++;
                        pushUserObject(d, dayName, "Weekly Off", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                    } else if (holidayList.contains(date)) {
                        holidayCount++;
                        pushUserObject(d, dayName, "Holiday", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                    } else {
                        absentCount++;
                        pushUserObject(d, dayName, "Absent", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
                    }
                } else {
                    HashMap<String, ArrayList<Document>> allRecordMap = userNodeObject.get(date);
                    //  {"leave":[{},{}] , "punch": [{},{}], satOff:[{},{}] }}
                    // Punch
                    if (allRecordMap.containsKey("punch")) {
                        presentCount++;
                        try {
                            // getShiftData
                            JSONObject shiftDetails = getShiftDetails(userShiftDetails, date, shifts);
                            ArrayList<Document> punchArray = allRecordMap.get("punch");

                            String shortBreakTime = "";
                            String shortBreakReason = "";
                            String shortBreakStatus = "";
                            for (Document doc1 : punchArray) {
                                if (doc1.containsKey("type")) {
                                    if (doc1.getString("type").equalsIgnoreCase("ShortBreak") && doc1.getString("approval_status").equalsIgnoreCase("APPROVED")) {
                                        shortBreakTime = doc1.getString("time");
                                        shortBreakStatus = "Short-Break";
                                        if (doc1.containsKey("reason_for_short_break")) {
                                            shortBreakReason = doc1.getString("reason_for_short_break");
                                        }
                                        shortBreakCount++;
                                    }
                                }
                            }

                            Document inPunch = punchArray.get(0);
                            Document outPunch = punchArray.get((punchArray.size() - 1));

                            String[] inTime = inPunch.getString("time").split(":");
                            long inTimeInseconds = Integer.parseInt(inTime[0]) * 60 * 60 + Integer.parseInt(inTime[1]) * 60 + Integer.parseInt(inTime[2]);// minutes are worth 60 seconds. Hours are worth 60 minutes.

                            String[] outTime = outPunch.getString("time").split(":");
                            long outTimeInseconds = Integer.parseInt(outTime[0]) * 60 * 60 + Integer.parseInt(outTime[1]) * 60 + Integer.parseInt(outTime[2]);// minutes are worth 60 seconds. Hours are worth 60 minutes.

                            String totalHours = df.format(new Date(Math.abs(outTimeInseconds - inTimeInseconds) * 1000)).toString().substring(11, 19);

                            String shiftName = (String) shiftDetails.get("shiftName");
                            String shiftInTime = (String) shiftDetails.get("shiftInTime");
                            String shiftOutTime = (String) shiftDetails.get("shiftOutTime");

                            long shiftInTimeInSeconds = Integer.parseInt(shiftInTime.split(":")[0]) * 60 * 60 + Integer.parseInt(shiftInTime.split(":")[1]) * 60;
                            long shiftOutTimeInSeconds = Integer.parseInt(shiftOutTime.split(":")[0]) * 60 * 60 + Integer.parseInt(shiftOutTime.split(":")[1]) * 60;

                            String OT = "00:00:00";
                            String status = "In";

                            String earlyBy = "00:00:00";
                            if (outTimeInseconds < shiftOutTimeInSeconds) {
                                earlyBy = df.format(new Date(Math.abs(shiftOutTimeInSeconds - outTimeInseconds) * 1000)).toString().substring(11, 19);
                            }

                            // Late By
                            String lateBy = "00:00:00";
                            if (inTimeInseconds > shiftInTimeInSeconds) {
                                lateBy = df.format(new Date(Math.abs(inTimeInseconds - shiftInTimeInSeconds) * 1000)).toString().substring(11, 19);
                            }

                            // Late mark
                            if (inTimeInseconds > shiftInTimeInSeconds) {
                                if ((inTimeInseconds - shiftInTimeInSeconds) / 60 > 15) {
                                    lateMark++;
                                }
                            }

                            // status
                            if (punchArray.size() > 1) {
                                status = "Out";
                            }

                            // OverTime
                            if (!status.equals("In")) {
                                if (Math.abs(outTimeInseconds - inTimeInseconds) > Math.abs(shiftOutTimeInSeconds - shiftInTimeInSeconds)) {
                                    OT = df.format(new Date(Math.abs((outTimeInseconds - inTimeInseconds) - (shiftOutTimeInSeconds - shiftInTimeInSeconds)) * 1000)).toString().substring(11, 19);
                                }
                            }

                            // Half day full day status
                            String workingStatus = "";
                            long totalHoursInSeconds = Math.abs(outTimeInseconds - inTimeInseconds);
                            long totalShiftHoursInSeconds = Math.abs(shiftOutTimeInSeconds - shiftInTimeInSeconds);
                            if (!status.equals("In")) {
                                if (totalShiftHoursInSeconds - totalHoursInSeconds >= 4) {
                                    workingStatus = "Half Day";
                                    halfDayCount++;
                                } else {
                                    workingStatus = "Full Day";
                                    fullDayCount++;
                                }
                            }

                            String inRemoteLocation = "";
                            String outRemoteLocation = "";
                            if (inPunch.getString("type") == "Remote" || inPunch.getString("type") == "Attendance Regularization") {
                                inRemoteLocation = inPunch.getString("display_name");
                            }
                            if (outPunch.getString("type") == "Remote" || outPunch.getString("type") == "Attendance Regularization") {
                                outRemoteLocation = outPunch.getString("display_name");
                            }
                            // Get Day from timestamp
                            pushUserObject(d, dayName, "Present", inPunch.get("time").toString(),
                                    outPunch.get("time").toString(), totalHours, status, inPunch.get("type").toString(), inRemoteLocation,
                                    outPunch.get("type").toString(), outRemoteLocation, shiftName, shiftInTime, shiftOutTime, lateBy,
                                    earlyBy, OT, shortBreakStatus, shortBreakTime, shortBreakReason, workingStatus, "");

                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    } // Leave
                    else {
                        String status = "";
                        String leaveType = "";
                        if (dayName.equals("Sun")) {
                            weeklyOffCount++;
                            status = "Weekly Off";
                        } else {
                            // check date is holiday
                            if (holidayList.contains(date)) {
                                holidayCount++;
                                status = "Holiday";
                            } // check leave
                            else if (allRecordMap.containsKey("leave")) {
                                Document leaveObject = allRecordMap.get("leave").get(0);
                                if (leaveObject.getString("leave_type").equalsIgnoreCase("Casual & Sick Leave")) {
                                    cslCount++;
                                    status = "Casual & Sick Leave";
                                } else if (leaveObject.getString("leave_type").equalsIgnoreCase("Privilege Leave")) {
                                    plCount++;
                                    status = "Privilege Leave";
                                } else if (leaveObject.getString("leave_type").equalsIgnoreCase("Comp Off")) {
                                    compOffCount++;
                                    status = "Comp Off";
                                } else if (leaveObject.getString("leave_type").equalsIgnoreCase("Special Leave")) {
                                    specialLeaveCount++;
                                    status = "Special Leave";
                                } else if (leaveObject.getString("leave_type").equalsIgnoreCase("Maternity Leave")) {
                                    maternityLeaveCount++;
                                    status = "Maternity Leave";
                                } else if (leaveObject.getString("leave_type").equalsIgnoreCase("Paternity Leave")) {
                                    paternityLeaveCount++;
                                    status = "Paternity Leave";
                                }

                                if (!status.equals("")) {
                                    if (leaveObject.containsKey("day_type")) {
                                        leaveType = leaveObject.getString("day_type");
                                    }
                                }
                            } // Satoff
                            else if (allRecordMap.containsKey("satOff")) {
                                SatOffCount++;
                                Document satOffObject = allRecordMap.get("satOff").get(0);
                                status = "Saturday Off";
                            }
                        }

                        pushUserObject(d, dayName, status, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", leaveType);
                    }
                }
            }

            // Add PL CSL .... Count
            pushUserObject("PL", String.valueOf(plCount), "", "", "CSL", String.valueOf(cslCount),
                    "", "", "Comp-Off", String.valueOf(compOffCount), "", "", "Weekly-Off", String.valueOf(weeklyOffCount),
                    "", "", "Holiday", String.valueOf(holidayCount), "", "", "", "");

            pushUserObject("Sat-Off", String.valueOf(SatOffCount), "", "", "Present", String.valueOf(presentCount),
                    "", "", "Absent", String.valueOf(absentCount), "", "", "Late-Mark", String.valueOf(lateMarkCount),
                    "", "", "Special-leave", String.valueOf(specialLeaveCount), "", "", "", "");

            pushUserObject("Shortbreak", String.valueOf(shortBreakCount), "", "", "Half-Day", String.valueOf(halfDayCount),
                    "", "", "Full-Day", String.valueOf(fullDayCount), "", "", "Maternity", String.valueOf(maternityLeaveCount),
                    "", "", "Paternity", String.valueOf(paternityLeaveCount), "", "", "", "");

            // Push Blank Object
            pushUserObject("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

            pushUserObject("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");

        }
    }

    private static List<Long> getDatesBetween(
            Date startDate, Date endDate) {
        List<Long> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    private static JSONObject getShiftDetails(Document shiftObject, Long date, HashMap<String, JSONObject> shifts) {

        JSONObject shiftResponseData = new JSONObject();
        try {
            if (shiftObject != null) {
                if (shiftObject.containsKey("is_permanent_req") && shiftObject.getBoolean("is_permanent_req")) {
                    if (shiftObject.getLong("permanent_from_time") >= date) {
                        // Permanent shift change
                        shiftResponseData.put("shiftName", shiftObject.get("permanent_shift_name"));
                        shiftResponseData.put("shiftInTime", shifts.get(shiftObject.get("permanent_shift_name")).get("shiftInTime"));
                        shiftResponseData.put("shiftOutTime", shifts.get(shiftObject.get("permanent_shift_name")).get("shiftOutTime"));
                    } else {
                        // Not permanent shift
                        shiftResponseData = getNotPermanentShiftDetails(shiftObject, date, shifts);
                    }
                } else {
                    // Not permanent shift
                    shiftResponseData = getNotPermanentShiftDetails(shiftObject, date, shifts);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return shiftResponseData;
    }

    // Not permanent shit details
    private static JSONObject getNotPermanentShiftDetails(Document shiftObject, Long date, HashMap<String, JSONObject> shifts) {
        JSONObject shiftResponseData = new JSONObject();
        if (!shiftObject.containsKey("shift_change_data")) {
            shiftResponseData.put("shiftName", shiftObject.get("name"));
            shiftResponseData.put("shiftInTime", shiftObject.getString("shift_time").split("-")[0].split(" ")[0]);
            shiftResponseData.put("shiftOutTime", shiftObject.getString("shift_time").split("-")[1].split(" ")[0]);
        } else {
            ArrayList<Document> shiftChangeDataArray = (ArrayList<Document>) shiftObject.get("shift_change_data");
            for (int i = 0; i < shiftChangeDataArray.size(); i++) {
                Document doc = (Document) shiftChangeDataArray.get(i);
                if (doc.containsKey("dateMillis") && doc.getLong("dateMillis") == date) {
                    shiftResponseData.put("shiftName", doc.get("shift_name"));
                    shiftResponseData.put("shiftInTime", shifts.get(doc.get("shift_name")).get("shiftInTime"));
                    shiftResponseData.put("shiftOutTime", shifts.get(doc.get("shift_name")).get("shiftOutTime"));
                } else {
                    shiftResponseData.put("shiftName", shiftObject.get("name"));
                    shiftResponseData.put("shiftInTime", shiftObject.getString("shift_time").split("-")[0].split(" ")[0]);
                    shiftResponseData.put("shiftOutTime", shiftObject.getString("shift_time").split("-")[1].split(" ")[0]);
                }
            }
        }

        return shiftResponseData;
    }

    private static void pushUserObject(String date, String day, String status, String inTime, String outTime,
            String total, String attendenceStatus, String inAttendenceType, String inAttendenceLocation, String out_attendance_type,
            String out_attendance_location, String shiftName, String shiftInTime, String shiftOutTime, String lateBy, String earlyBy, String OT,
            String shortBreakStatus, String shortBreakTime, String shortBreakReason, String attendanceStatus, String leaveType) {

        JsonObject blankObject = new JsonObject();
        blankObject.addProperty("date", date);
        blankObject.addProperty("day", day);
        blankObject.addProperty("status", status);
        blankObject.addProperty("inTime", inTime);
        blankObject.addProperty("outTime", outTime);
        blankObject.addProperty("total", total);
        blankObject.addProperty("attendenceStatus", attendenceStatus);
        blankObject.addProperty("inAttendenceType", inAttendenceType);
        blankObject.addProperty("inAttendenceLocation", inAttendenceLocation);
        blankObject.addProperty("out_attendance_type", out_attendance_type);
        blankObject.addProperty("out_attendance_location", out_attendance_location);
        blankObject.addProperty("shiftName", shiftName);
        blankObject.addProperty("shiftInTime", shiftInTime);
        blankObject.addProperty("shiftOutTime", shiftOutTime);
        blankObject.addProperty("lateBy", lateBy);
        blankObject.addProperty("earlyBy", earlyBy);
        blankObject.addProperty("OT", OT);
        blankObject.addProperty("shortBreakStatus", shortBreakStatus);
        blankObject.addProperty("shortBreakTime", shortBreakTime);
        blankObject.addProperty("shortBreakReason", shortBreakReason);
        blankObject.addProperty("attendanceStatus", attendanceStatus);
        blankObject.addProperty("leaveType", leaveType);
        PunchDetails.add(blankObject);
    }

}
