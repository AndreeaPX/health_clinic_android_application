package com.example.msl.Converters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.Medication;
import com.example.msl.Instance.User;
import com.example.msl.LoginActivity;
import com.example.msl.R;
import com.example.msl.TreatmentActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MedicationAdapter extends ArrayAdapter<Medication> {

    TextView tvName, tvDate, tvDose, tvInterval, tvTimes;
    ImageButton btnEdit, btnDelete;
    private int resource;
    private List<Medication> objects;
    private LayoutInflater inflater;
    SharedPreferences sharedPreferences = getContext().getSharedPreferences(LoginActivity.SHAREDPREFERENCES, Context.MODE_PRIVATE);
    private String URL = User.URL + "deleteTreatment.php";
    private String URL_EDIT = User.URL  + "editTreatment.php";

    public MedicationAdapter(@NonNull Context context, int resource, @NonNull List<Medication> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.resource = resource;
        this.objects = objects;
        this.inflater = inflater;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = inflater.inflate(this.resource, parent, false);
        tvName = row.findViewById(R.id.textView_medicationName);
        tvDate = row.findViewById(R.id.textView_medicationDate);
        tvDose = row.findViewById(R.id.textView_medicationDose);
        tvInterval = row.findViewById(R.id.textView_interval);
        tvTimes = row.findViewById(R.id.textView_times);
        btnDelete = row.findViewById(R.id.imageButton_delete);
        btnEdit = row.findViewById(R.id.imageButton_edit);
        Medication medication = objects.get(position);
        tvName.setText(medication.getName());
        tvDate.setText(DateConverter.fromDate(medication.getStartingDate()));
        tvDose.setText(String.valueOf(medication.getDose()) + " mg/day");
        tvInterval.setText(medication.getInterval());
        tvTimes.setText(medication.getTimesADay());

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletMedication(URL, position);
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(row.getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Light));
                @SuppressLint("ViewHolder") View mview = inflater.inflate(R.layout.dialog_edit_treatment, null);
                TextView medicationName = (TextView) mview.findViewById(R.id.textView_dialogMedName);
                EditText medicationDosage = (EditText) mview.findViewById(R.id.editText_editDosage);
                EditText medicationDate = (EditText) mview.findViewById(R.id.editText_editDate);
                Spinner medicationSpinnerTimes = (Spinner) mview.findViewById(R.id.spinner2_editTimes);
                Spinner medicationSpinnerInterval = (Spinner) mview.findViewById(R.id.spinner_editInterval);
                Button btnSave = (Button) mview.findViewById(R.id.button_editTreatment);
                Button btnCancel = (Button) mview.findViewById(R.id.button_cancel);

                Medication medication = objects.get(position);
                medicationName.setText(medication.getName());
                medicationDosage.setText(String.valueOf(medication.getDose()));
                medicationDate.setText(DateConverter.fromDate(medication.getStartingDate()));
                ArrayAdapter<CharSequence> adapterInterval = ArrayAdapter.createFromResource(getContext(), R.array.spinner_interval, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                medicationSpinnerInterval.setAdapter(adapterInterval);
                int spinnerPosition = adapterInterval.getPosition(medication.getInterval());
                medicationSpinnerInterval.setSelection(spinnerPosition);
                ArrayAdapter<CharSequence> adapterFreq = ArrayAdapter.createFromResource(getContext(), R.array.spinner_times_a_day, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
                medicationSpinnerTimes.setAdapter(adapterFreq);
                spinnerPosition = adapterFreq.getPosition(medication.getTimesADay());
                medicationSpinnerTimes.setSelection(spinnerPosition);

                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!medicationDosage.getText().toString().isEmpty() && !medicationDate.getText().toString().isEmpty()){
                            String inputDate = medicationDate.getText().toString().trim();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                            try {
                                Date dateToSave = dateFormat.parse(inputDate);
                                medication.setDose(Float.parseFloat(medicationDosage.getText().toString().trim()));
                                medication.setStartingDate(dateToSave);
                                medication.setTimesADay(medicationSpinnerTimes.getSelectedItem().toString());
                                medication.setInterval(medicationSpinnerInterval.getSelectedItem().toString());
                                editMedication(URL_EDIT,medication);
                                objects.set(position, medication);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }catch (ParseException e){
                                Toast.makeText(getContext(),R.string.format_err,Toast.LENGTH_SHORT).show();
                            }


                        }
                        else {
                            Toast.makeText(getContext(), R.string.error_data_adding, Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        return row;
    }

    private void deletMedication(String URL, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), R.string.success, Toast.LENGTH_LONG).show();
                objects.remove(position);
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();

            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email", sharedPreferences.getString("email", ""));
                String id = String.valueOf(objects.get(position).getId());
                map.put("id_medication", id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }


    private void editMedication(String URL, Medication medication) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), R.string.success, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("email",sharedPreferences.getString("email",""));
                map.put("id", String.valueOf(medication.getId()));
                map.put("interval",medication.getInterval());
                map.put("times", medication.getTimesADay());
                map.put("startingDate",DateConverter.fromDate(medication.getStartingDate()));
                map.put("dosage",String.valueOf(medication.getDose()));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }


}
