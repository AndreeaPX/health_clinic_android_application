package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView tvSignUp, tvForgetPassword;
    Button btnLogin;
    EditText etEmail, etPassword;
    public static final String SHAREDPREFERENCES = "preferencesLogin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeComponents();
        recuperateSharedPreferences();
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intent);
            }
        });
        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setTransformationMethod(null);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    validateUser();
                }
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChangePasswordActivity.class));
            }
        });
    }

    protected boolean validateData(){
        if(etEmail.getText() == null || etEmail.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.invalid_Email,Toast.LENGTH_LONG).show();
            return false;
        }
        if(etPassword.getText() == null || etPassword.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.invalid_password,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    protected void initializeComponents(){
        tvSignUp=findViewById(R.id.tv_singUp);
        btnLogin=findViewById(R.id.login_btn);
        etEmail=findViewById(R.id.etEmail);
        etPassword=findViewById(R.id.etPassword);
        tvForgetPassword = findViewById(R.id.tv_forgot_pass);
    }

    protected void validateUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, User.URL + "login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(Objects.equals(response, "password match")){
                    saveInSharedPreferences();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),R.string.verify_connection,Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();
                map.put("email",etEmail.getText().toString().trim());
                map.put("password",etPassword.getText().toString().trim());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void saveInSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHAREDPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", etEmail.getText().toString().trim());
        editor.putString("password", etPassword.getText().toString().trim());
        editor.putBoolean("session",true);
        editor.apply();
    }

    private void recuperateSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHAREDPREFERENCES, MODE_PRIVATE);
        etEmail.setText(sharedPreferences.getString("email",""));
        etPassword.setText(sharedPreferences.getString("password",""));

    }

}