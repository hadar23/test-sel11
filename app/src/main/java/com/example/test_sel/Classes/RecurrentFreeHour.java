package com.example.test_sel.Classes;

import java.util.HashMap;
import java.util.Map;

public class RecurrentFreeHour {
    private String hostUserId;
    private String hostName;
    private String dayInAWeek;
    private String hour;
    private HashMap<String, String> deleted = new HashMap<String, String>();

    public RecurrentFreeHour() {
    }

    public RecurrentFreeHour(String hostUserId, String hostName, String dayInAWeek, String hour) {
        this.hostUserId = hostUserId;
        this.hostName = hostName;
        this.dayInAWeek = dayInAWeek;
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

    public String getDayInAWeek() {
        return dayInAWeek;
    }

    public void setDayInAWeek(String dayInAWeek) {
        this.dayInAWeek = dayInAWeek;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public HashMap<String, String> getDeleted() {
        return deleted;
    }

    public void setDeleted(HashMap<String, String> deleted) {
        this.deleted = deleted;
    }

    public void addToDeleted(String key){
        this.deleted.put(key, key);
    }

    public void removeFromDeleted(String key){
        this.deleted.remove(key);
    }
}