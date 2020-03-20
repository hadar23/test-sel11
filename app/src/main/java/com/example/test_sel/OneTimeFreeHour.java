package com.example.test_sel;

public class OneTimeFreeHour {
    private String hostUserId;
    private String hostName;
    private String year;
    private String month;
    private String day;
    private String hour;

    public OneTimeFreeHour() {
    }

    public OneTimeFreeHour(String hostUserId, String hostName, String year, String month, String day, String hour) {
        this.hostUserId = hostUserId;
        this.hostName = hostName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
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
}
