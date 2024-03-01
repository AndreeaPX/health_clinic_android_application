package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
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
import com.example.msl.Converters.DateConverter;
import com.example.msl.Instance.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddTreatmentActivity extends AppCompatActivity {

    TextView spinnerMedication;
    EditText etDate, etDosage;
    ArrayList<String> medicationList = new ArrayList<>();
    Dialog dialog;
    RequestQueue requestQueue;
    private final String URL_SPINNER = User.URL + "spinner_meds.php";
    private final String URL_ADD = User.URL + "addUserMedication.php";
    Button btnSend;
    Spinner spinnerTimesADay, spinnerInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_treatment);
        initiateComponents();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        generateDataForSpinner();
        spinnerMedication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(AddTreatmentActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_spinner);
                dialog.getWindow().setLayout(1000, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText search = dialog.findViewById(R.id.editText_dialog_illness);
                ListView listView = dialog.findViewById(R.id.listView_illneses);
                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, medicationList);
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
                        spinnerMedication.setText(listAdapter.getItem(position));
                        dialog.dismiss();
                    }
                });
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    addMedicine(URL_ADD);
                }
            }
        });
    }

    private void initiateComponents() {
        spinnerMedication = findViewById(R.id.textView_medicine);
        etDate = findViewById(R.id.EditText_startingDateMedicine);
        etDosage = findViewById(R.id.EditText_dosage);
        btnSend = findViewById(R.id.button_sendMedicine);
        spinnerInterval = findViewById(R.id.spinner_interval);
        spinnerTimesADay = findViewById(R.id.spinner_feq);
        ArrayAdapter<CharSequence> adapterInterval = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_interval, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerInterval.setAdapter(adapterInterval);
        ArrayAdapter<CharSequence> adapterFreq = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_times_a_day, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerTimesADay.setAdapter(adapterFreq);
    }

    private Boolean validateData() {
        if (etDate.getText().toString().isEmpty() || etDate.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), R.string.error_data_adding, Toast.LENGTH_LONG).show();
            return false;
        }
        if (etDosage.getText().toString().isEmpty() || etDosage.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), R.string.error_data_adding, Toast.LENGTH_LONG).show();
            return false;
        }
        if(spinnerMedication.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(),R.string.select_medicine_,Toast.LENGTH_LONG).show();
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.US);
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

    private void generateDataForSpinner() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_SPINNER, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("medication");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String illnessName = jsonObject.getString("drugs");
                        medicationList.add(illnessName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void addMedicine(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("email", getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE).getString("email", ""));
                map.put("drugs", spinnerMedication.getText().toString().trim());
                map.put("startingDate", etDate.getText().toString().trim());
                map.put("dosage", etDosage.getText().toString().trim());
                map.put("times", spinnerTimesADay.getSelectedItem().toString());
                map.put("dates", spinnerInterval.getSelectedItem().toString());
                return map;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


    }
}