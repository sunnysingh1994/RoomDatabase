package com.example.systemtaskapp;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText Email,Password,mUsername,mEmail,mPassword,mReEnter;
    private Button Login,SignUp,mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email=(EditText)findViewById(R.id.input_email);
        Password=(EditText) findViewById(R.id.input_password);
        Login=(Button)findViewById(R.id.btn_login);
        SignUp=(Button)findViewById(R.id.btn_signup);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();

            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(LoginActivity.this);
                View mView=getLayoutInflater().inflate(R.layout.signup_popup,null);
                mUsername=(EditText) mView.findViewById(R.id.input_username);
                mEmail=(EditText) mView.findViewById(R.id.input_email);
                mPassword=(EditText) mView.findViewById(R.id.input_password);
                mReEnter=(EditText) mView.findViewById(R.id.input_reEnterPassword);
                mSignup=(Button) mView.findViewById(R.id.btn_signup2);
                mSignup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login2();

                    }
                });
               mBuilder.setView(mView);
               AlertDialog dialog=mBuilder.create();
               dialog.show();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if(validate()){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        if (!validate()) {
            onLoginFailed();
            return;
        }
    }
    public void login2() {
        Log.d(TAG, "LoginSignUp");

        if(validate2()){
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }
        if (!validate2()) {
            onLoginFailed2();
            return;
        }
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        Login.setEnabled(true);
    }
    public void onLoginFailed2() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        Login.setEnabled(true);
    }
    public boolean validate() {

        boolean valid = true;

        String email = Email.getText().toString();
        String password =Password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("enter a valid email address");

            valid = false;
        } else {
            Email.setError(null);
        }

        if (password.isEmpty() || password.length() <= 6 || password.length() > 10) {
            Password.setError("between 6 and 10 alphanumeric characters");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }



    public boolean validate2() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        String password =mPassword.getText().toString();
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
        String reEnterPassword=mReEnter.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("enter a valid email address");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        if (password.isEmpty() || password.length() <= 6 || password.length() > 10) {
            mPassword.setError("between 6 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        if (reEnterPassword.isEmpty() || reEnterPassword.length() <= 6 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            mReEnter.setError("Password Do not match");
            valid = false;
        } else {
            mReEnter.setError(null);
        }

        return valid;
    }





}
