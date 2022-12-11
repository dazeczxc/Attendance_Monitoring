package com.example.user.project101;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //initialization


    public long backPressedTime;
    private Toast backToast;

    //simple date format which is used in the database
    DateFormat dff = new SimpleDateFormat("hh:mm a");
    Date noww = new Date();
    String timenow = dff.format(noww);

    //date fromat which has the name of the day or weekdays
    Calendar calendar = Calendar.getInstance();
    String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

    TextView txtvDate, txtvName;
    Button btnOpenSub, btnOpenAttendance, btnOpenScan;

    //database
    MyDBHelper attendanceDBHelper;
    SubjectDBHelper dbHelper;
    ArrayList mList, mList2;

    Spinner spinSubj;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(Home.this, R.color.colorHomeStatusBar));

        //declaration of variables after initialization...

        dbHelper = new SubjectDBHelper(Home.this);
        attendanceDBHelper = new MyDBHelper(Home.this);
        mList = new ArrayList();
        mList2 = new ArrayList();

        //setting the name of the Usser
        txtvName = (TextView) findViewById(R.id.txtVName);
        Intent incommingName = getIntent();
        String passName = incommingName.getStringExtra("PassName");
        txtvName.setText("Prof. "+passName);


        txtvDate = (TextView) findViewById(R.id.txtvCurrentDate);
        txtvDate.setText(currentDate);

        //button for viewing the subject
        btnOpenSub = (Button) findViewById(R.id.btnOpenSubj);
        btnOpenSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent newStartIntent = new Intent(getApplicationContext(),Homepage.class);
                startActivity(newStartIntent);
            }
        });

        //button for scanning
        btnOpenScan = (Button) findViewById(R.id.btnScanOpen);
        btnOpenScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectSubjectforScanning(Home.this);
            }
        });

        //button for viewing the attendance
        btnOpenAttendance = (Button) findViewById(R.id.btnViewAttendance);
        btnOpenAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectSubjectforAttendance(Home.this);
            }
        });

        //declaration of navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    //shows selecting subject dialog
    private void showSelectSubjectforAttendance(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.select_subject_dialog);
        dialog.setTitle("Select Subject");

        //initialize the spinner
        final Spinner spinSubj = (Spinner) dialog.findViewById(R.id.spinnerSub);
        final Spinner spinSect = (Spinner) dialog.findViewById(R.id.spinnerSect);

        //and button
        Button btnSelectSubj = (Button) dialog.findViewById(R.id.buttonSelectSubject);

        //set width for dialog activity
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.90);
        //set height for dialog activity
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.35);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        //query the subjects from the database
            Cursor data = dbHelper.getData("SELECT * FROM tblSubject");
            mList.clear();

            while(data.moveToNext()){


                mList.add(data.getString(1));

                //put the result into the spinner
                SpinnerAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,mList);
                spinSubj.setAdapter(listAdapter);
            }

        //if the result is none then toast the message of empty
        if(mList.size() == 0){
            mList.add("No Subject");
            btnSelectSubj.setEnabled(false);
            Toast.makeText(this, "Please Add Subject First!", Toast.LENGTH_LONG).show();

        }

        //when the subject is selected .. the other spinner will automatically update based on the subject
        //this is when there are similar subjects with different section.. so the other spinner will set data with corresponding sections
        spinSubj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinSubj.getSelectedItem().toString();

                //sql query for specific sections from the selected subject
                Cursor data1 = dbHelper.getData("SELECT * FROM tblSubject WHERE SUBJECT = '"+selected+"'");

                mList2.clear();
                while(data1.moveToNext()){
                    mList2.add(data1.getString(2));

                    //setting data into spinner
                    SpinnerAdapter listAdapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,mList2);
                    spinSect.setAdapter(listAdapter2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //button to open the attendance
        btnSelectSubj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the selected value from spinner and open the attendance subj

                String subjfromSpinner = spinSubj.getSelectedItem().toString();
                String sectionfromSpinner = spinSect.getSelectedItem().toString();

                Intent newStartIntent = new Intent(getApplicationContext(), Attendance_from_Home.class);
                newStartIntent.putExtra("PassSubject", subjfromSpinner);
                newStartIntent.putExtra("PassSection", sectionfromSpinner);

                startActivity(newStartIntent);
                dialog.dismiss();


            }
        });

    }


    //shows selecting subject dialog
    private void showSelectSubjectforScanning(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.select_subject_dialog);
        dialog.setTitle("Select Subject");


        Button btnSelectSubj = (Button) dialog.findViewById(R.id.buttonSelectSubject);

        //set width for dialog activity
        int width = (int)(activity.getResources().getDisplayMetrics().widthPixels*0.90);
        //set height for dialog activity
        int height = (int)(activity.getResources().getDisplayMetrics().heightPixels*0.35);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        //declaration
        final Spinner spinSubj = (Spinner) dialog.findViewById(R.id.spinnerSub);
        final Spinner spinSect = (Spinner) dialog.findViewById(R.id.spinnerSect);

        //adding values to spinner...subject
        Cursor data = dbHelper.getData("SELECT * FROM tblSubject");
        mList.clear();
        while(data.moveToNext()){
            mList.add(data.getString(1));

            SpinnerAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,mList);
            spinSubj.setAdapter(listAdapter);
        }
        if(mList.size() == 0){
            mList.add("No Subject");
            btnSelectSubj.setEnabled(false);
            Toast.makeText(this, "Please Add Subject First!", Toast.LENGTH_LONG).show();

        }

        //adding values to spinner...section
        spinSubj.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = spinSubj.getSelectedItem().toString();

                Cursor data1 = dbHelper.getData("SELECT * FROM tblSubject WHERE SUBJECT = '"+selected+"'");

                mList2.clear();
                while(data1.moveToNext()){
                    mList2.add(data1.getString(2));

                    SpinnerAdapter listAdapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item,mList2);
                    spinSect.setAdapter(listAdapter2);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        btnSelectSubj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getting the selected value from spinner and open the scanner

                String subjfromSpinner = spinSubj.getSelectedItem().toString();
                String sectionfromSpinner = spinSect.getSelectedItem().toString();

                Intent newStartIntent = new Intent(getApplicationContext(), Scan_bg.class);

                newStartIntent.putExtra("PassSubject", subjfromSpinner);
                newStartIntent.putExtra("PassSection", sectionfromSpinner);

                startActivity(newStartIntent);
                dialog.dismiss();

            }
        });

    }

    //open database
    @Override
    protected void onStart() {
        super.onStart();
        dbHelper.openDB();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }


        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;

        }else {
            backToast = Toast.makeText(Home.this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    //navigation code for opening activities
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //nav home
        if (id == R.id.nav_Home) {

            //nav subject
        } else if (id == R.id.nav_Subject) {
            Intent newStartIntent = new Intent(getApplicationContext(),Homepage.class);
            startActivity(newStartIntent);
            finish();

            //logout
        } else if (id == R.id.nav_Logout) {
            AlertDialog.Builder dialogLogout = new AlertDialog.Builder(Home.this);
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
}
