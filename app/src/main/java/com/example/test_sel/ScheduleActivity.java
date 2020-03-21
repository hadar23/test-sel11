package com.example.test_sel;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.test_sel.Callback.CallBack_HashMapReady;
import com.example.test_sel.Callback.CallBack_StringValueReady;
import com.example.test_sel.Classes.Meeting;
import com.example.test_sel.Classes.Message;
import com.example.test_sel.Classes.RecurrentFreeHour;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private DatabaseReference refFreeHoursUser;
    private DatabaseReference refMeetingsUser;

    private WeekView mWeekView;
    private Calendar calendar;
    private Context context = this;
    private String hostUserId;
    private String hostUserName;
    private String guestUserId = "";
    private String guestUserName = "";
    private String courseCode = "";
    private String courseName = "";
    private boolean isHost = false;

    private HashMap<String, RecurrentFreeHour> recurrentFreeHours = new HashMap<>();
    private HashMap<String, HashMap<String, OneTimeFreeHour>> oneTimeFreeHours = new HashMap<>();
    private HashMap<String, HashMap<String, Meeting>> meetings = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        isHost = getIntent().getExtras().getBoolean("isHost");

        fAuth = FirebaseAuth.getInstance();
        if(isHost) {
            hostUserId = fAuth.getCurrentUser().getUid();
        } else{
            guestUserId = fAuth.getCurrentUser().getUid();
        }

        refFreeHoursUser = FirebaseDatabase.getInstance().getReference().child("Users").child(hostUserId);
        if(isHost) {
            refMeetingsUser = FirebaseDatabase.getInstance().getReference().child("Users").child(hostUserId);
        }else{
            refMeetingsUser = FirebaseDatabase.getInstance().getReference().child("Users").child(guestUserId);

        }

        MyFireBase.getStringValue(refFreeHoursUser, "fullName", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                hostUserName = value;
            }

            @Override
            public void error() {

            }
        });
        MyFireBase.getStringValue(refMeetingsUser, "fullName", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                guestUserName = value;
            }

            @Override
            public void error() {

            }
        });


        mWeekView = (WeekView) findViewById(R.id.weekView);

        mWeekView.setHorizontalFlingEnabled(false);
        mWeekView.setVerticalFlingEnabled(false);
        mWeekView.setXScrollingSpeed(1);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        mWeekView.goToDate(calendar);
        mWeekView.goToHour(8);
//        mWeekView.setShowDistinctPastFutureColor(true);

        if (isHost) {
            mWeekView.setEmptyViewClickListener(new WeekView.EmptyViewClickListener() {
                @Override
                public void onEmptyViewClicked(Calendar time) {
                    showFreeTimeDialog(time);
                }

            });
        }

        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                showEventDialog(event);
            }
        });


        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        readMeetingsFromFireBase(year, month);
        readOneTimeFreeHoursFromFireBase(year, month);
        readRecurringFreeHoursFromFireBase();

        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                String firstKeyUp = String.valueOf(newYear) + "-" + String.valueOf(newMonth + 1);
                String firstKeyDown = String.valueOf(newYear) + "-" + String.valueOf(newMonth - 1);
                if (!oneTimeFreeHours.containsKey(firstKeyUp)) {
                    readOneTimeFreeHoursFromFireBase(String.valueOf(newYear), String.valueOf(newMonth + 1));
                }
                if (!oneTimeFreeHours.containsKey(firstKeyDown)) {
                    readOneTimeFreeHoursFromFireBase(String.valueOf(newYear), String.valueOf(newMonth - 1));
                }
                if (!meetings.containsKey(firstKeyUp)) {
                    readMeetingsFromFireBase(String.valueOf(newYear), String.valueOf(newMonth + 1));
                }
                if (!meetings.containsKey(firstKeyDown)) {
                    readMeetingsFromFireBase(String.valueOf(newYear), String.valueOf(newMonth - 1));
                }
                ArrayList<WeekViewEvent> AllEvents = returnRecurrentEventsForMonth(newMonth, newYear);
                AllEvents.addAll(returnOneTimeEventsForMonth(newMonth, newYear));
                AllEvents.addAll(returnMeetingsEventsForMonth(newMonth, newYear));
                return AllEvents;
            }
        });


        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            mWeekView.setNumberOfVisibleDays(5);
        } else {
            // In portrait
            Toast.makeText(this, "To show 5 days in a week, change to Landscape mode", Toast.LENGTH_SHORT).show();
            mWeekView.setNumberOfVisibleDays(3);
        }

        mWeekView.notifyDatasetChanged();

    }

    private String getEventTitle(Calendar time) {
        return String.format(Locale.ENGLISH, "Event of %02d:%02d %d/%s", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH) + 1);
    }

    public void showEventDialog(final WeekViewEvent event) {
        //TODO dup
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final boolean isRecurrent = event.getColor() == ContextCompat.getColor(context, R.color.recurrentFreeHoursGreen);
        final boolean isOneTime = event.getColor() == ContextCompat.getColor(context, R.color.oneTimeFreeHoursGreen);
        final boolean isMeeting = event.getColor() == ContextCompat.getColor(context, R.color.meetingRed);

        String[] option = isRecurrent && isHost ? new String[2] : new String[1];
//        String[] option = recurrent ? new String[2] : new String[1];
        Calendar time = event.getStartTime();
        final String year = String.valueOf(time.get(Calendar.YEAR));
        final String month = String.valueOf(time.get(Calendar.MONTH) + 1);
        String day = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
        String dayInAWeek = String.valueOf(time.get(Calendar.DAY_OF_WEEK));
        String hour = String.valueOf(time.get(Calendar.HOUR_OF_DAY));


        final String firstKey = year + "-" + month;
        final String secondKey = day + "-" + hour;
        final String thirdKey = dayInAWeek + "-" + hour;

        if (isHost) {
            if (isRecurrent) {
                option[0] = "Remove for this day";
                option[1] = "Remove for every day";
                addMytitle(builder, "This is a recurring event\nDo you want to remove it?");
            } else if (isOneTime) {
                option[0] = "Remove";
                addMytitle(builder, "This is a one-time event\nDo you want to remove it?");
            } else {
                option[0] = "Cancel";
                addMytitle(builder, "This is a meeting\nDo you want to cancel it?");
            }
        } else {
            if (isMeeting) {
                option[0] = "Cancel";
                addMytitle(builder, "This is a meeting\nDo you want to cancel it?");
            } else {
                option[0] = "Schedule meeting";
                addMytitle(builder, "Do you want to schedule a meeting?");
            }
        }

        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isHost) {
                    if (isRecurrent) {
                        if (which == 0) {
                            Toast.makeText(context, "This day ", Toast.LENGTH_SHORT).show();
                            refFreeHoursUser.child("recurrentFreeHours").child(thirdKey).child("deleted").child(firstKey + "-" + secondKey).setValue(firstKey + "-" + secondKey);
                        } else if (which == 1) {
                            Toast.makeText(context, "Every day ", Toast.LENGTH_SHORT).show();
                            refFreeHoursUser.child("recurrentFreeHours").child(thirdKey).removeValue();
                        }
                        readRecurringFreeHoursFromFireBase();
                    } else if (isOneTime) {
                        if (which == 0) {
                            Toast.makeText(context, "Remove ", Toast.LENGTH_SHORT).show();
                            refFreeHoursUser.child("oneTimeFreeHours").child(firstKey).child(secondKey).removeValue();
                        }
                        readOneTimeFreeHoursFromFireBase(year, month);
                        readRecurringFreeHoursFromFireBase();
                    } else {
                        if (which == 0) {
                            Toast.makeText(context, "Cancel ", Toast.LENGTH_SHORT).show();
                            removeMeeting(event);
                        }
                        readMeetingsFromFireBase(year, month);
                        readOneTimeFreeHoursFromFireBase(year, month);
                        readRecurringFreeHoursFromFireBase();
                    }
                } else {
                    if (isMeeting) {
                        if (which == 0) {
                            Toast.makeText(context, "Cancel ", Toast.LENGTH_SHORT).show();
                            removeMeeting(event);
                        }
                        readMeetingsFromFireBase(year, month);
                        readOneTimeFreeHoursFromFireBase(year, month);
                        readRecurringFreeHoursFromFireBase();
                    } else {
                        if (which == 0) {
                            Toast.makeText(context, "Meeting ", Toast.LENGTH_SHORT).show();
                            createMeeting(event);
                        }
                        readMeetingsFromFireBase(year, month);
                        readRecurringFreeHoursFromFireBase();
                    }
                }
            }
        });
        builder.create().show();
    }

    public void showFreeTimeDialog(final Calendar time) {
        //option to show in dialog
        String[] option = {"One-Time", "Regular"};

        //allert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //set title
        addMytitle(builder, "Pick type of free hour");

        // set item to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String year = String.valueOf(time.get(Calendar.YEAR));
                String month = String.valueOf(time.get(Calendar.MONTH) + 1);
                String day = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
                String dayInAWeek = String.valueOf(time.get(Calendar.DAY_OF_WEEK));
                String hour = String.valueOf(time.get(Calendar.HOUR_OF_DAY));

                String firstKey = year + "-" + month;
                String secondKey = day + "-" + hour;
                String thirdKey = dayInAWeek + "-" + hour;


                //user choose one-time
                if (which == 0) {
                    Toast.makeText(context, "One-Time Event ", Toast.LENGTH_SHORT).show();
                    OneTimeFreeHour oneTimeFreeHour = new OneTimeFreeHour(hostUserId, hostUserName, year, month, day, hour);
                    refFreeHoursUser.child("oneTimeFreeHours").child(firstKey).child(secondKey).setValue(oneTimeFreeHour);
                    oneTimeFreeHours.get(firstKey).put(secondKey, oneTimeFreeHour);
                }

                //user choose regular
                else if (which == 1) {
                    Toast.makeText(context, "Regular Event ", Toast.LENGTH_SHORT).show();
                    final RecurrentFreeHour recurrentFreeHour = new RecurrentFreeHour(hostUserId, hostUserName, dayInAWeek, hour);
                    refFreeHoursUser.child("recurrentFreeHours").child(thirdKey).setValue(recurrentFreeHour);
                    recurrentFreeHours.put(thirdKey, recurrentFreeHour);
                }

                mWeekView.notifyDatasetChanged();
            }
        });
        builder.create().show();
    }

    private void readRecurringFreeHoursFromFireBase() {
        MyFireBase.getRecurrentFreeHours(refFreeHoursUser.child("recurrentFreeHours"), new CallBack_HashMapReady<RecurrentFreeHour>() {
            @Override
            public void hashMapReady(HashMap<String, RecurrentFreeHour> hashMap) {
                recurrentFreeHours = hashMap;
                mWeekView.notifyDatasetChanged();
            }

            @Override
            public void error() {

            }
        });
    }

    private void readOneTimeFreeHoursFromFireBase(String year, String month) {
        final String firstKey = year + "-" + month;
        MyFireBase.getOneTimeFreeHours(refFreeHoursUser.child("oneTimeFreeHours"), year, month, new CallBack_HashMapReady<OneTimeFreeHour>() {
            @Override
            public void hashMapReady(HashMap<String, OneTimeFreeHour> hashMap) {
                oneTimeFreeHours.put(firstKey, hashMap);
            }

            @Override
            public void error() {

            }
        });
    }

    private void addMytitle(AlertDialog.Builder builder, String s) {
        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText(s);
        //TODO
        //  title.setBackgroundColor(R.color.colorheadDialog);
        title.setBackgroundColor(Color.MAGENTA);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        builder.setCustomTitle(title);
    }

    private ArrayList<WeekViewEvent> returnRecurrentEventsForMonth(int month, int year) {
        ArrayList<WeekViewEvent> events = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.setMinimalDaysInFirstWeek(1);

        int nubmerOfWeeksInMonth = cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
        for (int w = 1; w <= nubmerOfWeeksInMonth; w++) {
            for (RecurrentFreeHour recurrentFreeHour : recurrentFreeHours.values()) {
                int dayInAWeek = Integer.parseInt(recurrentFreeHour.getDayInAWeek());
                int hour = Integer.parseInt(recurrentFreeHour.getHour());

                Calendar startTime = Calendar.getInstance();
                startTime.clear();
                startTime.setMinimalDaysInFirstWeek(1);
                startTime.set(Calendar.YEAR, year);
                startTime.set(Calendar.MONTH, month - 1);
                startTime.set(Calendar.DAY_OF_WEEK, dayInAWeek);
                startTime.set(Calendar.WEEK_OF_MONTH, w);
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, 0);

                if (startTime.get(Calendar.MONTH) != month - 1) {
                    continue;
                }

                String firstKey = year + "-" + month;
                String secondKey = startTime.get(Calendar.DAY_OF_MONTH) + "-" + hour;

                if (recurrentFreeHour.getDeleted().containsKey(firstKey + "-" + secondKey)) {
                    continue;
                }

                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, 1);
                WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
                event.setColor(ContextCompat.getColor(context, R.color.recurrentFreeHoursGreen));
                events.add(event);
            }
        }
        return events;
    }

    private ArrayList<WeekViewEvent> returnOneTimeEventsForMonth(int newMonth, int newYear) {
        ArrayList<WeekViewEvent> events = new ArrayList<>();
        String firstKey = String.valueOf(newYear) + "-" + String.valueOf(newMonth);

        if (!oneTimeFreeHours.containsKey(firstKey)) {
            return events;
        }

        for (OneTimeFreeHour oneTimeFreeHour : oneTimeFreeHours.get(firstKey).values()) {
            int year = Integer.parseInt(oneTimeFreeHour.getYear());
            int month = Integer.parseInt(oneTimeFreeHour.getMonth());
            if (newYear != year || newMonth != month) {
                return events;
            }
            int hour = Integer.parseInt(oneTimeFreeHour.getHour());
            int day = Integer.parseInt((oneTimeFreeHour.getDay()));
            Calendar startTime = Calendar.getInstance();
            startTime.clear();
            startTime.set(Calendar.YEAR, year);
            startTime.set(Calendar.MONTH, month - 1);
            startTime.set(Calendar.DAY_OF_MONTH, day);
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, 0);

            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 1);
            WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
            event.setColor(ContextCompat.getColor(context, R.color.oneTimeFreeHoursGreen));
            events.add(event);
        }

        return events;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void createMeeting(WeekViewEvent event) {
        //TODO dup
        boolean isRecurrent = event.getColor() == ContextCompat.getColor(context, R.color.recurrentFreeHoursGreen);
        String typeOfEventBeforeMeeting = isRecurrent ? Meeting.RECURRENT_FREE_HOUR : Meeting.ONE_TIME_FREE_HOUR;
        Calendar time = event.getStartTime();
        final String year = String.valueOf(time.get(Calendar.YEAR));
        final String month = String.valueOf(time.get(Calendar.MONTH) + 1);
        final String day = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
        String dayInAWeek = String.valueOf(time.get(Calendar.DAY_OF_WEEK));
        final String hour = String.valueOf(time.get(Calendar.HOUR_OF_DAY));

        Meeting meeting = new Meeting(hostUserId, hostUserName, guestUserId, guestUserName, year, month, day, hour, courseCode, courseName, typeOfEventBeforeMeeting);

        final String firstKey = year + "-" + month;
        final String secondKey = day + "-" + hour;
        final String thirdKey = dayInAWeek + "-" + hour;

        if (isRecurrent) {
            RecurrentFreeHour recurrentFreeHour = recurrentFreeHours.get(thirdKey);
            recurrentFreeHour.addToDeleted(firstKey + "-" + secondKey);
            refFreeHoursUser.child("recurrentFreeHours").child(thirdKey).setValue(recurrentFreeHour);
        } else {
            oneTimeFreeHours.get(firstKey).remove(secondKey);
            refFreeHoursUser.child("oneTimeFreeHours").child(firstKey).child(secondKey).removeValue();
        }

        refFreeHoursUser.child("meetings").child(firstKey).child(secondKey).setValue(meeting);
        if (!isHost) {
            refMeetingsUser.child("meetings").child(firstKey).child(secondKey).setValue(meeting);
        }

        meetings.get(firstKey).put(secondKey, meeting);
        final String messageTimeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());


        MyFireBase.getStringValue(refMeetingsUser, "imagePath", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                Message messageForHost = new Message(hostUserId, hostUserName, guestUserId, guestUserName, year, month, day, hour, messageTimeInMillis, courseCode, courseName, Message.SCHEDULE_MESSAGE, Message.GUEST, Message.HOST, value);
                refFreeHoursUser.child("messages").child(messageTimeInMillis).setValue(messageForHost);
            }

            @Override
            public void error() {

            }
        });

        MyFireBase.getStringValue(refFreeHoursUser, "imagePath", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                Message messageForGuest = new Message(hostUserId, hostUserName, guestUserId, guestUserName, year, month, day, hour, messageTimeInMillis, courseCode, courseName, Message.SCHEDULE_MESSAGE, Message.GUEST, Message.GUEST, value);
                refMeetingsUser.child("messages").child(messageTimeInMillis).setValue(messageForGuest);
            }

            @Override
            public void error() {

            }
        });



    }

    private void removeMeeting(WeekViewEvent event) {
        //TODO dup
        Calendar time = event.getStartTime();
        final String year = String.valueOf(time.get(Calendar.YEAR));
        final String month = String.valueOf(time.get(Calendar.MONTH) + 1);
        final String day = String.valueOf(time.get(Calendar.DAY_OF_MONTH));
        String dayInAWeek = String.valueOf(time.get(Calendar.DAY_OF_WEEK));
        final String hour = String.valueOf(time.get(Calendar.HOUR_OF_DAY));

        final String firstKey = year + "-" + month;
        final String secondKey = day + "-" + hour;
        final String thirdKey = dayInAWeek + "-" + hour;

        Meeting meeting = meetings.get(firstKey).get(secondKey);
        String typeOfEventBeforeMeeting = meeting.getTypeOfEventBeforeMeeting();
        if (typeOfEventBeforeMeeting.equals(Meeting.RECURRENT_FREE_HOUR)) {
            if (recurrentFreeHours.containsKey(thirdKey)) {
                refFreeHoursUser.child("recurrentFreeHours").child(thirdKey).child("deleted").child(firstKey + "-" + secondKey).removeValue();
            }
        } else {
            OneTimeFreeHour oneTimeFreeHour = new OneTimeFreeHour(hostUserId, hostUserName, year, month, day, hour);
            refFreeHoursUser.child("oneTimeFreeHours").child(firstKey).child(secondKey).setValue(oneTimeFreeHour);
        }

        refFreeHoursUser.child("meetings").child(firstKey).child(secondKey).removeValue();
        if (!isHost) {
            refMeetingsUser.child("meetings").child(firstKey).child(secondKey).removeValue();
        }

        meetings.get(firstKey).remove(secondKey);

        final String messageTimeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());
        final String initiator;
        if (isHost) {
            initiator = Message.HOST;
        } else {
            initiator = Message.GUEST;
        }

        MyFireBase.getStringValue(refMeetingsUser, "imagePath", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                Message messageForHost = new Message(hostUserId, hostUserName, guestUserId, guestUserName, year, month, day, hour, messageTimeInMillis, courseCode, courseName, Message.CANCEL_MESSAGE, initiator, Message.HOST, value);
                refFreeHoursUser.child("messages").child(messageTimeInMillis).setValue(messageForHost);
            }

            @Override
            public void error() {

            }
        });

        MyFireBase.getStringValue(refFreeHoursUser, "imagePath", new CallBack_StringValueReady() {
            @Override
            public void stringValueReady(String value) {
                Message messageForGuest = new Message(hostUserId, hostUserName, guestUserId, guestUserName, year, month, day, hour, messageTimeInMillis, courseCode, courseName, Message.CANCEL_MESSAGE, initiator, Message.GUEST, value);
                refMeetingsUser.child("messages").child(messageTimeInMillis).setValue(messageForGuest);
            }

            @Override
            public void error() {

            }
        });
    }

    private ArrayList<WeekViewEvent> returnMeetingsEventsForMonth(int newMonth, int newYear) {
        ArrayList<WeekViewEvent> events = new ArrayList<>();
        String firstKey = String.valueOf(newYear) + "-" + String.valueOf(newMonth);

        if (!meetings.containsKey(firstKey)) {
            return events;
        }

        for (Meeting meeting : meetings.get(firstKey).values()) {
            int year = Integer.parseInt(meeting.getYear());
            int month = Integer.parseInt(meeting.getMonth());
            if (newYear != year || newMonth != month) {
                return events;
            }
            int hour = Integer.parseInt(meeting.getHour());
            int day = Integer.parseInt((meeting.getDay()));
            Calendar startTime = Calendar.getInstance();
            startTime.clear();
            startTime.set(Calendar.YEAR, year);
            startTime.set(Calendar.MONTH, month - 1);
            startTime.set(Calendar.DAY_OF_MONTH, day);
            startTime.set(Calendar.HOUR_OF_DAY, hour);
            startTime.set(Calendar.MINUTE, 0);

            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR, 1);
            WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
            event.setColor(ContextCompat.getColor(context, R.color.meetingRed));
            events.add(event);
        }

        return events;
    }

    private void readMeetingsFromFireBase(String year, String month) {
        final String firstKey = year + "-" + month;
        MyFireBase.getMeetings(refMeetingsUser.child("meetings"), year, month, new CallBack_HashMapReady<Meeting>() {
            @Override
            public void hashMapReady(HashMap<String, Meeting> hashMap) {
                meetings.put(firstKey, hashMap);
            }

            @Override
            public void error() {

            }
        });
    }


}
