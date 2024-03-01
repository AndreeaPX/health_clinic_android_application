package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ChangePasswordActivity extends AppCompatActivity {

    EditText etEmail, etPassword, etConfirmPassword, etPhoneNumber;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initializeComponents();
        etConfirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etConfirmPassword.setTransformationMethod(null);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    changePasswordAndValidate();
                }
            }
        });
    }

    private void initializeComponents(){
        etEmail = findViewById(R.id.etEmailCheck);
        etPassword = findViewById(R.id.etPasswordCheck);
        etConfirmPassword = findViewById(R.id.etConfirmPasswordCheck);
        etPhoneNumber = findViewById(R.id.etPhoneNumberCheck);
        btnSave = findViewById(R.id.btn_changePass);
    }

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
        if(etPhoneNumber.getText()==null || etPhoneNumber.getText().toString().isEmpty() || etPhoneNumber.getText().length()<10){
            Toast.makeText(getApplicationContext(),R.string.error_phoneNumber_adding,Toast.LENGTH_LONG).show();
            return false;
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

    protected void changePasswordAndValidate(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, User.URL + "changePassword.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(Objects.equals(response, "correct")){
                    Toast.makeText(getApplicationContext(),R.string.success, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
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
                map.put("password",etConfirmPassword.getText().toString().trim());
                map.put("phone",etPhoneNumber.getText().toString().trim());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}