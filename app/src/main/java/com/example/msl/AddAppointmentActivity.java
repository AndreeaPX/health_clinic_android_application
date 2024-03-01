package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Instance.Appointment;
import com.example.msl.Instance.Clinic;
import com.example.msl.Instance.Doctor;
import com.example.msl.Instance.Specialization;
import com.example.msl.Instance.RDoctor;
import com.example.msl.Instance.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddAppointmentActivity extends AppCompatActivity {

    DatePickerDialog datePickerDialog;
    Button btnDate, btnTime, btnSend;
    RequestQueue requestQueue;
    Spinner spinnerDoctor, spinnerSpecialization, spinnerClinic;
    EditText etDetails;
    ArrayList<Specialization> specializationList = new ArrayList<>();
    ArrayList<Doctor> doctorList = new ArrayList<Doctor>();
    ArrayList<Clinic> clinicList = new ArrayList<>();
    ArrayList<RDoctor> systemList = new ArrayList<>();
    private final String URL_SPECIALIZATION = User.URL + "spinner_specialization.php";
    private final String URL_DOCTOR = User.URL + "spinner_doctor.php";
    private final String URL_CLINIC = User.URL + "spinner_clinic.php";
    private final String URL_R_DOCTORS = User.URL + "getDoctors.php";
    private final String URL_ADD = User.URL+ "addAppointment.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        requestQueue = Volley.newRequestQueue(this);
        initiateComponents();
        initDatePicker();
        btnDate.setText(getTodayDate());
        generateDataForSpinners();

        Intent intent = getIntent();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFormValid()){
                    registerAppointment(createAppointment(),URL_ADD);
                    setResult(RESULT_OK,intent);
                }
            }
        });
    }


    private void generateDataForSpinners() {

        Clinic clinic = new Clinic(101, "Nothing selected");
        clinicList.add(clinic);

        generateRDoctors();
        generateClinicList();
        generateSpecializationList();
        generateDoctorList();

        ArrayAdapter<Clinic> adapterClinic = new ArrayAdapter<Clinic>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, clinicList);
        adapterClinic.notifyDataSetChanged();
        spinnerClinic.setAdapter(adapterClinic);


        spinnerClinic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Clinic selectedClinic = adapterClinic.getItem(position);
                int idSelected = selectedClinic.getId();

                if (idSelected == 101) {
                } else {


                    List<RDoctor> selectedList = systemList.stream().filter(p -> p.getIdClinic() == idSelected).collect(Collectors.toList());
                    if (!selectedList.isEmpty()) {

                        //Doctori clinica selectata
                        List<Doctor> selectedDoctors = doctorList.stream().filter(e -> selectedList.stream().map(RDoctor::getIdDoctor).anyMatch(idD -> idD.equals(e.getId())))
                                .collect(Collectors.toList());

                        //  Specializarile doctorilor din clinica
                        List<Specialization> selectedSpecializations = specializationList.stream().filter(e -> selectedDoctors.stream().map(Doctor::getIdSpecialization).anyMatch(idS -> idS.equals(e.getId())))
                                .collect(Collectors.toList());

                        ArrayAdapter<Specialization> adapterSpecialization =
                                new ArrayAdapter<Specialization>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, selectedSpecializations);
                        adapterSpecialization.notifyDataSetChanged();
                        spinnerSpecialization.setAdapter(adapterSpecialization);

                        spinnerSpecialization.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Specialization specializationSelected = adapterSpecialization.getItem(position);
                                int idSpc = specializationSelected.getId();

                                List<Doctor> selectedDoctorsWS = selectedDoctors.stream().filter(e -> e.getIdSpecialization() == idSpc).collect(Collectors.toList());

                                ArrayAdapter<Doctor> adapterDoctor = new ArrayAdapter<Doctor>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, selectedDoctorsWS);
                                adapterDoctor.notifyDataSetChanged();
                                spinnerDoctor.setAdapter(adapterDoctor);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initiateComponents() {
        btnDate = findViewById(R.id.button_datePicker);
        btnTime = findViewById(R.id.button_timePicker);
        spinnerClinic = findViewById(R.id.spinner_clinic);
        spinnerDoctor = findViewById(R.id.spinner_doctor);
        spinnerSpecialization = findViewById(R.id.spinner_specialization);
        etDetails = findViewById(R.id.etDetails);
        btnSend = findViewById(R.id.button_sendAppointment);
    }

    private String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = makeDateString(dayOfMonth, month, year);
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month - 1, dayOfMonth);
                int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    Toast.makeText(AddAppointmentActivity.this, R.string.select_weekday, Toast.LENGTH_SHORT).show();
                    return;
                }
                btnDate.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
    }

    private String makeDateString(int dayOfMonth, int month, int year) {
        return year + "-" + String.format(Locale.getDefault(), "%02d", month) + "-" + String.format(Locale.getDefault(), "%02d", dayOfMonth);
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }

    public void openTimePicker(View view) {
        Calendar currentTime = Calendar.getInstance();
        int hourOfDay = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);

                if (hourOfDay < 9 || hourOfDay >= 17) {
                    Toast.makeText(AddAppointmentActivity.this, R.string.select_time_from_interval, Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String formattedTime = timeFormat.format(selectedTime.getTime());
                    btnTime.setText(formattedTime);
                }
            }
        }, hourOfDay, minute, true);

        timePickerDialog.setTitle(R.string.select_time);
        timePickerDialog.show();
    }

    private void generateSpecializationList() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SPECIALIZATION, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("specialization");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        Specialization specialization = new Specialization(id, name);
                        specializationList.add(specialization);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void generateDoctorList() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_DOCTOR, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("doctor");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        int id = jsonObject.getInt("id");
                        String lastName = jsonObject.getString("lastName");
                        String firstName = jsonObject.getString("firstName");
                        int idSpecialization = jsonObject.getInt("id_specialization");
                        Doctor doctor = new Doctor(id, firstName, lastName, idSpecialization);
                        doctorList.add(doctor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void generateClinicList() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_CLINIC, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("clinic");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        Clinic clinic = new Clinic(id, name);
                        clinicList.add(clinic);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void generateRDoctors() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_R_DOCTORS, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("r_doctor");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        int idDoctor = jsonObject.getInt("id_doctor");
                        int idClinic = jsonObject.getInt("id_clinic");
                        RDoctor object = new RDoctor(idDoctor, idClinic);
                        systemList.add(object);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    private boolean isFormValid() {

        Clinic selectedClinic = (Clinic) spinnerClinic.getSelectedItem();
        Specialization selectedSpecialization = (Specialization) spinnerSpecialization.getSelectedItem();
        Doctor selectedDoctor = (Doctor) spinnerDoctor.getSelectedItem();

        if (selectedClinic.getId() == 101) {

            Toast.makeText(AddAppointmentActivity.this, R.string.select_clinic, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedSpecialization == null) {

            Toast.makeText(AddAppointmentActivity.this, R.string.select_specialization, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDoctor == null) {

            Toast.makeText(AddAppointmentActivity.this, R.string.select_doctor, Toast.LENGTH_SHORT).show();
            return false;
        }


        String selectedDate = btnDate.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(selectedDate);
            Date currentDate = new Date();

            if (date.before(currentDate)) {

                Toast.makeText(getApplicationContext(),R.string.future_date,Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Date date = sdf.parse(selectedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                Toast.makeText(AddAppointmentActivity.this, R.string.select_weekday, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String selectedTime = btnTime.getText().toString();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date time = timeFormat.parse(selectedTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            if (hourOfDay < 9 || hourOfDay >= 17) {
                Toast.makeText(AddAppointmentActivity.this, R.string.select_time_from_interval, Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return true;
    }

    private Appointment createAppointment(){
        Date appointementDate = DateConverter.fromString(btnDate.getText().toString());
        String appointmentTime =  btnTime.getText().toString();
        String details = etDetails.getText().toString();
        Clinic selectedClinic = (Clinic) spinnerClinic.getSelectedItem();
        Doctor selectedDoctor = (Doctor) spinnerDoctor.getSelectedItem();
        int idDoctor = selectedDoctor.getId();
        int idClinic = selectedClinic.getId();
        return new Appointment(appointementDate,appointmentTime,details,idDoctor,idClinic);
    }
    private void registerAppointment(Appointment appointment, String URL) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), R.string.error_data_adding, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE).getString("email", ""));
                map.put("date", DateConverter.fromDate(appointment.getAppointmentDate()));
                map.put("time", appointment.getAppointmentTime());
                map.put("details", appointment.getDetails());
                map.put("id_clinic", String.valueOf(appointment.getIdClinica()));
                map.put("id_doctor", String.valueOf(appointment.getIdDoctor()));
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}