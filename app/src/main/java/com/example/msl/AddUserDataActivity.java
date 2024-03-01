package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Instance.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddUserDataActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Button btnDate, btnNext;
    EditText etFirstName, etLastName, etPhone, etAddress, etOccupation, etCity;
    RadioGroup rgGender;
    RadioButton rbMale, rbFemale;
    Intent intent;
    private final static String URL = User.URL+"addDataAfterSignUp.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_data);
        initDatePicker();
        initComponents();
        btnDate.setText(getTodayDate());

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateUser()) {
                    User user = createUser();
                    if (user != null ) {
                        registerUserData(user, URL);
                    }
                }
            }
        });

    }

    private String getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day,month,year);
    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);

                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month - 1, dayOfMonth);
                int yearSelected = selectedDate.get(Calendar.YEAR);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int age = currentYear - yearSelected;
                if (age < 18) {
                    Toast.makeText(getApplicationContext(), R.string.under_18, Toast.LENGTH_SHORT).show();
                    return;
                }

                btnDate.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this,dateSetListener,year,month,day);

    }

    private String makeDateString(int dayOfMonth, int month, int year){
        return year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", dayOfMonth);
    }

    public void openDatePicker(View view){
        datePickerDialog.show();
    }

    protected void initComponents(){

        btnDate = findViewById(R.id.button_datePicker_data);
        btnNext=findViewById(R.id.button_next);
        etFirstName = findViewById(R.id.editText_FirstName);
        etLastName = findViewById(R.id.EditText_LastName);
        etPhone = findViewById(R.id.EditText_PhoneNumber);
        etAddress = findViewById(R.id.EditText_Address);
        etOccupation=findViewById(R.id.editText_Occupation);
        rgGender = findViewById(R.id.radioGroup_data);
        rbMale = findViewById(R.id.radioButton_male);
        rbFemale = findViewById(R.id.radioButton_female);
        etCity = findViewById(R.id.EditText_City);
    }

    protected boolean validateUser(){

        if(etLastName.getText()==null || etLastName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
            return false;
        }

        if(etFirstName.getText()==null || etFirstName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
            return false;
        }

        if(etOccupation.getText()==null || etOccupation.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
            return false;
        }

        if(etPhone.getText()==null || etPhone.getText().toString().isEmpty() || etPhone.getText().length()<10){
            Toast.makeText(getApplicationContext(),R.string.error_phoneNumber_adding,Toast.LENGTH_LONG).show();
            return false;
        }

        String selectedDate = btnDate.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(selectedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int yearSelected = calendar.get(Calendar.YEAR);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int age = currentYear - yearSelected;

            if (age < 18) {
                Toast.makeText(getApplicationContext(), R.string.under_18, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(etCity.getText()==null || etCity.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    protected User createUser(){
        intent=getIntent();
            if (intent.hasExtra(SignUpActivity.SEND_EMAIL) && intent.hasExtra(SignUpActivity.SEND_PASSWORD)) {
                String email = getIntent().getStringExtra(SignUpActivity.SEND_EMAIL);
                String password = getIntent().getStringExtra(SignUpActivity.SEND_PASSWORD);
                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
                Date birthday = DateConverter.fromString(btnDate.getText().toString().trim());
                RadioButton checkedButton = findViewById(rgGender.getCheckedRadioButtonId());
                String gender = checkedButton.getText().toString();
                String occupation = etOccupation.getText().toString();
                String city = etCity.getText().toString();
                return new User(email, password, firstName, lastName, phone, address, birthday, gender, occupation, city);
            }

        else return null;
    }


    protected void registerUserData(User user, String URL) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(Objects.equals(response, "error")) {
                    Toast.makeText(getApplicationContext(), R.string.error_phoneNumber_unique, Toast.LENGTH_LONG).show();
                }
                else {
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
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
                map.put("email", user.getEmail());
                map.put("password", user.getPassword());
                map.put("firstName", user.getFirstName());
                map.put("lastName", user.getLastName());
                map.put("phone", user.getPhone());
                map.put("address", user.getAddress());
                map.put("gender", user.getGender());
                map.put("birthday", DateConverter.fromDate(user.getBirthday()));
                map.put("occupation", user.getOccupation());
                map.put("city", user.getCity());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }




}