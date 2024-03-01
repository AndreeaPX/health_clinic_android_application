package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.IllnessAdapter;
import com.example.msl.Instance.Illness;
import com.example.msl.Instance.User;
import com.example.msl.databinding.ActivityProfileBinding;
import com.example.msl.databinding.ActivityStatusBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StatusActivity extends DrawerActivity {

    ActivityStatusBinding activityStatusBinding;
    FloatingActionButton fabAdd;
    ListView lvStatus;
    List<Illness> illnessList = new ArrayList<>();
    RequestQueue requestQueue;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityStatusBinding = ActivityStatusBinding.inflate(getLayoutInflater());
        setContentView(activityStatusBinding.getRoot());
        generateActivityTitle("Status");

        fabAdd = findViewById(R.id.floatingActionButton_condition);
        lvStatus = findViewById(R.id.listView_status);
        sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String URL = User.URL + "createStatus.php?email=" + email + "";
        refreshStatus(URL);
        IllnessAdapter illnessAdapter = new IllnessAdapter(getApplicationContext(),
                R.layout.listview_illness_row, illnessList, getLayoutInflater());
        lvStatus.setAdapter(illnessAdapter);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), addUserAffectionsActivity.class), 200);
            }
        });
    }

    private void refreshStatus(String URL) {
        illnessList.clear();
        generateStatus(URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            String URL = User.URL + "createStatus.php?email=" + email + "";
            refreshStatus(URL);
        }
    }

    private void generateStatus(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response != null && response.length() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonObject = response.getJSONObject(i);
                            int id = jsonObject.getInt("id_illness");
                            String name = jsonObject.getString("text");
                            String category = jsonObject.getString("category");
                            String isRare = jsonObject.getString("IsRare");
                            int risk = jsonObject.getInt("Risk");
                            Illness illness = new Illness(id, name, category, isRare, risk);
                            if (!illnessList.contains(illness)) {
                                illnessList.add(illness);
                            }
                            Collections.sort(illnessList);
                            IllnessAdapter currentAdapter = (IllnessAdapter) lvStatus.getAdapter();
                            currentAdapter.notifyDataSetChanged();
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