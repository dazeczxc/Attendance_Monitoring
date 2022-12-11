package com.example.user.project101;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class Enrolled extends AppCompatActivity {

    String subjectAdd, sectionAdd, subjectID;
    TextView txtVDate, txtVSubject, txtVSection, nameTitle;
    Button btnScanB;

    ListView mListView;
    ArrayList<Student_Model> mList;
    Student_RecordListAdapter mAdapter = null;
    StudentDBHelper dbHelper;

    String scannedData;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(Enrolled.this, R.color.colorHome));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent incommingSubjectID = getIntent();
        Intent incommingSubject = getIntent();
        Intent incommingSection = getIntent();

        subjectID = incommingSubjectID.getStringExtra("PassSubjectID");
        subjectAdd = incommingSubject.getStringExtra("PassSubject");
        sectionAdd = incommingSection.getStringExtra("PassSection");

        //passing data
        txtVSubject = (TextView)findViewById(R.id.textviewSubject);
        txtVSection = (TextView)findViewById(R.id.textViewSectionRow);
        nameTitle = (TextView) findViewById(R.id.namecc);

        txtVSubject.setText(subjectAdd);
        txtVSection.setText(sectionAdd);


        //databse declaration
        dbHelper = new StudentDBHelper(Enrolled.this);
        mListView = (ListView) findViewById(R.id.listView);
        mList = new ArrayList<>();
        mAdapter = new Student_RecordListAdapter(this, R.layout.row_student, mList);
        mListView.setAdapter(mAdapter);


        refreshRecords();


        //listview items onclick
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                //working

                final CharSequence[] items = {"Delete"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(Enrolled.this);
                dialog.setTitle("Choose an Action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        if (i == 0) {
                            //delete
                            Cursor c = dbHelper.getData("SELECT studentID FROM Student ORDER BY NAME ASC");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                            refreshRecords();
                        }



                    }
                });
                dialog.show();

            }
        });



        btnScanB = (Button) findViewById(R.id.buttonScan) ;
        btnScanB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

    }

    //open database
    @Override
    protected void onStart() {
        super.onStart();
        dbHelper.openDB();
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

                //checks if the student is already in the attendance
                Cursor cursor = dbHelper.getData(" SELECT * FROM STUDENT WHERE Name = '"+scannedData+"' AND SUBJECT = '"+subjectID+"' ");
                mList.clear();

                while (cursor.moveToNext()){
                    String name = cursor.getString(1);


                    mList.add(new Student_Model(name));

                    //if true then the user will be notified
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                     builder.setTitle("Student is already at the list");
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
                if (mList.size() == 0){
                    //if none then the data will be saved

                    dbHelper.insert(scannedData, subjectID);
                    Toast.makeText(Enrolled.this, "Data Save Successfully", Toast.LENGTH_SHORT).show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(scannedData+"\n\n");
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
        Cursor data = dbHelper.getData(" SELECT * FROM STUDENT WHERE SUBJECT = '"+subjectID+"' ORDER BY NAME ASC");
        mList.clear();

        while (data.moveToNext()){
            String name = data.getString(1);
             mList.add(new Student_Model(name));
        }
        mAdapter.notifyDataSetChanged();
         nameTitle.setVisibility(View.VISIBLE);

        if (mList.size() == 0){
            Toast.makeText(this, "No Record", Toast.LENGTH_SHORT).show();

            nameTitle.setVisibility(View.INVISIBLE);
         }
    }

    //delete dialog
    private void showDialogDelete(final int idRecord){
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(Enrolled.this);
        dialogDelete.setMessage("Are you sure you want to permanently delete the student? \n\n This cannot be undone!");
        dialogDelete.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteData(idRecord);
                Toast.makeText(Enrolled.this, "Student Deleted Successfully", Toast.LENGTH_SHORT).show();
                refreshRecords();
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

}
