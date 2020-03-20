package com.example.test_sel;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.widget.Toolbar;

import com.example.test_sel.Callback.CallBack_ArrayReady;
import com.example.test_sel.Classes.CardInfo;
import com.example.test_sel.Classes.Course;

import java.util.ArrayList;


public class fragment_courses extends Fragment {

    private RecyclerView courseRecyclerView;
    private PostSearchAdapter postSearchAdapter;
    private ImageView image_mentoring;
    ArrayList<Course> myCourses;
    EditText editText;
    private View view;
    Toolbar toolBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_post, container, false);
        }

        image_mentoring = view.findViewById(R.id.image_mentoring);
        image_mentoring.setVisibility(View.GONE);
        //TOP TOLL BAR
        toolBar = view.findViewById(R.id.toolbar);
        toolBar.setVisibility(View.GONE);


//        if (getIntent().getStringExtra("kind")!=null) {
//            image_mentoring.setImageResource(R.drawable.img_offer_mentor);
////            BTNsign_up.setText("UPDATE");
////            setMyData();
////            userId = getIntent().getExtras().getString("userid");
//        }


        MyFireBase.getCourses(new CallBack_ArrayReady<Course>() {
            @Override
            public void arrayReady(ArrayList<Course> courses) {
                myCourses = courses;
                buildCourseList(courses, inflater);
            }

            @Override
            public void error() {
            }
        });
//search part
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
        ArrayList<Course> courseArrRec = new ArrayList<>();
        for (Course u : myCourses) {
            if (u.getCourseName().toLowerCase().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
            if (u.getCourseCode().contains(text.toLowerCase())) {
                courseArrRec.add(u);
            }
        }
        buildCourseList(courseArrRec, inflater);


    }

    private void buildCourseList(ArrayList<Course> courses, LayoutInflater inflater) {
        ArrayList<CardInfo> cards = new ArrayList<>(courses.size());
        for (CardInfo cardInfo : courses) {
            cards.add(cardInfo != null ? cardInfo : null);
        }
        courseRecyclerView = view.findViewById(R.id.RCL_courseList);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
//        if (getIntent().getExtras() != null) {
//            postSearchAdapter = new PostSearchAdapter(this, cards,getIntent());
//        } else {
        postSearchAdapter = new PostSearchAdapter(inflater.getContext(), cards);
//        }
        postSearchAdapter.setClickListener(myItemClickListener);
        courseRecyclerView.setAdapter(postSearchAdapter);
    }

    private PostSearchAdapter.ItemClickListener myItemClickListener = new PostSearchAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
        }
    };

}
