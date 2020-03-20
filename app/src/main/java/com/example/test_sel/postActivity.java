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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.test_sel.Callback.CallBack_ArrayReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.Course;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class postActivity extends AppCompatActivity {

    private RecyclerView courseRecyclerView;
    private PostAdapter postSearchAdapter;
    private ImageView image_mentoring;
    ArrayList<Course> myCourses;
    Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        image_mentoring = findViewById(R.id.image_mentoring);



        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");

        if (getIntent().getStringExtra("addCourse")!=null) {
            image_mentoring.setVisibility(View.VISIBLE);
//            BTNsign_up.setText("UPDATE");
//            setMyData();
//            userId = getIntent().getExtras().getString("userid");
        }


        MyFireBase.getCourses(new CallBack_ArrayReady<Course>() {
            @Override
            public void arrayReady(ArrayList<Course> courses) {
                myCourses = courses;
                buildCourseList(courses);
            }

            @Override
            public void error() {
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
        ArrayList<Course> courseArrRec = new ArrayList<>();
        for (Course u : myCourses) {
            if (u.getCourseName().toLowerCase().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
            if (u.getCourseCode().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
        }

        buildCourseList(courseArrRec);

    }

    private void buildCourseList(ArrayList<Course> courses) {
        ArrayList<CardInfo> cards = new ArrayList<>(courses.size());
        for (CardInfo cardInfo : courses) {
            cards.add(cardInfo != null ? cardInfo : null);
        }
        courseRecyclerView = findViewById(R.id.RCL_courseList);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().getExtras() != null) {
            postSearchAdapter = new PostAdapter(this, cards,getIntent());
        } else {
            postSearchAdapter = new PostAdapter(this, cards);
        }
        postSearchAdapter.setClickListener(myItemClickListener);
        courseRecyclerView.setAdapter(postSearchAdapter);
    }

    private PostAdapter.ItemClickListener myItemClickListener = new PostAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };

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


