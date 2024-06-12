package com.example.ticketcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketbooking.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Future;

public class SignUp extends AppCompatActivity {
    private Button signUpBtn;
    TextView signInBtn;
    EditText email;
    EditText password;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signUpBtn = findViewById(R.id.signUpBtn);
        signInBtn = findViewById(R.id.signInBtn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.loginPassword);


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the below code sends them to admin tab
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the below code sends them to admin tab
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

}
void createUser(String email , String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password);
    Toast.makeText(this,"Account Created SuccessFully", Toast.LENGTH_LONG).show();
}
}
//createUser(email.getText().toString(),password.getText().toString());