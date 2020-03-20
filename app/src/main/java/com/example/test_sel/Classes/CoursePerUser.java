package com.example.test_sel.Classes;

import java.io.Serializable;

public class CoursePerUser implements Serializable, CardInfo {
    private String courseCode;
    private String courseName;
    private String courseLevel;
    private String courseImage;
    private String courseUserGrade;
    private String userId;
    private String userName;
    private String userPhone;
    private String userImage;
    private String typeToShow;

    final public static String SHOW_AS_USER = "showAsUser";
    final public static String SHOW_AS_COURSE = "showAsCourse";

    public CoursePerUser() {

    }

    public CoursePerUser(String courseCode, String courseName, String courseLevel, String courseImage,
                         String courseUserGrade, String userId, String userName, String userPhone,
                         String userImage, String typeToShow) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.courseLevel = courseLevel;
        this.courseImage = courseImage;
        this.courseUserGrade = courseUserGrade;
        this.userId = userId;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userImage = userImage;
        this.typeToShow = typeToShow;
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

    public String getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = courseLevel;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public String getCourseUserGrade() {
        return courseUserGrade;
    }

    public void setCourseUserGrade(String courseUserGrade) {
        this.courseUserGrade = courseUserGrade;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getTypeToShow() {
        return typeToShow;
    }

    public void setTypeToShow(String typeToShow) {
        this.typeToShow = typeToShow;
    }

    @Override
    public String Title() {
        return getUserName();
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
        return  getCourseUserGrade();
    }

    @Override
    public String LastRow() {
        return getCourseLevel();
    }

    @Override
    public String Image() {
        String imagePath = "";
        if(typeToShow.equals(SHOW_AS_USER)){
            imagePath = userImage;
        } else{
            imagePath = courseImage;
        }
        return imagePath;
    }

    @Override
    public String kind() {
        String kind = "";
        if(typeToShow.equals(SHOW_AS_USER)){
            kind = "userPerCourse";
        } else{
            kind = "coursePerUser";
        }
        return kind;
    }
}
