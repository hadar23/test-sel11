package com.example.test_sel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;

import com.google.firebase.auth.FirebaseAuth;

public class homePageActivity extends AppCompatActivity {
    GridLayout mainGrid;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("Home");

        mainGrid = findViewById(R.id.mainGrid);
        mainGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
//set EVENT
        setSingleEvent(mainGrid);

    }
//
//    private void setTaggleEvent(GridLayout mainGrid) {
//        for (int i = 0; i < mainGrid.getChildCount(); i++) {
//            final CardView c = (CardView) mainGrid.getChildAt(i);
//            c.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (c.getCardBackgroundColor().getDefaultColor() == -1) {
//
////change color
//                        c.setCardBackgroundColor(Color.parseColor("#123456"));
//                    } else {
//
////change color
//                        c.setCardBackgroundColor(Color.parseColor("#123456"));
//
//
//                    }
//                });
//            }
//        }
//    }

    private void setSingleEvent(GridLayout mainGrid) {

        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            Log.d("taga",mainGrid.getChildCount()+"");
            CardView c = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:
                            startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
                            break;
                        case 1:
                            startActivity(new Intent(getApplicationContext(),postActivity.class));
                            break;
                        case 2:
                            //message
                            startActivity(new Intent(getApplicationContext(), MessageActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(getApplicationContext(), frag_activity.class));
                            break;
                        case 4:
                            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                            break;
                        case 5:
                            startActivity(new Intent(getApplicationContext(), allMyCoursesActivity.class));
                            break;
                    }
                }
            });
        }
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



