package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class frag_activity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private Toolbar topToolBar;
    private ImageView image_mentoring;
    private Intent intent;
    DatabaseReference myRef;
    Fragment fragment = null;
    private Toolbar toolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_activity);

        topToolBar = findViewById(R.id.toolbar);
        image_mentoring = findViewById(R.id.image_mentoring);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setTitle("Search");

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");
//if user click on serach by course ' and then click on the course card
        //list of user that to mentor to the spese course

        if (getIntent().getStringExtra("key") != null) {
            if (getIntent().getStringExtra("type").equals("comeFromCourseFreg")) {
                myRef = FirebaseDatabase.getInstance().getReference("Courses").child(getIntent().getExtras().getString("key"));
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.hasChild("usersList")) {

                            image_mentoring.setImageResource(R.drawable.img_mentoring);
                            fragment = new fragment_coursesPerUser();
                            Bundle args = new Bundle();
                            args.putString("comeFromCourseFreg", getIntent().getExtras().getString("key"));
                            fragment.setArguments(args);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, fragment).commit();
                        } else {

                            Log.d("dag", "nooo");
                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            ///
            if (getIntent().getStringExtra("type").equals("comeFromUserFreg")) {
                myRef = FirebaseDatabase.getInstance().getReference("Users").child(getIntent().getExtras().getString("key")).child("courses");
                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            image_mentoring.setImageResource(R.drawable.img_mentoring);
                            fragment = new fragment_coursesPerUser();
                            Bundle args = new Bundle();
                            args.putString("comeFromUserFreg", getIntent().getExtras().getString("key"));
                            fragment.setArguments(args);
                            getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, fragment).commit();
//

                        } else {

                            Log.d("dag", "nooo");
                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

        }


        bottomNavigationView = findViewById(R.id.bottomBar);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, new fragment_users()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.page_courses:

                        image_mentoring.setImageResource(R.drawable.img_search_by_course);
                        fragment = new fragment_courses();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("type", "userList");
//                        fragment.setArguments(bundle);
                        break;
                    case R.id.page_users:

                        fragment = new fragment_users();
                        image_mentoring.setImageResource(R.drawable.img_search_by_user);
                        break;


                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fregCenter, fragment).commit();
                return true;
            }
        });
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