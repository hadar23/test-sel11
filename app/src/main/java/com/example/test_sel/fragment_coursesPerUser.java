package com.example.test_sel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test_sel.Callback.CallBack_ArrayReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.CoursePerUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class fragment_coursesPerUser extends Fragment {
    private RecyclerView userRecyclerView;
    private PostSearchAdapter postSearchAdapter;
    private EditText editText;
    ArrayList<CoursePerUser> myUsersPerCourses;
    DatabaseReference myRef;

    String keycourse = "",keyuser="";
    //    ArrayList<CardInfo> cards = new ArrayList<>(myUsers.size());
    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_user_post, container, false);
        }

        //get from intente yhet coder key
        Bundle args = getArguments();
        if (args != null) {

            keycourse = args.getString("comeFromCourseFreg", "");
            keyuser = args.getString("comeFromUserFreg", "");

        }
        if (!keycourse.equals(""))
        myRef = FirebaseDatabase.getInstance().getReference("Courses").child(keycourse).child("usersList");


        if (!keyuser.equals(""))
            myRef = FirebaseDatabase.getInstance().getReference("Users").child(keyuser).child("courses");

        //add list of all user that do mentor to the course
        MyFireBase.getCoursesPerUser(myRef, new CallBack_ArrayReady<CoursePerUser>() {
            @Override
            public void arrayReady(ArrayList<CoursePerUser> coursePerUsers) {
                myUsersPerCourses = coursePerUsers;
                buildUsersList(coursePerUsers, inflater);
            }
            @Override
            public void error() {
            }
        });


        editText = view.findViewById(R.id.edt_search);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString(), inflater);

            }
        });

        return view;
    }

    private void filter(String text, LayoutInflater inflater) {
        ArrayList<CoursePerUser> userArrRec = new ArrayList<>();
        for (CoursePerUser u : myUsersPerCourses) {
            if (u.getUserName().toLowerCase().contains(text.toLowerCase())) {
                userArrRec.add(u);
            }
        }

        buildUsersList(userArrRec, inflater);

    }

    private void buildUsersList(ArrayList<CoursePerUser> users, LayoutInflater inflater) {
        ArrayList<CardInfo> cards = new ArrayList<>(users.size());
        for (CardInfo cardInfo : users) {
            cards.add(cardInfo != null ? cardInfo : null);
        }
        userRecyclerView = view.findViewById(R.id.RCL_courseList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        postSearchAdapter = new PostSearchAdapter(inflater.getContext(), cards);
        postSearchAdapter.setClickListener(myItemClickListener);
        userRecyclerView.setAdapter(postSearchAdapter);
    }


    private PostSearchAdapter.ItemClickListener myItemClickListener = new PostSearchAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };
}
