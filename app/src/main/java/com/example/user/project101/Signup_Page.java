package com.example.user.project101;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Signup_Page extends AppCompatActivity {

    //initialize globaly the needed components....set variables

    EditText username, password, repassword, namea;
    Button btnSignUp;

    TextView btnSignin;
    ImageView btnBack;
    AccountDBHelper myDb;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__page);

        //setting the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(Signup_Page.this, R.color.colorHomepage));

        //declaration
        btnBack = (ImageView) findViewById(R.id.imvBacktoLogin);

        //return button... the arrow in the signup that to returns to sign in page
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newStartIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(newStartIntent);
                finish();
            }
        });

        //connecting the variable to the components from the layout xml

        namea = (EditText) findViewById(R.id.etxtName);
        username = (EditText) findViewById(R.id.etxtUserName);
        password = (EditText) findViewById(R.id.etxtPassword);
        repassword = (EditText) findViewById(R.id.etxtRePassword);

        btnSignUp = (Button) findViewById(R.id.btnRegister);
        btnSignin = (TextView) findViewById(R.id.txtSigin);

        myDb = new AccountDBHelper(this);

        //signup button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //decalare variables
                String name = namea.getText().toString();
                String user = username.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                //if the input fields are empty.. toast the warn
                if (user.equals("") ||user.equals("") || pass.equals("") || repass.equals(""))
                {
                    Toast.makeText(Signup_Page.this, "Fill all the field first", Toast.LENGTH_LONG).show();
                }

                else
                {
                    //if the password is retype correctly
                    if (pass.equals(repass))
                    {
                        //checks if the username was existing in the database
                        Boolean checkusernameresult = myDb.checkusername(user);

                        //if not then the user can make a new account
                        if (checkusernameresult == false)
                        {
                            //the data will be save in the database
                            Boolean regResult = myDb.insertData(user, pass, name);
                            if (regResult == true)
                            {
                                //set the textfields no values
                                namea.setText("");
                                username.setText("");
                                password.setText("");
                                repassword.setText("");

                                Toast.makeText(Signup_Page.this, "Signup successful, go to Signin", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(Signup_Page.this, "Registration failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        //if already exist then he can proceed with signing in or making new account
                        {
                            Toast.makeText(Signup_Page.this, "Username is already existing \n Please go to Sign In", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    //if not equally retype toat the message
                    {
                        Toast.makeText(Signup_Page.this, "Password did not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //button that opens the signin page
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

}
