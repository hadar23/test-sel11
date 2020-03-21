package com.example.test_sel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test_sel.Callback.CallBack_StringValueReady;
import com.example.test_sel.Callback.CallBack_UserReady;
import com.example.test_sel.Classes.CoursePerUser;
import com.example.test_sel.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddMentoringActivity extends AppCompatActivity {
    private TextView TXT_code_course, TXT_course_name;
    private EditText EDT_course_grade, EDT_course_level;
    private Button BTN_add;
    private String course_code, course_grade, userName = "", course_name, course_level, userId;
    private boolean full = false, courseExist = true;
    private FirebaseUser user;
    private FirebaseAuth fAuth;
    private DatabaseReference refCourses, refUser;
    private ProgressBar ProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mentoring);

        TXT_code_course = findViewById(R.id.TXT_code_course);
        EDT_course_grade = findViewById(R.id.EDT_course_grade);
        TXT_course_name = findViewById(R.id.TXT_course_name);
        EDT_course_level = findViewById(R.id.EDT_course_level);

        BTN_add = findViewById(R.id.BTNadd);
        ProgressBar = findViewById(R.id.pbarReg);

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        userId = fAuth.getCurrentUser().getUid();

        //set text
        TXT_code_course.setText(getIntent().getExtras().getString("codeC"));
        TXT_course_name.setText(getIntent().getExtras().getString("nameC"));
        if (getIntent().getBooleanExtra("yesEdit", true)) {
            EDT_course_level.setText(getIntent().getExtras().getString("levelC"));
            EDT_course_grade.setText(getIntent().getExtras().getString("gradeC"));
        }

        refCourses = FirebaseDatabase.getInstance().getReference("Courses");
        refUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                course_grade = EDT_course_grade.getText().toString();
                course_code = TXT_code_course.getText().toString();
                course_name = TXT_course_name.getText().toString();
                course_level = EDT_course_level.getText().toString();


                //checks
                if (TextUtils.isEmpty(course_grade)) {
                    EDT_course_grade.setError("grade is required");
                    return;
                }
                if (Integer.parseInt(course_grade) < 85) {
                    Toast.makeText(AddMentoringActivity.this, "sorry, your grade is too low", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Integer.parseInt(course_grade) > 100) {
                    Toast.makeText(AddMentoringActivity.this, "sorry, your grade is incorrect", Toast.LENGTH_LONG).show();
                    return;
                }

                //TODO fullName dont work
                MyFireBase.getUser(new CallBack_UserReady() {
                    @Override
                    public void userReady(User user) {
                        userId = user.getUserId();
                        userName = user.getFullName();
                        String userPhone = user.getPhone();
                        String userImage = user.getImagePath();
                        String courseImage = "";


                        //ProgressBar after all the check
                        ProgressBar.setVisibility(View.VISIBLE);
                        //TODO  inseret the right name
                        CoursePerUser coursePerUser = new CoursePerUser(course_code, course_name,
                                course_level, courseImage, course_grade, userId, userName, userPhone,
                                userImage, CoursePerUser.SHOW_AS_COURSE);

                        CoursePerUser userPerCourse = new CoursePerUser(course_code, course_name,
                                course_level, courseImage, course_grade, userId, userName, userPhone,
                                userImage, CoursePerUser.SHOW_AS_USER);

                        //add pluse 1 to counter
                        MyFireBase.addToCounter(refCourses.child(course_code), "usersCounter", 1);
                        MyFireBase.addToCounter(refUser, "coursesCounter", 1);

                        //insert course-per-user to user
                        DatabaseReference courseInUser = refUser.child("courses").child(course_code);
                        courseInUser.setValue(coursePerUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddMentoringActivity.this, "Course Added.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddMentoringActivity.this, "data is not insert ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //insert course-per-user to course
                        DatabaseReference userListCourse = refCourses.child(course_code).child("usersList").child(userId);
                        userListCourse.setValue(userPerCourse).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AddMentoringActivity.this, "Course Added.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AddMentoringActivity.this, "data is not insert ", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                        startActivity(new Intent(getApplicationContext(), allMyCoursesActivity.class));
                        finish();
                    }

                    @Override
                    public void error() {

                    }
                });

            }


        });


    }

    public boolean isEDempty(EditText etText) {
        if (etText.getText().toString().trim().length() == 0) {
            etText.setError("Can not Save with Empty Field.");
            return true;
        }
        return false;
    }


}
