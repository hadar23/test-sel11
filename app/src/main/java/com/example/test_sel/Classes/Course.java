package com.example.test_sel.Classes;

import java.io.Serializable;
import java.util.HashMap;

public class Course implements Serializable, CardInfo {
    private String courseCode;
    private String courseName;
    private String usersCounter;
    private HashMap<String, String> users;
    private HashMap<String, CoursePerUser> usersList;

    public Course() {

    }

    public Course(String courseCode, String courseName) {
        setCourseCode(courseCode);
        setCourseName(courseName);
        setUsersCounter("0");
        //        setUsersCounter(usersCounter);
        setUsers(new HashMap<String, String>());
        setUsersList(new HashMap<String, CoursePerUser>());

    }

    public HashMap<String, CoursePerUser> getUsersList() {
        return usersList;
    }

    public void setUsersList(HashMap<String, CoursePerUser> usersList) {
        this.usersList = usersList;
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

    public String getUsersCounter() {
        return usersCounter;
    }

    public void setUsersCounter(String usersCounter) {
        this.usersCounter = usersCounter;
    }

    public HashMap<String, String> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, String> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseCode=" + courseCode +
                ", courseName='" + courseName + '\'' +
                ", usersCounter=" + usersCounter +
                ", users=" + users +
                '}';
    }

    @Override
    public String Title() {
        return "";

    }

    @Override
    public String FirstRow() {
    return  getCourseName();
    }

    @Override
    public String SecondRow() {
        return  getCourseCode();
    }

    @Override
    public String ThirdRow() {
        return  getUsersCounter();
    }

    @Override
    public String LastRow() {
        return "";
    }

    @Override
    public String Image() {
        return "";
//        getImage();

    }

    @Override
    public String kind() {
        return "course";
    }
}
