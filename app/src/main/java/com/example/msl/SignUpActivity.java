package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.User;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    TextView tvLogin;
    Button btnSignUp;
    EditText etEmail, etPassword, etConfirmPassword;
    private static final String URL = User.URL+"/signUp.php";
    public static final String SEND_EMAIL = "EMAIL";
    public static final String SEND_PASSWORD = "PASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeComponents();
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

       etConfirmPassword.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               etConfirmPassword.setTransformationMethod(null);
           }
       });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()) {
                        registerUser(etEmail.getText().toString().trim(), etConfirmPassword.getText().toString().trim());

                }
            }
    });}

    protected boolean isValid(){
        if(etEmail.getText() == null || etEmail.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            String email = etEmail.getText().toString().trim();
            if(!email.matches(emailPattern)){
                Toast.makeText(getApplicationContext(),R.string.invalid_Email, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        if(etPassword.getText() == null || etPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_LONG).show();
            return false;
        }
        if(etConfirmPassword.getText() == null || etConfirmPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.enter_confirmation, Toast.LENGTH_LONG).show();
            return false;
        }
        if(!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
            Toast.makeText(getApplicationContext(), R.string.error_confirmation, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    protected void initializeComponents(){
        tvLogin=findViewById(R.id.tv_login);
        btnSignUp=findViewById(R.id.signUp_btn);
        etEmail = findViewById(R.id.etEmail_su);
        etPassword = findViewById(R.id.etPassword_su);
        etConfirmPassword = findViewById(R.id.etConfirmPass);
    }

    protected void registerUser(final String email, final String password) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(Objects.equals(response, "email exist")){
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent =  new Intent(getApplicationContext(),AddUserDataActivity.class);
                    intent.putExtra(SEND_EMAIL,email);
                    intent.putExtra(SEND_PASSWORD,password);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("email",email);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}