package com.example.user.project101;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Homepage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //initialization
    SubjectDBHelper dbHelper;
    ListView mListView;
    ArrayList<Subject_Model> mList;
    Subject_RecordListAdapter mAdapter = null;

    int tHour, tMinute, t2Hour, t2Minute;

    Model mm;

    Button btnAddSub;
    String SubjecttobePass;

    TextView name;

    //date
    DateFormat df = new SimpleDateFormat("MMMM-dd-yyyy");
    Date now = new Date();
    String datenow = df.format(now);

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbHelper = new SubjectDBHelper(Homepage.this);

        //status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(Homepage.this, R.color.colorHomepage));

        btnAddSub = (Button) findViewById(R.id.buttonAddSub);
        btnAddSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubjectDialog(Homepage.this);
            }
        });

        name = (TextView) findViewById(R.id.textViewNamePass);

        Intent incommingName = getIntent();


        //getting the subbjects from database and setting into listview with custom design
        mListView = (ListView) findViewById(R.id.listViewHomepageSubject);
        mList = new ArrayList<>();
        mAdapter = new Subject_RecordListAdapter(this, R.layout.row_for_subject_in_homepage, mList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //working

                final CharSequence[] items = {"Edit", "View Attendance", "View Students"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(Homepage.this);
                dialog.setTitle("Choose an Action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        if (i == 0) {
                            final CharSequence[] items = {"Update", "Delete"};

                            AlertDialog.Builder dialog2 = new AlertDialog.Builder(Homepage.this);
                            dialog2.setTitle("Choose an Action");
                            dialog2.setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Cursor c = dbHelper.getData("SELECT id FROM tblSubject");
                                        ArrayList<Integer> arrID = new ArrayList<Integer>();
                                        while (c.moveToNext()){
                                            arrID.add(c.getInt(0));
                                        }
                                        //show update dialog
                                        showDialogUpdate(Homepage.this, arrID.get(position));
                                        updateRecordList();

                                    }
                                    if (which == 1 ){
                                        //delete
                                        Cursor c = dbHelper.getData("SELECT id FROM tblSubject");
                                        ArrayList<Integer> arrID = new ArrayList<Integer>();
                                        while (c.moveToNext()){
                                            arrID.add(c.getInt(0));
                                        }
                                        showDialogDelete(arrID.get(position));
                                        updateRecordList();
                                    }
                                }
                            });
                            dialog2.show();

                        }

                        if (i == 1 ){
                            //attendance
                            Cursor c = dbHelper.getData("SELECT id FROM tblSubject");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            selectData(arrID.get(position));

                        }

                        if (i == 2 ){
                            //enrolled students on the subjects
                            Cursor c = dbHelper.getData("SELECT id FROM tblSubject");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            selectDataEnroll(arrID.get(position));

                        }

                    }
                });
                dialog.show();

            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    //update dialog
    private void showDialogUpdate(Activity activity, final int position){
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.updatesubject_dialog);
        dialog.setTitle("Update");

        final boolean[] selectedDay;
        final ArrayList<Integer> dayList = new ArrayList<>();
        final String[] dayArray = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        final TextView etxtaddSubject = (TextView) dialog.findViewById(R.id.editTextInputSubject);
        final EditText etxtaddSection = (EditText) dialog.findViewById(R.id.editTextInputSection);
        final TextView etxtSelectedDay = (TextView) dialog.findViewById(R.id.editTextInputDay);
        final TextView etxtTime = (TextView) dialog.findViewById(R.id.editTextTime);
        final TextView etxtTimeto = (TextView) dialog.findViewById(R.id.editTextTimeTo);

        Button btnUpdate = (Button) dialog.findViewById(R.id.buttonUpdateSubject);

        //showing the timepicker
        etxtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(Homepage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //initialize hour and minute
                        tHour = hourOfDay;
                        tMinute = minute;

                        //store hour minute to string
                        String time = tHour +":"+ tMinute;
                        //initialize 24 hour time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);
                            //initialize 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            //set selected time to textview
                            etxtTime.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, 12, 0, false);
                ////set transparent backgeound
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ////displaye previous selected Time
                timePickerDialog.updateTime(tHour,tMinute);
                timePickerDialog.show();
            }
        });

        etxtTimeto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(Homepage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //initialize hour and minute
                        t2Hour = hourOfDay;
                        t2Minute = minute;

                        //store hour minute to string
                        String time = t2Hour +":"+ t2Minute;
                        //initialize 24 hour time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);
                            //initialize 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            //set selected time to textview
                            etxtTimeto.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, 12, 0, false);
                ////set transparent backgeound
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ////displaye previous selected Time
                timePickerDialog.updateTime(t2Hour,t2Minute);
                timePickerDialog.show();
            }
        });

        //setting the list for days
        //setting hte values of the list
        selectedDay = new boolean[dayArray.length];
        etxtSelectedDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
                builder.setTitle("Select Day");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(dayArray, selectedDay, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                        if (isChecked){
                            dayList.add(i);
                            Collections.sort(dayList);
                        }else {
                            dayList.remove(i);

                        }
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j=0; j<dayList.size(); j++){
                            stringBuilder.append(dayArray[dayList.get(j)]);
                            if(j !=dayList.size()-1){
                                stringBuilder.append(", ");
                            }
                        }

                        etxtSelectedDay.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int j=0; j<selectedDay.length; j++){
                            selectedDay[j] = false;
                            dayList.clear();
                            etxtSelectedDay.setText("");
                        }
                    }
                });

                builder.show();

            }
        });

        //getting selected data from row
        Cursor data = dbHelper.getData("SELECT * FROM tblSubject WHERE id = " + position);
        mList.clear();

        while (data.moveToNext()) {
            String subjectt = data.getString(1);
            String section = data.getString(2);
            String day = data.getString(3);
            String time = data.getString(4);
            String timeto = data.getString(5);


            etxtaddSubject.setText(subjectt);
            etxtaddSection.setText(section);
            etxtSelectedDay.setText(day);
            etxtTime.setText(time);
            etxtTimeto.setText(timeto);



            mList.add(new Subject_Model(subjectt, section, day, time, timeto));
            mAdapter = new Subject_RecordListAdapter(this, R.layout.row_for_subject_in_homepage, mList);
            mListView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();

        //set width for dialog activity
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.90);
        //set height for dialog activity
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etxtSelectedDay.equals("Select a Day")){
                    Toast.makeText(Homepage.this, "Please select a Day", Toast.LENGTH_SHORT).show();
                }else {
                    dbHelper.updateData(etxtaddSubject.getText().toString().trim(), etxtaddSection.getText().toString().trim(), etxtSelectedDay.getText().toString().trim(),etxtTime.getText().toString().trim(), etxtTimeto.getText().toString().trim(), position);
                    dialog.dismiss();
                    Toast.makeText(Homepage.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    updateRecordList();

                    etxtaddSubject.setText("");
                    etxtaddSection.setText("");
                    etxtSelectedDay.setText("");
                    etxtTime.setText("");
                    etxtTimeto.setText("");

                }

            }
        });
        updateRecordList();
    }


    //delete dialog
    private void showDialogDelete(final int idRecord){
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Homepage.this);
        dialogDelete.setMessage("Are you sure you want to permanently delete this subject? \n\n This cannot be undone!");
        dialogDelete.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteData(idRecord);
                Toast.makeText(Homepage.this, "Subject Deleted Successfully", Toast.LENGTH_SHORT).show();
                updateRecordList();
            }
        });
        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }


    //selects data from specific row and opens the attendance activity
    private void selectData(final int position) {
        //getting selected data from row
        Cursor data = dbHelper.getData("SELECT * FROM tblSubject WHERE id = " + position);
        mList.clear();

        String subjectID = null;
        String subjectt = null;
        String day = null;
        String section = null;
        String time = null;
        String timeto = null;

        while (data.moveToNext()) {
            subjectID = data.getString(0);
            subjectt = data.getString(1);
            section = data.getString(2);
            day = data.getString(3);
            time = data.getString(4);
            timeto = data.getString(5);




            mList.add(new Subject_Model(subjectt, section, day, time, timeto));
            mAdapter = new Subject_RecordListAdapter(this, R.layout.row_for_subject_in_homepage, mList);
            mListView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();

        Intent newStartIntent = new Intent(getApplicationContext(), Attendance_Per_Subject.class);
        newStartIntent.putExtra("PassSubjectID", subjectID);
        newStartIntent.putExtra("PassSubject", subjectt);
        newStartIntent.putExtra("PassSection", section);
        newStartIntent.putExtra("PassDay", day);
        newStartIntent.putExtra("PassTime", time);
        newStartIntent.putExtra("PassTimeto", timeto);

        startActivity(newStartIntent);

    }



    //selects data from specific row and opens the enrolles activity
    private void selectDataEnroll(final int position) {
        //getting selected data from row
        Cursor data = dbHelper.getData("SELECT * FROM tblSubject WHERE id = " + position);
        mList.clear();

        String subjectID = null;
        String subjectt = null;
        String day = null;
        String section = null;
        String time = null;
        String timeto = null;

        while (data.moveToNext()) {
            subjectID = data.getString(0);
            subjectt = data.getString(1);
            section = data.getString(2);
            day = data.getString(3);
            time = data.getString(4);
            timeto = data.getString(5);




            mList.add(new Subject_Model(subjectt, section, day, time, timeto));
            mAdapter = new Subject_RecordListAdapter(this, R.layout.row_for_subject_in_homepage, mList);
            mListView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();

        Intent newStartIntent = new Intent(getApplicationContext(), Enrolled.class);
        newStartIntent.putExtra("PassSubjectID", subjectID);
        newStartIntent.putExtra("PassSubject", subjectt);
        newStartIntent.putExtra("PassSection", section);
        newStartIntent.putExtra("PassDay", day);
        newStartIntent.putExtra("PassTime", time);
        newStartIntent.putExtra("PassTimeto", timeto);

        startActivity(newStartIntent);

    }


    //shows add subject dialog
    private void showAddSubjectDialog(Activity activity) {
        //initialize
         final boolean[] selectedDay;
         final ArrayList<Integer> dayList = new ArrayList<>();
         final String[] dayArray = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.addsubject_dialog);
        dialog.setTitle("Add Subject");

        final EditText etxtaddSubject = (EditText) dialog.findViewById(R.id.etxtAddSubjectDialog);
        final EditText etxtaddSection = (EditText) dialog.findViewById(R.id.etxtAddSectionDialog);
        final TextView etxtSelectedDay = (TextView) dialog.findViewById(R.id.etxtSelectedDay);
        final TextView etxtTime = (TextView) dialog.findViewById(R.id.etxtTimeSched);
        final TextView etxtTimeTo = (TextView) dialog.findViewById(R.id.etxtTimeSchedTo);
        Button btnAddDialog = (Button) dialog.findViewById(R.id.buttonAddSubjectDialog);

        //showing the timepicker
        etxtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(Homepage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //initialize hour and minute
                        tHour = hourOfDay;
                        tMinute = minute;

                        //store hour minute to string
                        String time = tHour +":"+ tMinute;
                        //initialize 24 hour time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);
                            //initialize 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            //set selected time to textview
                            etxtTime.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, 12, 0, false);
                ////set transparent backgeound
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ////displaye previous selected Time
                timePickerDialog.updateTime(tHour,tMinute);
                timePickerDialog.show();
            }
        });

        etxtTimeTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(Homepage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //initialize hour and minute
                        t2Hour = hourOfDay;
                        t2Minute = minute;

                        //store hour minute to string
                        String time = t2Hour +":"+ t2Minute;
                        //initialize 24 hour time format
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm");
                        try {
                            Date date = f24Hours.parse(time);
                            //initialize 12 hours time format
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa");
                            //set selected time to textview
                            etxtTimeTo.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, 12, 0, false);
                ////set transparent backgeound
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                ////displaye previous selected Time
                timePickerDialog.updateTime(t2Hour,t2Minute);
                timePickerDialog.show();
            }
        });

        //showing the weekdays for selection
        selectedDay = new boolean[dayArray.length];
        etxtSelectedDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Homepage.this);
                builder.setTitle("Select Day");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(dayArray, selectedDay, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                        if (isChecked){
                            dayList.add(i);
                            Collections.sort(dayList);
                        }else {
                            dayList.remove(i);

                        }
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int j=0; j<dayList.size(); j++){
                            stringBuilder.append(dayArray[dayList.get(j)]);
                            if(j !=dayList.size()-1){
                                stringBuilder.append(", ");
                            }
                        }

                        etxtSelectedDay.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int j=0; j<selectedDay.length; j++){
                            selectedDay[j] = false;
                            dayList.clear();
                            etxtSelectedDay.setText("");
                        }
                    }
                });

                builder.show();

            }
        });



        //set width for dialog activity
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.90);
        //set height for dialog activity
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.5);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        btnAddDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String aSubject = etxtaddSubject.getText().toString().trim();
                String aSection = etxtaddSection.getText().toString().trim();
                String aDay = etxtSelectedDay.getText().toString().trim();
                String aTime = etxtTime.getText().toString().trim();
                String aTimeTo = etxtTimeTo.getText().toString().trim();



                if(aSubject.equals("") || aSection.equals("") || aTime.equals("")){

                    Toast.makeText(Homepage.this, "Please fill all input fields", Toast.LENGTH_SHORT).show();

                }else if(aDay.equals("")){
                    Toast.makeText(Homepage.this, "Please select scheduled day", Toast.LENGTH_SHORT).show();
                }

                else {

                    long result = dbHelper.insertSub(aSubject, aSection, aDay, aTime, aTimeTo);

                    if (result == -1) {
                        Toast.makeText(Homepage.this, "Some Error occured while Inserting", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Homepage.this, "Data Added Successfully", Toast.LENGTH_SHORT).show();

                    }
                    etxtaddSubject.setText("");
                    etxtaddSection.setText("");
                    etxtSelectedDay.setText("");
                    etxtTime.setText("");
                    etxtTimeTo.setText("");


                    updateRecordList();
                }
            }
        });

    }


    //reload the listview with data from database
    private void updateRecordList() {
        Cursor data = dbHelper.getListContentsSubject();
        mList.clear();

        while (data.moveToNext()){
            String subject = data.getString(1);
            String section = data.getString(2);
            String day = data.getString(3);
            String time = data.getString(4);
            String timeto = data.getString(5);

            mList.add(new Subject_Model(subject, section, day, time, timeto));

        }
        mAdapter.notifyDataSetChanged();
        if (mList.size() == 0){
            Toast.makeText(this, "No Record", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String nametobePass = name.getText().toString();

        if (id == R.id.nav_Home) {
            Intent newStartIntent = new Intent(getApplicationContext(),Home.class);
            newStartIntent.putExtra("PassName", nametobePass);

            startActivity(newStartIntent);
            finish();

        } else if (id == R.id.nav_Subject) {

        } else if (id == R.id.nav_Logout) {
            AlertDialog.Builder dialogLogout = new AlertDialog.Builder(Homepage.this);
            dialogLogout.setMessage("Are you sure you want to Logout?");
            dialogLogout.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent newStartIntent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(newStartIntent);
                    finish();
                }
            });
            dialogLogout.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogLogout.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //open database
    @Override
    protected void onStart() {
        super.onStart();
        dbHelper.openDB();
        updateRecordList();
    }

    // close database
    @Override
    protected void onStop() {
        super.onStop();
        dbHelper.closeDB();
    }



}
