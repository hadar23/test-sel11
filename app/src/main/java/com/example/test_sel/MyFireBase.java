package com.example.test_sel;

import androidx.annotation.NonNull;

import com.example.test_sel.Callback.CallBack_ArrayReady;
import com.example.test_sel.Callback.CallBack_HashMapReady;
import com.example.test_sel.Callback.CallBack_IsExistReady;
import com.example.test_sel.Callback.CallBack_StringValueReady;

import com.example.test_sel.Callback.CallBack_UserReady;
import com.example.test_sel.Classes.Course;
import com.example.test_sel.Classes.User;
import com.example.test_sel.Classes.CoursePerUser;
import com.example.test_sel.Classes.Meeting;
import com.example.test_sel.Classes.Message;
import com.example.test_sel.Classes.RecurrentFreeHour;
import com.example.test_sel.nada.CallBack_getHashMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyFireBase {
    public static void getUser(final CallBack_UserReady callBack_userReady) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference myRef = database.getReference("Users").child(userId);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBack_userReady.error();

                User user = dataSnapshot.getValue(User.class);
                callBack_userReady.userReady(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_userReady.error();
            }
        });
    }

    public static void getStringValue(DatabaseReference myRef, final String key, final CallBack_StringValueReady callBack_StringValueReady) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBack_StringValueReady.error();

                String returnValue = dataSnapshot.child(key).getValue().toString();
                callBack_StringValueReady.stringValueReady(returnValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_StringValueReady.error();
            }
        });
    }

    public static void getHashValue(DatabaseReference myRef, final String key, final CallBack_getHashMap callBack_getHashMap) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBack_getHashMap.error();

                HashMap<String, String> returnValue = (HashMap<String, String>) dataSnapshot.child(key).getValue();
                callBack_getHashMap.getHashReady(returnValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_getHashMap.error();
            }
        });
    }

    public static void addToCounter(final DatabaseReference myRef, final String key, final int addValue) {
        getStringValue(myRef, key, new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                int valueInt = Integer.parseInt(value);
                valueInt += addValue;
                myRef.child(key).setValue(String.valueOf(valueInt));
            }

            @Override
            public void error() {

            }
        });
    }

    public static void checkValueExist(DatabaseReference myRef, final String key, final CallBack_IsExistReady callBack_isExistReady) {
        myRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    callBack_isExistReady.isExistReady(true);
                } else {
                    callBack_isExistReady.isExistReady(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_isExistReady.error();
            }
        });
    }

    public static void getUsers(final CallBack_ArrayReady<User> callBack_arrayReady) {
        final ArrayList<User> users2 = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        //ref inside database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBack_arrayReady.error();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    users2.add(user);
                }
                callBack_arrayReady.arrayReady(users2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_arrayReady.error();
            }
        });
    }

    public static void getCourses(final CallBack_ArrayReady<Course> callBack_arrayReady) {
        final ArrayList<Course> courses = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Courses");
        //ref inside database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBack_arrayReady.error();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Course course = ds.getValue(Course.class);
                    courses.add(course);
                }

                callBack_arrayReady.arrayReady(courses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_arrayReady.error();
            }
        });
    }


    public static void getCoursesPerUser(DatabaseReference myRef, final CallBack_ArrayReady<CoursePerUser> callBack_arrayReady) {
        final ArrayList<CoursePerUser> coursesPerUsers = new ArrayList<>();
        //ref inside database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBack_arrayReady.error();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    CoursePerUser course = ds.getValue(CoursePerUser.class);
                    coursesPerUsers.add(course);
                }

                callBack_arrayReady.arrayReady(coursesPerUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_arrayReady.error();
            }
        });
    }

//meeting
    public static void getRecurrentFreeHours(DatabaseReference myRef, final CallBack_HashMapReady<RecurrentFreeHour> callBack_hashMapReady) {
        final HashMap<String, RecurrentFreeHour> recurrentFreeHours = new HashMap<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBack_hashMapReady.error();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    RecurrentFreeHour recurrentFreeHour = ds.getValue(RecurrentFreeHour.class);
                    String thirdKey = ds.getKey();
                    recurrentFreeHours.put(thirdKey, recurrentFreeHour);
                }

                callBack_hashMapReady.hashMapReady(recurrentFreeHours);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_hashMapReady.error();
            }
        });
    }

    public static void getOneTimeFreeHours(DatabaseReference myRef, String year, String month, final CallBack_HashMapReady<OneTimeFreeHour> callBack_hashMapReady) {
        final HashMap<String, OneTimeFreeHour> oneTimeFreeHours = new HashMap<>();
        final String firstKey = year + "-" + month;

        myRef.child(firstKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    callBack_hashMapReady.error();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    OneTimeFreeHour oneTimeFreeHour = ds.getValue(OneTimeFreeHour.class);
                    String secondKey = ds.getKey();
                    oneTimeFreeHours.put(secondKey, oneTimeFreeHour);
                }
                callBack_hashMapReady.hashMapReady(oneTimeFreeHours);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_hashMapReady.error();
            }
        });
    }

    public static void getMeetings(DatabaseReference myRef, String year, String month, final CallBack_HashMapReady<Meeting> callBack_hashMapReady) {
        final HashMap<String, Meeting> meetings = new HashMap<>();
        final String firstKey = year + "-" + month;

        myRef.child(firstKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    callBack_hashMapReady.error();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Meeting meeting = ds.getValue(Meeting.class);
                    String secondKey = ds.getKey();
                    meetings.put(secondKey, meeting);
                }
                callBack_hashMapReady.hashMapReady(meetings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_hashMapReady.error();
            }
        });
    }

    public static void getMessages(DatabaseReference myRef, final CallBack_ArrayReady<Message> callBack_arrayReady) {
        final ArrayList<Message> messages = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    callBack_arrayReady.arrayReady(messages);
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Message message = ds.getValue(Message.class);
                    messages.add(message);
                }
                callBack_arrayReady.arrayReady(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack_arrayReady.error();
            }
        });
    }


}

