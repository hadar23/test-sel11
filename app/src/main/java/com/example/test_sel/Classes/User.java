package com.example.test_sel.Classes;


import com.example.test_sel.OneTimeFreeHour;

import java.util.HashMap;

public class User implements CardInfo {
    private String userId;
    private String fullName;
    private String phone;
    private String academy;
    private String startYear;
    private String engineering;
    private String imagePath;
    private String description;
    private String coursesCounter;

    private HashMap<String, CoursePerUser> courses;
    private HashMap<String, HashMap<String, Meeting>> meetings = new HashMap<>();
    private HashMap<String, RecurrentFreeHour> recurrentFreeHours = new HashMap<>();
    private HashMap<String, HashMap<String, OneTimeFreeHour>> oneTimeFreeHours = new HashMap<>();
    private HashMap<String, Message> messages = new HashMap<>();



    public User() {

    }
    public User(String userId, String fullName, String phone, String academy, String startYear, String engineering, String imagePath, String description) {
        setUserId(userId);
        setFullName(fullName);
        setPhone(phone);
        setAcademy(academy);
        setStartYear(startYear);
        setEngineering(engineering);
        setImagePath(imagePath);
        setDescription(description);
        setCoursesCounter("0");
    }



    public void setCourses(HashMap<String, CoursePerUser> courses) {
        this.courses = courses;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getEngineering() {
        return engineering;
    }

    public void setEngineering(String engineering) {
        this.engineering = engineering;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getCoursesCounter() {
        return coursesCounter;
    }

    public void setCoursesCounter(String coursesCounter) {
        this.coursesCounter = coursesCounter;
    }

//    public HashMap<String, String> getCourses() {
//        return courses;
//    }
//
//    public void setCourses(HashMap<String, String> courses) {
//        this.courses = courses;
//    }

    public HashMap<String, HashMap<String, Meeting>> getMeetings() {
        return meetings;
    }

    public void setMeetings(HashMap<String, HashMap<String, Meeting>> meetings) {
        this.meetings = meetings;
    }

    public HashMap<String, RecurrentFreeHour> getRecurrentFreeHours() {
        return recurrentFreeHours;
    }

    public void setRecurrentFreeHours(HashMap<String, RecurrentFreeHour> recurrentFreeHours) {
        this.recurrentFreeHours = recurrentFreeHours;
    }

    public HashMap<String, HashMap<String, OneTimeFreeHour>> getOneTimeFreeHours() {
        return oneTimeFreeHours;
    }

    public void setOneTimeFreeHours(HashMap<String, HashMap<String, OneTimeFreeHour>> oneTimeFreeHours) {
        this.oneTimeFreeHours = oneTimeFreeHours;
    }

    public HashMap<String, Message> getMessages() {
        return messages;
    }

    public void setMessages(HashMap<String, Message> messages) {
        this.messages = messages;
    }

    @Override
    public String Title() {
        return getFullName();
    }

    @Override
    public String FirstRow() {
        //course name
        return getFullName();
    }

    @Override
    public String SecondRow() {
        return getEngineering();
    }
    //course code
    @Override
    public String ThirdRow() {
        return getCoursesCounter();
    }


    @Override
    public String LastRow() {
        return getCoursesCounter();
    }

    //course grade
    @Override
    public String Image() {
        return getImagePath();
    }

    @Override
    public String kind() {
        return "user";
    }
}





