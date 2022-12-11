package com.example.user.project101;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
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

public class Attendance_from_Home extends AppCompatActivity {

    //initialization
    String scannedData;

    DateFormat dff = new SimpleDateFormat("hh:mm a");
    Date noww = new Date();
    String timenow = dff.format(noww);

    //date fromat
    Calendar calendar = Calendar.getInstance();
    String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    Date now = new Date();
    String datenow = df.format(now);

    String subjectAdd, sectionAdd, subjectID;

    ListView mListView;
    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;

    TextView txtVDate, txtVSubject, txtVSection, txtvtext, txtvSearchDate, nameTitle, timeTitle, subject_ID;
    Button btnScanB;
    MyDBHelper dbHelper;
    SubjectDBHelper subjectdbhelper;

    ArrayList<Student_Model> student_mList;
    Student_RecordListAdapter student_mAdapter = null;
    StudentDBHelper studentdbHelper;



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_from__home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setStatusBarColor(ContextCompat.getColor(Attendance_from_Home.this, R.color.colorHome));

        dbHelper = new MyDBHelper(Attendance_from_Home.this);
        subjectdbhelper = new SubjectDBHelper(Attendance_from_Home.this);
        studentdbHelper = new StudentDBHelper(Attendance_from_Home.this);
        student_mList = new ArrayList<>();
        student_mAdapter = new Student_RecordListAdapter(this, R.layout.row_student, student_mList);

        //date fromat
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        //passing data
        txtVSubject = (TextView)findViewById(R.id.textviewSubject);
        txtVSection = (TextView)findViewById(R.id.textViewSectionRow);
        txtvtext = (TextView) findViewById(R.id.text);
        txtvSearchDate = (TextView) findViewById(R.id.textViewSearchDate);
        nameTitle = (TextView) findViewById(R.id.namexx);
        timeTitle = (TextView) findViewById(R.id.timexx);
        subject_ID = (TextView) findViewById(R.id.txtvSubjectID);

        txtvSearchDate.setText(datenow);

        Intent incommingSubject = getIntent();
        Intent incommingSection = getIntent();

        subjectAdd = incommingSubject.getStringExtra("PassSubject");
        sectionAdd = incommingSection.getStringExtra("PassSection");

        txtVSubject.setText(subjectAdd);
        txtVSection.setText(sectionAdd);

        btnScanB = (Button) findViewById(R.id.buttonScan) ;
        btnScanB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        //setting current date
        txtVDate = (TextView)findViewById(R.id.textviewDate);
        txtVDate.setText(currentDate);


        mListView = (ListView) findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        mListView.setAdapter(mAdapter);

        //attendance query
        Cursor Subjectdata = subjectdbhelper.getData(" SELECT * FROM tblSubject WHERE SUBJECT = '"+subjectAdd+"' AND section = '"+sectionAdd+"' ");
        mList.clear();
        while (Subjectdata.moveToNext()) {
            String sub= Subjectdata.getString(0);
            subject_ID.setText(sub);

        }
        mAdapter.notifyDataSetChanged();

        subjectID = subject_ID.getText().toString();

        //flooding the listview with data from databse
        Cursor cursor = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectID+"' AND DATE = '"+datenow+"' ORDER BY NAME");
        mList.clear();

        while (cursor.moveToNext()){
            String name = cursor.getString(0);
            String time = cursor.getString(1);
            mList.add(new Model(name, time));

        }
        mAdapter.notifyDataSetChanged();
        if (mList.size() == 0){
            Toast.makeText(this, "No Record", Toast.LENGTH_SHORT).show();

            nameTitle.setVisibility(View.INVISIBLE);
            timeTitle.setVisibility(View.INVISIBLE);

        }
        refreshRecords();

        //listview items onclick
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });

    }
    //open database
    @Override
    protected void onStart() {
        super.onStart();
        dbHelper.openDB();
        studentdbHelper.openDB();
    }



    private void startScan() {
        final CharSequence[] items = {"Edit", "View Attendance"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Scan Student Info for "+subjectAdd+" "+sectionAdd+ " Subject");
        builder.setTitle(" ");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                scanCode();
            }

        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Intent newStartIntent = new Intent(getApplicationContext(),Home.class);
                startActivity(newStartIntent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //qr scanner code method
    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.setBeepEnabled(true);
        integrator.initiateScan();
    }



    //scanned result inserted into database and when no result
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            scannedData = result.getContents();
            if (scannedData != null){


                //checks if the student is enrolled in the subject
                Cursor cursor_verify = studentdbHelper.getData(" SELECT * FROM STUDENT WHERE NAME = '"+scannedData+"' AND SUBJECT = '"+subjectID+"' ");
                student_mList.clear();

                while (cursor_verify.moveToNext()) {
                    String name = cursor_verify.getString(1);

                    student_mList.add(new Student_Model(name));
                }
                student_mAdapter.notifyDataSetChanged();

                if(student_mList.size() == 0) {

                    //
                    //if true then the user will be notified
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Student is not enrolled in this subject");
                    builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scanCode();
                        }
                    }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    //




                }else{
                    //
                    //checks if the student is already in the attendance
                    Cursor cursor = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE NAME = '" + scannedData + "' AND DATE = '" + datenow + "' AND SUBJECT = '" + subjectID + "' ");
                    mList.clear();

                    while (cursor.moveToNext()) {
                        String name = cursor.getString(0);
                        String time = cursor.getString(1);
                        String date = cursor.getString(2);

                        mList.add(new Model(name, time));

                        //if true then the user will be notified
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(name + "\n\n Time In:  " + time);
                        builder.setTitle("Student is already checked!");
                        builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanCode();
                            }
                        }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        refreshRecords();

                    }
                    mAdapter.notifyDataSetChanged();
                    if (mList.size() == 0) {
                        //if none then the data will be saved

                        dbHelper.insert(scannedData, datenow, timenow, subjectID);
                        Toast.makeText(Attendance_from_Home.this, "Data Save Successfully", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(scannedData + "\n\n Time In:  " + timenow);
                        builder.setTitle("Scanned Result");
                        builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanCode();

                            }
                        }).setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        refreshRecords();
                    }
                    //

                }
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
                refreshRecords();
            }

        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //refresh the data when adding scanned
    private void refreshRecords() {
        Cursor data = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectID+"' AND DATE = '"+datenow+"' ORDER BY TIME");
        mList.clear();

        while (data.moveToNext()){
            String name = data.getString(0);
            String time = data.getString(1);
            mList.add(new Model(name, time));

        }
        mAdapter.notifyDataSetChanged();
        nameTitle.setVisibility(View.VISIBLE);
        timeTitle.setVisibility(View.VISIBLE);

        txtvSearchDate.setText(datenow);

        if (mList.size() == 0){
            Toast.makeText(this, "No Record", Toast.LENGTH_SHORT).show();

            nameTitle.setVisibility(View.INVISIBLE);
            timeTitle.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //code here
            handleDates();
            return true;
        }
        if( id == R.id.action_refresh){
            refreshRecords();
            txtvtext.setText(" Today");
            txtVDate.setText(currentDate);
            btnScanB.setVisibility(View.VISIBLE);
            txtvSearchDate.setText(datenow);

            return true;
        }

        if( id == R.id.action_sort){
            sortData();

            //code here
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleDates(){
        Calendar calendar = Calendar.getInstance();


        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear + 1;
                if(monthOfYear > 9){
                    String dateString = monthOfYear+"/"+dayOfMonth+"/"+year;
                    txtvSearchDate.setText(dateString);
                }else if(monthOfYear <10){
                    String zero = "0";
                    String dateString = zero+monthOfYear+"/"+dayOfMonth+"/"+year;
                    txtvSearchDate.setText(dateString);
                }
                handle();
            }
        }, year, month, date);

        datePickerDialog.show();
    }

    private void handle(){
        final String da = txtvSearchDate.getText().toString();

        btnScanB.setVisibility(View.INVISIBLE);
        txtVDate.setText(da);
        txtvtext.setText("Date");

        Cursor data = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectID+"' AND DATE = '"+da+"'  ORDER BY NAME");
        mList.clear();

        while (data.moveToNext()){
            String name = data.getString(0);
            String time = data.getString(1);
            mList.add(new Model(name, time));

        }
        mAdapter.notifyDataSetChanged();
        nameTitle.setVisibility(View.VISIBLE);
        timeTitle.setVisibility(View.VISIBLE);

        if (mList.size() == 0){
            Toast.makeText(Attendance_from_Home.this, "No Records for this date", Toast.LENGTH_LONG).show();
            nameTitle.setVisibility(View.INVISIBLE);
            timeTitle.setVisibility(View.INVISIBLE);

        }
/*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(subjectAdd+" "+sectionAdd+" \n \nDate: "+da);
        builder.setTitle("Search Attendance");
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btnScanB.setVisibility(View.INVISIBLE);
                txtVDate.setText(da);
                txtvtext.setText("Attendance on:");

                Cursor data = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectAdd+"' AND DATE = '"+da+"'  ORDER BY NAME");
                mList.clear();

                while (data.moveToNext()){
                    String name = data.getString(0);
                    String time = data.getString(1);
                    mList.add(new Model(name, time));

                }
                mAdapter.notifyDataSetChanged();
                if (mList.size() == 0){
                    Toast.makeText(Attendance_from_Home.this, "No Records for this date", Toast.LENGTH_LONG).show();
                    nameTitle.setVisibility(View.INVISIBLE);
                    timeTitle.setVisibility(View.INVISIBLE);

                }

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                refreshRecords();
                dialog.dismiss();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        */
    }


    private void sortData(){

        final String da = txtvSearchDate.getText().toString();

        final CharSequence[] items = {"Name Ascending", "Name Descending", "Time Ascending", "Time Descending"};

        AlertDialog.Builder dialog = new AlertDialog.Builder(Attendance_from_Home.this);
        dialog.setTitle("Sort by:");

        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    //name ascending
                    Cursor data = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectID+"' AND DATE = '"+da+"'  ORDER BY NAME ASC");
                    mList.clear();

                    while (data.moveToNext()){
                        String name = data.getString(0);
                        String time = data.getString(1);
                        mList.add(new Model(name, time));
                    }
                    mAdapter.notifyDataSetChanged();

                }
                if(i == 1){
                    //name descending
                    Cursor data = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectID+"' AND DATE = '"+da+"'  ORDER BY NAME DESC");
                    mList.clear();

                    while (data.moveToNext()){
                        String name = data.getString(0);
                        String time = data.getString(1);
                        mList.add(new Model(name, time));

                    }
                    mAdapter.notifyDataSetChanged();
                }
                if(i == 2){
                    //time ascending
                    Cursor data = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectID+"' AND DATE = '"+da+"'  ORDER BY TIME ASC");
                    mList.clear();

                    while (data.moveToNext()){
                        String name = data.getString(0);
                        String time = data.getString(1);
                        mList.add(new Model(name, time));

                    }
                    mAdapter.notifyDataSetChanged();
                }
                if(i == 3){
                    //time descending
                    Cursor data = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE SUBJECT = '"+subjectID+"' AND DATE = '"+da+"'  ORDER BY TIME DESC");
                    mList.clear();

                    while (data.moveToNext()){
                        String name = data.getString(0);
                        String time = data.getString(1);
                        mList.add(new Model(name, time));

                    }
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
        dialog.show();
    }


}
