package com.example.user.project101;


import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    //initialize globaly the needed components....set variables

    public long backPressedTime;
    private Toast backToast;

    EditText etxtnewUserName;
    EditText etxtnewPassword;
    Button btnnewLogin;

    TextView btnsignup;
    ArrayList mList;

    String user, pass, passName;

    //database
    AccountDBHelper myDb;

    //end of initialization

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorHomepage));
        setContentView(R.layout.activity_main);

        //connecting the variable to the components from the layout xml
        etxtnewUserName = (EditText) findViewById(R.id.editTextUserName);
        etxtnewPassword = (EditText) findViewById(R.id.editTextPassword);
        btnnewLogin = (Button) findViewById(R.id.buttonLogin);

        myDb = new AccountDBHelper(this);
        mList = new ArrayList();

        //l0gin button
        btnnewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = etxtnewUserName.getText().toString();
                pass = etxtnewPassword.getText().toString();

                //if the text field is empty
                if (user.equals("") || pass.equals(""))
                {
                    Toast.makeText(MainActivity.this, "Please enter the credentials", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    // if not empty.. verifies the data from the databse
                    Boolean userpassresult =  myDb.checkusernamepassword(user, pass);
                    if (userpassresult == true) {

                        //proceed if the values from the database is true
                        Cursor data = myDb.getData("select * from tbusers where username = '" +user+ "'");
                        mList.clear();
                        while (data.moveToNext()) {
                            passName = data.getString(2);
                        }

                        Intent newStartIntent = new Intent(getApplicationContext(), Home.class);
                        newStartIntent.putExtra("PassName", passName);
                        startActivity(newStartIntent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        //sign up button... the textview which acts as button for signup
        btnsignup = (TextView) findViewById(R.id.txtSignup);
        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens the sign up page
                Intent newStartIntent = new Intent(getApplicationContext(), Signup_Page.class);
                startActivity(newStartIntent);
                finish();

            }
        });

    }

    //hide or shows the password
    public void ShowHidePassword (View view) {
        if (view.getId() == R.id.iconEye) {
            //shows
            if (etxtnewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.ic_eye_open);
                etxtnewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                //hides
                ((ImageView) (view)).setImageResource(R.drawable.ic_eye_close);
                etxtnewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {



        if (backPressedTime + 2000 > System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();

            finishAffinity();
            System.exit(0);

            return;

        }else {
            backToast = Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}
