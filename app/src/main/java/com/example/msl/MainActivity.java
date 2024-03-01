package com.example.msl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.AppointmentAdapter;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Converters.MedicationAdapter;
import com.example.msl.Instance.Appointment;
import com.example.msl.Instance.Medication;
import com.example.msl.Instance.User;
import com.example.msl.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends DrawerActivity {

    ActivityMainBinding activityMainBinding;
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView ;
    ArrayList<Appointment> appointmentList = new ArrayList<>();
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        generateActivityTitle("Home");
        floatingActionButton=findViewById(R.id.floatingActionButton_main);
        recyclerView =findViewById(R.id.recycleView_main);

        sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String URL = User.URL + "createAppointments.php?email=" + email + "";
        generateAppointments(URL);

        AppointmentAdapter appointmentAdapter = new AppointmentAdapter(getApplicationContext(), appointmentList);
        recyclerView.setAdapter(appointmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddAppointmentActivity.class);
                startActivityForResult(intent,200);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            String URL = User.URL + "createAppointments.php?email=" + email + "";
            generateAppointments(URL);
        }
    }


    private void generateAppointments(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null) {
                    JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String time = jsonObject.getString("time");
                            Date date = DateConverter.fromString(jsonObject.getString("date"));
                            String details = jsonObject.getString("details");
                            String lastName = jsonObject.getString("lastName");
                            String firstName = jsonObject.getString("firstName");
                            String name = jsonObject.getString("name");
                            String address = jsonObject.getString("address");
                            String specialization = jsonObject.getString("specializationName");
                            Appointment appointment = new Appointment(id, date, time, details, lastName + " - " + firstName, name, address, specialization);
                            if (!appointmentList.contains(appointment)) {
                                appointmentList.add(appointment);
                            }
                            AppointmentAdapter adapter = (AppointmentAdapter) recyclerView.getAdapter();
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

}