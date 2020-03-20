package com.example.test_sel.Classes;

public class Meeting {
    private String hostUserId;
    private String hostName;
    private String guestUserId;
    private String guestName;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String courseCode;
    private String courseName;
    private String typeOfEventBeforeMeeting;

    final public static String ONE_TIME_FREE_HOUR = "oneTimeFreeHour";
    final public static String RECURRENT_FREE_HOUR = "recurrentFreeHour";



    public Meeting() {
    }

    public Meeting(String hostUserId, String hostName, String guestUserId, String guestName, String year, String month, String day, String hour, String courseCode, String courseName, String typeOfEventBeforeMeeting) {
        this.hostUserId = hostUserId;
        this.hostName = hostName;
        this.guestUserId = guestUserId;
        this.guestName = guestName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.typeOfEventBeforeMeeting = typeOfEventBeforeMeeting;
    }

    public String getHostUserId() {
        return hostUserId;
    }

    public void setHostUserId(String hostUserId) {
        this.hostUserId = hostUserId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getGuestUserId() {
        return guestUserId;
    }

    public void setGuestUserId(String guestUserId) {
        this.guestUserId = guestUserId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTypeOfEventBeforeMeeting() {
        return typeOfEventBeforeMeeting;
    }

    public void setTypeOfEventBeforeMeeting(String typeOfEventBeforeMeeting) {
        this.typeOfEventBeforeMeeting = typeOfEventBeforeMeeting;
    }
}