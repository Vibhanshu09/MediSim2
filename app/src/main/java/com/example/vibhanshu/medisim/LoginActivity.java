package com.example.vibhanshu.medisim;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;
    private ProgressBar mProgressBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);
        mLogin = findViewById(R.id.login_button);
        mProgressBar = findViewById(R.id.login_progress_bar);
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    setResult(Activity.RESULT_OK,new Intent());
                    //startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    LoginActivity.this.finish();
                }
            }
        };
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin.setText("Logging in...");
                mLogin.setBackground(getResources().getDrawable(R.drawable.button_disabled));
                mLogin.setEnabled(false);
                mProgressBar.setVisibility(View.VISIBLE);
                startSignIn();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    /**
     *  Log in to application as ADMIN
    * Email and password provided by Vibhanshu Rai
    * Contact on 9554044486 to get admin Email and password
    * Or write to Vibhanshu09@outlook.com
    */
    private void startSignIn(){
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Fields are emply", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Sign in Problem", Toast.LENGTH_SHORT).show();
                        mLogin.setText("Login");
                        mLogin.setBackground(getResources().getDrawable(R.drawable.button_enabled));
                        mLogin.setEnabled(true);
                        mProgressBar.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(LoginActivity.this,"Logged in Successfully",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
