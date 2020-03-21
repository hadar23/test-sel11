package com.example.test_sel;

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
import com.example.test_sel.Classes.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class fragment_users extends Fragment {
    private RecyclerView userRecyclerView;
    private PostSearchAdapter postSearchAdapter;
    private EditText editText;
    ArrayList<User> myUsers=new ArrayList<>();
    ArrayList<CoursePerUser> myUsersCourse,tempList;
    DatabaseReference myRef;
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

//        MyFireBase.getUsers(new CallBack_UsersReady() {
//            @Override
//            public void usersReady(ArrayList<User> users) {
//                myUsers = users;
//                buildUsersList(users, inflater);
//            }
//
//            @Override
//            public void error() {
//            }
//        });


        MyFireBase.getUsers(new CallBack_ArrayReady<User>() {
            @Override
            public void arrayReady(ArrayList<User> users) {
                for (User myUser1 : users) {
                    if (Integer.parseInt(myUser1.getCoursesCounter()) > 0) {
                        myUsers.add(myUser1);
                        Log.d("ga",myUser1.getCoursesCounter());
                        buildUsersList(myUsers, inflater);
                       // myUsers.add(myUser1);
//                        myRef = FirebaseDatabase.getInstance().getReference("Users").child(myUser1.getUserId()).child("courses");
//                        MyFireBase.getCoursesPerUser(myRef, new CallBack_ArrayReady<CoursePerUser>() {
//                            @Override
//                            public void arrayReady(ArrayList<CoursePerUser> courses) {
////                                myUser.set
////                                tempList=courses;
////                                myUsersCourse.addAll() = courses;
//                                myUsersCourse=courses;
//                                buildUsersList(myUsersCourse, inflater);
//                            }
//
//                            @Override
//                            public void error() {
//                            }
//                        });
                    }
                }
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
        ArrayList<User> userArrRec = new ArrayList<>();
        for (User u : myUsers) {
            if (u.getFullName().toLowerCase().contains(text.toLowerCase())) {
                userArrRec.add(u);
            }
        }

        buildUsersList(userArrRec, inflater);

    }

    private void buildUsersList(ArrayList<User> users, LayoutInflater inflater) {
        ArrayList<CardInfo> cards = new ArrayList<>(users.size());
        for (CardInfo cardInfo : users) {
//            if ()cards)   if (Integer.parseInt(cardInfo.getCoursesCounter())> 0) {
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