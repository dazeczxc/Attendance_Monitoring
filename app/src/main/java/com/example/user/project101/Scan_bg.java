package com.example.user.project101;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by USER on 16/07/2021.
 */
public class Scan_bg extends AppCompatActivity {

    //initialization
    String scannedData;

    ArrayList<Model> mList;
    RecordListAdapter mAdapter = null;

    //date
    DateFormat dff = new SimpleDateFormat("hh:mm a");
    Date noww = new Date();
    String timenow = dff.format(noww);

    DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    Date now = new Date();
    String datenow = df.format(now);

    //date fromat
    Calendar calendar = Calendar.getInstance();
    String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

    String subjectAdd, sectionAdd, subjectID;

    MyDBHelper dbHelper;
    SubjectDBHelper subjectdbhelper;
    StudentDBHelper studentdbHelper;

    ArrayList<Student_Model> student_mList;
    Student_RecordListAdapter student_mAdapter = null;

    TextView subject_ID;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_background);
        subjectdbhelper = new SubjectDBHelper(Scan_bg.this);
        studentdbHelper = new StudentDBHelper(Scan_bg.this);
        student_mList = new ArrayList<>();
        student_mAdapter = new Student_RecordListAdapter(this, R.layout.row_student, student_mList);

        dbHelper = new MyDBHelper(Scan_bg.this);
        getWindow().setStatusBarColor(ContextCompat.getColor(Scan_bg.this, R.color.colorUdo));

        mList = new ArrayList<>();
        mAdapter = new RecordListAdapter(this, R.layout.row, mList);
        subject_ID = (TextView) findViewById(R.id.txtvSubjectID);

        Intent incommingSubject = getIntent();
        Intent incommingSection = getIntent();

        subjectAdd = incommingSubject.getStringExtra("PassSubject");
        sectionAdd = incommingSection.getStringExtra("PassSection");

        //attendance query
        Cursor Subjectdata = subjectdbhelper.getData(" SELECT * FROM tblSubject WHERE SUBJECT = '"+subjectAdd+"' AND section = '"+sectionAdd+"' ");
        mList.clear();
        while (Subjectdata.moveToNext()) {
            String sub= Subjectdata.getString(0);
            subject_ID.setText(sub);

        }
        mAdapter.notifyDataSetChanged();

        subjectID = subject_ID.getText().toString();

        startScan();
    }

    //open database
    @Override
    protected void onStart() {
        super.onStart();
        dbHelper.openDB();
    }

    private void startScan() {
        final CharSequence[] items = {"Edit", "View Attendance"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Scan Student Info for "+subjectAdd+" "+sectionAdd+ " Subject");
        builder.setTitle(" ");
        builder.setCancelable(false);
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
            if (scannedData != null) {

                //checks if student is enrolled
                //checks if the student is enrolled in the subject
                Cursor cursor_verify = studentdbHelper.getData(" SELECT * FROM STUDENT WHERE NAME = '" + scannedData + "' AND SUBJECT = '" + subjectID + "' ");
                student_mList.clear();

                while (cursor_verify.moveToNext()) {
                    String name = cursor_verify.getString(1);

                    student_mList.add(new Student_Model(name));
                }
                student_mAdapter.notifyDataSetChanged();

                if (student_mList.size() == 0) {
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
                } else {


                    //checks if the student is already in the attendance
                    Cursor cursor = dbHelper.getData(" SELECT * FROM ATTENDANCE WHERE Name = '" + scannedData + "' AND DATE = '" + datenow + "' AND SUBJECT = '" + subjectID + "'");
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
                        }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Intent newStartIntent = new Intent(getApplicationContext(), Home.class);
                                startActivity(newStartIntent);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mList.size() == 0) {
                        //if none then the data will be saved

                        dbHelper.insert(scannedData, datenow, timenow, subjectID);
                        Toast.makeText(Scan_bg.this, "Data Save Successfully", Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(scannedData + "\n\n Time In:  " + timenow);
                        builder.setTitle("Scanned Result");
                        builder.setPositiveButton("Scan Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                scanCode();
                            }
                        }).setNegativeButton("finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                Intent newStartIntent = new Intent(getApplicationContext(), Home.class);
                                startActivity(newStartIntent);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
            else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show();
            }


        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
