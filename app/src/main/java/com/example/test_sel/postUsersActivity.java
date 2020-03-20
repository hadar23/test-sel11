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

import com.example.test_sel.Callback.CallBack_ArrayReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class postUsersActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private PostSearchAdapter postSearchAdapter;
    private EditText editText;
    ArrayList<User> myUsers;
    private Toolbar toolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_post);

        //TOP TOLL BAR
        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        getSupportActionBar().setTitle("");

        MyFireBase.getUsers(new CallBack_ArrayReady<User>() {
            @Override
            public void arrayReady(ArrayList<User> users) {
                myUsers=users;
                buildUsersList(users);
            }

            @Override
            public void error() {
            }
        });

        EditText editText=findViewById(R.id.edt_search);

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
        ArrayList<User> userArrRec=new ArrayList<>();
        for (User u:myUsers){
            if(u.getFullName().toLowerCase().contains(text.toLowerCase())){
                userArrRec.add(u);
            }
        }

        buildUsersList(userArrRec);

    }

    private void buildUsersList(ArrayList<User> users) {
        ArrayList<CardInfo> cards = new ArrayList<>(users.size());
        for (CardInfo cardInfo : users) {
            cards.add(cardInfo != null ? cardInfo : null);
        }
        userRecyclerView = findViewById(R.id.RCL_courseList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        postSearchAdapter = new PostSearchAdapter(this, cards);
        postSearchAdapter.setClickListener(myItemClickListener);
        userRecyclerView.setAdapter(postSearchAdapter);
    }

    private PostSearchAdapter.ItemClickListener myItemClickListener = new PostSearchAdapter.ItemClickListener() {
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


