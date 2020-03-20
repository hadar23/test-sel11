package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.test_sel.Callback.CallBack_ArrayReady;
import com.example.test_sel.Callback.CallBack_StringValueReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.CoursePerUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class allMyCoursesActivity extends AppCompatActivity {
    private RecyclerView courseRecyclerView;
    private PostAdapter postAdapter;
    private ImageView image_AddMentor;
    private FirebaseAuth fAuth;
    DatabaseReference myRef;
    Boolean isvisit;
    private Toolbar toolBar;
    Intent visit;

    private String currentUserId = "", username, visitorUserId = "", userId = "";

    ArrayList<CoursePerUser> myCoursePerUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_my_courses);

        fAuth = FirebaseAuth.getInstance();
        currentUserId = fAuth.getCurrentUser().getUid();

        image_AddMentor = findViewById(R.id.image_AddMentor);

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");

        if (getIntent().getStringExtra("visiitUserId") != null) {
            userId = getIntent().getExtras().getString("visiitUserId");
            isvisit = true;
        } else {
            userId = fAuth.getCurrentUser().getUid();
        }
        //get user name
        MyFireBase.getStringValue(FirebaseDatabase.getInstance().getReference().child("Users").child(userId), "fullName", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                username = value;
            }

            @Override
            public void error() {

            }
        });

        MyFireBase.getStringValue(FirebaseDatabase.getInstance().getReference().child("Users").child(userId), "userId", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                visitorUserId = value;
            }

            @Override
            public void error() {

            }
        });
        //check if the user is a visitor

        if (!((currentUserId.trim()).equals(userId.trim()))) {
            visit = new Intent();
            visit.putExtra("yes", "yes");
            Log.d("userrr", visitorUserId + "          " + userId);

            image_AddMentor.setVisibility(View.GONE);
        }

        //get CoursesPerUser list to show in recycle
        myRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("courses");
        MyFireBase.getCoursesPerUser(myRef, new CallBack_ArrayReady<CoursePerUser>() {
            @Override
            public void arrayReady(ArrayList<CoursePerUser> coursePerUsers) {
                allMyCoursesActivity.this.myCoursePerUsers = coursePerUsers;
                buildCourseList(allMyCoursesActivity.this.myCoursePerUsers);
                Log.d("user", allMyCoursesActivity.this.myCoursePerUsers.toString());
            }

            @Override
            public void error() {

            }
        });

        image_AddMentor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), postActivity.class);
                i.putExtra("kind", "addCourse");
                i.putExtra("username", username);
                i.putExtra("userId", userId);

                startActivity(i);

            }
        });
//search part
        EditText editText = findViewById(R.id.edt_search);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());

            }
        });
    }

    private void filter(String text) {
        ArrayList<CoursePerUser> courseArrRec = new ArrayList<>();
        for (CoursePerUser u : myCoursePerUsers) {
            if (u.getCourseName().toLowerCase().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
            if (u.getCourseCode().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
        }

        buildCourseList(courseArrRec);

    }

    private void buildCourseList(ArrayList<CoursePerUser> courses) {
        ArrayList<CardInfo> cards = new ArrayList<>(courses.size());
        for (CardInfo cardInfo : courses) {
            cards.add(cardInfo != null ? cardInfo : null);
        }
        courseRecyclerView = findViewById(R.id.RCL_courseList);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (visit != null) {
            postAdapter = new PostAdapter(this, cards, visit);
        } else {
            postAdapter = new PostAdapter(this, cards);
        }
        postAdapter.setClickListener(myItemClickListener);
        courseRecyclerView.setAdapter(postAdapter);
    }

    private PostAdapter.ItemClickListener myItemClickListener = new PostAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();


    }

    //----------------START top toolbar------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inFlater = getMenuInflater();
        inFlater.inflate(R.menu.side_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //
        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
        if (item.getItemId() == R.id.search) {
            startActivity(new Intent(getApplicationContext(), frag_activity.class));
            finish();
        }

        if (item.getItemId() == R.id.home) {
            startActivity(new Intent(getApplicationContext(), homePageActivity.class));
            finish();
        }

        return true;
    }
}
//----------------end top toolbar------------------------------------------

