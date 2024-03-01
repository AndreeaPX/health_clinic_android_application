package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SyncRequest;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.Illness;
import com.example.msl.Instance.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class addUserAffectionsActivity extends AppCompatActivity {

    TextView spinner_affection;
    EditText etDate;
    Button btnSend;
    ArrayList<String> illnessList = new ArrayList<>();
    Dialog dialog;
    RequestQueue requestQueue;
    private final String URL = User.URL+"spinner_populate_json.php";
    private final String URL_ADD = User.URL+"addUserAffection.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_affections);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        initComponents();
        generateDataForSpinner();
        spinner_affection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(addUserAffectionsActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(1000,1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText search = dialog.findViewById(R.id.editText_dialog_illness);
                ListView listView = dialog.findViewById(R.id.listView_illneses);
                ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, illnessList);
                listView.setAdapter(listAdapter);
                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        listAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        spinner_affection.setText(listAdapter.getItem(position));
                        dialog.dismiss();
                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    addIllness(URL_ADD);
                }
            }
        });
    }

    private Boolean validateData(){
        if(etDate.getText().toString().isEmpty() || etDate.getText().toString()==null){
            Toast.makeText(getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
            return false;
        }

        if(spinner_affection.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.select_illness,Toast.LENGTH_LONG).show();
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setLenient(false);
        try{
            Date data = format.parse(etDate.getText().toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),R.string.format_err,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void initComponents(){
        spinner_affection = findViewById(R.id.textView_illness);
        etDate = findViewById(R.id.editText_illness);
        btnSend = findViewById(R.id.button_illness);
    }

    private void generateDataForSpinner(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("illness");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String illnessName = jsonObject.getString("text");
                        illnessList.add(illnessName);
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

    private void addIllness(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
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
                Map<String, String> map = new HashMap<>();
                map.put("email",getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE).getString("email",""));
                map.put("text",spinner_affection.getText().toString().trim());
                map.put("startingDate",etDate.getText().toString().trim());
                return map;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}