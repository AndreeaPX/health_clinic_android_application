package com.example.msl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.AppointmentAdapter;
import com.example.msl.Converters.AppointmentHistoryAdapter;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Instance.Appointment;
import com.example.msl.Instance.Rating;
import com.example.msl.Instance.User;
import com.example.msl.databinding.ActivityHistoryBinding;
import com.example.msl.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends DrawerActivity {

   ActivityHistoryBinding activityHistoryBinding;
   RecyclerView recyclerView;
   SharedPreferences sharedPreferences;
   RequestQueue requestQueue;
   ArrayList<Appointment> appointmentList = new ArrayList<>();
   ArrayList<Rating> ratingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHistoryBinding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(activityHistoryBinding.getRoot());
        generateActivityTitle("History");
        recyclerView =findViewById(R.id.recycleView_history);

        sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String URL = User.URL + "createHistory.php?email=" + email + "";
        String URL_RATING = User.URL + "createRating.php?email=" + email+"";
        generateAppointments(URL);

        generateRatings(URL_RATING);
        AppointmentHistoryAdapter appointmentAdapter = new AppointmentHistoryAdapter(getApplicationContext(), appointmentList, ratingsList);
        recyclerView.setAdapter(appointmentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void generateAppointments(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null ){
                    JSONObject jsonObject = new JSONObject();
                    for(int i=0;i<response.length();i++){
                        try {
                            jsonObject=response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String time = jsonObject.getString("time");
                            Date date = DateConverter.fromString(jsonObject.getString("date"));
                            String details = jsonObject.getString("details");
                            String lastName = jsonObject.getString("lastName");
                            String firstName = jsonObject.getString("firstName");
                            String name = jsonObject.getString("name");
                            String address = jsonObject.getString("address");
                            String specialization = jsonObject.getString("specializationName");
                            Appointment appointment = new Appointment(id,date, time, details, lastName+" - "+firstName,name,address,specialization);
                            if(!appointmentList.contains(appointment)){
                                appointmentList.add(appointment);
                            }
                            AppointmentHistoryAdapter adapter = (AppointmentHistoryAdapter) recyclerView.getAdapter();
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
                // Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void generateRatings(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null ){
                    JSONObject jsonObject = new JSONObject();
                    for(int i=0;i<response.length();i++){
                        try {
                            jsonObject=response.getJSONObject(i);
                            int idAppointment = jsonObject.getInt("id_appointment");
                            String commnet = jsonObject.getString("comment");
                            float ratingValue = (float) jsonObject.getDouble("rating_value");
                            Rating rating = new Rating(idAppointment,ratingValue,commnet);
                            if(!ratingsList.contains(rating)){
                                ratingsList.add(rating);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        AppointmentHistoryAdapter adapter = (AppointmentHistoryAdapter) recyclerView.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             //    Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


}