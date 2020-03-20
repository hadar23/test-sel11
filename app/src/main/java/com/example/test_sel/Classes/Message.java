package com.example.test_sel.Classes;

import com.example.test_sel.Classes.CardInfo;

import java.io.Serializable;
import java.util.Calendar;

public class Message implements Serializable, CardInfo {
    private String hostUserId;
    private String hostName;
    private String guestUserId;
    private String guestName;
    private String meetingYear;
    private String meetingMonth;
    private String meetingDay;
    private String meetingHour;
    private String messageTimeInMilli;
    private String courseCode;
    private String courseName;
    private String typeOfMessage;
    private String initiator;
    private String recipient;
    private String imagePath;

    final public static String SCHEDULE_MESSAGE = "scheduleMessage";
    final public static String CANCEL_MESSAGE = "cancelMessage";
    final public static String HOST = "host";
    final public static String GUEST = "guest";

    public Message() {
    }

    public Message(String hostUserId, String hostName, String guestUserId, String guestName, String meetingYear, String meetingMonth, String meetingDay, String meetingHour, String messageTimeInMilli, String courseCode, String courseName, String typeOfMessage, String initiator, String recipient, String imagePath) {
        this.hostUserId = hostUserId;
        this.hostName = hostName;
        this.guestUserId = guestUserId;
        this.guestName = guestName;
        this.meetingYear = meetingYear;
        this.meetingMonth = meetingMonth;
        this.meetingDay = meetingDay;
        this.meetingHour = meetingHour;
        this.messageTimeInMilli = messageTimeInMilli;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.typeOfMessage = typeOfMessage;
        this.initiator = initiator;
        this.recipient = recipient;
        this.imagePath = imagePath;
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

    public String getMeetingYear() {
        return meetingYear;
    }

    public void setMeetingYear(String meetingYear) {
        this.meetingYear = meetingYear;
    }

    public String getMeetingMonth() {
        return meetingMonth;
    }

    public void setMeetingMonth(String meetingMonth) {
        this.meetingMonth = meetingMonth;
    }

    public String getMeetingDay() {
        return meetingDay;
    }

    public void setMeetingDay(String meetingDay) {
        this.meetingDay = meetingDay;
    }

    public String getMeetingHour() {
        return meetingHour;
    }

    public void setMeetingHour(String meetingHour) {
        this.meetingHour = meetingHour;
    }

    public String getMessageTimeInMilli() {
        return messageTimeInMilli;
    }

    public void setMessageTimeInMilli(String messageTimeInMilli) {
        this.messageTimeInMilli = messageTimeInMilli;
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

    public String getTypeOfMessage() {
        return typeOfMessage;
    }

    public void setTypeOfMessage(String typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String Title() {
        return "";
    }

    @Override
    public String FirstRow() {
        String initiatorName = initiator.equals(recipient) ? "you" : initiator.equals(HOST) ? hostName : guestName;
        String bodyText = typeOfMessage.equals(CANCEL_MESSAGE) ? " canceled your meeting" : " scheduled a meeting with you";
        String postfix = typeOfMessage.equals(SCHEDULE_MESSAGE) || !initiator.equals(recipient) ? "" : initiator.equals(HOST) ? " " + guestName : " " + hostName;
        return initiatorName + bodyText + postfix;
    }

    @Override
    public String SecondRow() {
        String text = "date of meeting: ";
        String date = String.format("%s-%s-%s, %s:00:00", meetingYear, meetingMonth,meetingDay, meetingHour);
        return text + date;
    }

    @Override
    public String ThirdRow() {
        Calendar time = Calendar.getInstance();
        time.clear();
        time.set(Calendar.MILLISECOND, Integer.parseInt(messageTimeInMilli));
        String text = "date of message: ";
        String year = String.valueOf(time.get(Calendar.YEAR));
        String month = String.valueOf(time.get(Calendar.MONTH));
        String day = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
        String hour = String.valueOf(time.get(Calendar.HOUR));
        String minute = String.valueOf(time.get(Calendar.MINUTE));
        String second = String.valueOf(time.get(Calendar.SECOND));
        String millisecond = String.valueOf(time.get(Calendar.MILLISECOND));

        String date = String.format("%s-%s-%s, %s:%s:%s.%s", year, month, day, hour, minute, second, millisecond);
        return text + date;
    }

    @Override
    public String LastRow() {
        return "";
    }

    @Override
    public String Image() {
        return imagePath;
    }

    @Override
    public String kind() {
        return "message";
    }
}
