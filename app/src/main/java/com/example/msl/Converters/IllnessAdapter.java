package com.example.msl.Converters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.Illness;
import com.example.msl.Instance.User;
import com.example.msl.LoginActivity;
import com.example.msl.R;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IllnessAdapter extends ArrayAdapter<Illness> {
    TextView tvText;
    Button btnDelete;
    private int resource;
    private List<Illness> objects;
    private LayoutInflater inflater;
    SharedPreferences sharedPreferences = getContext().getSharedPreferences(LoginActivity.SHAREDPREFERENCES,Context.MODE_PRIVATE);
    private String URL = User.URL + "deleteIllness.php";


    public IllnessAdapter(@NonNull Context context, int resource, @NonNull List<Illness> objects,
                          LayoutInflater inflater) {
        super(context, resource, objects);
        this.resource = resource;
        this.objects = objects;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = inflater.inflate(this.resource,parent,false);
        tvText = row.findViewById(R.id.textView_textIllness);
        btnDelete = row.findViewById(R.id.button_deleteIllness);
        Illness illness = objects.get(position);
        tvText.setText(illness.toString());
        if(illness.getRisk()>=3){
            row.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.RiskRed));
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteIllness(URL,position);
            }
        });
        return row;
    }


    private void deleteIllness(String URL, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(),R.string.success,Toast.LENGTH_LONG).show();
                objects.remove(position);
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();

            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", sharedPreferences.getString("email",""));
                String illnessName = objects.get(position).getName();
                map.put("illness",illnessName);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }


}
