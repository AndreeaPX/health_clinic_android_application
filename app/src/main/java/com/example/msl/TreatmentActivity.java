package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Converters.IllnessAdapter;
import com.example.msl.Converters.MedicationAdapter;
import com.example.msl.Instance.Medication;
import com.example.msl.Instance.User;
import com.example.msl.databinding.ActivityMainBinding;
import com.example.msl.databinding.ActivityTreatmentBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TreatmentActivity extends DrawerActivity {

    ActivityTreatmentBinding activityTreatmentBinding;
    FloatingActionButton fabAdd;
    ListView lvTreatments;
    List<Medication> medicationList = new ArrayList<>();
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTreatmentBinding = ActivityTreatmentBinding.inflate(getLayoutInflater());
        setContentView(activityTreatmentBinding.getRoot());
        generateActivityTitle("Treatment");
        fabAdd = findViewById(R.id.floatingActionButton_addTreatment);
        lvTreatments=findViewById(R.id.listView_treatment);

        sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String URL = User.URL + "createTreatments.php?email=" + email + "";
        refreshTreatment(URL);
        MedicationAdapter medicationAdapter = new MedicationAdapter(getApplicationContext(),
                R.layout.listview_medication_row, medicationList, getLayoutInflater());
        lvTreatments.setAdapter(medicationAdapter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddTreatmentActivity.class), 200);
            }
        });
    }

    private void refreshTreatment(String URL) {
        medicationList.clear();
        generateTreatments(URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            String URL = User.URL + "createTreatments.php?email=" + email + "";
            refreshTreatment(URL);
        }
    }

    private void generateTreatments(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null ) {
                    JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id_medication");
                            String name = jsonObject.getString("drugs");
                            Date startingDate = DateConverter.fromString(jsonObject.getString("startingDate"));
                            float dosage = Float.parseFloat(String.valueOf(jsonObject.get("dosage")));
                            String timesADay = jsonObject.getString("times");
                            String interval = jsonObject.getString("dates");
                            Medication medication = new Medication(id, name, startingDate, dosage, timesADay, interval);
                            if (!medicationList.contains(medication)) {
                                medicationList.add(medication);
                            }
                            Collections.sort(medicationList);
                            MedicationAdapter adapter = (MedicationAdapter) lvTreatments.getAdapter();
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }}}}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }



}