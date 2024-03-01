package com.example.msl.Converters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.Appointment;
import com.example.msl.Instance.User;
import com.example.msl.LoginActivity;
import com.example.msl.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private ArrayList<Appointment> appointmentList = new ArrayList<>();
    private Context context;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointmentList){
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_appiontmanet_row,parent,false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.tvDate.setText(DateConverter.fromDate(appointment.getAppointmentDate()));
        holder.tvTime.setText(appointment.getAppointmentTime());
        if (TextUtils.isEmpty(appointment.getDetails())) {
            holder.tvDetails.setText(context.getString(R.string.no_details));
        } else {
            holder.tvDetails.setText(appointment.getDetails());
        }
        holder.tvClinic.setText(appointment.getClinic());
        holder.tvSpecialization.setText(appointment.getSpecialization());
        holder.tvDoctor.setText(appointment.getDoctor());
        holder.tvAddress.setText(appointment.getAddress());
        holder.btnDelete.setImageResource(R.drawable.ic_delete);
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointment(User.URL + "deleteAppointment.php", holder.getAdapterPosition());
            }
        });
    }

    private void deleteAppointment(String URL, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, "Done!", Toast.LENGTH_LONG).show();
                appointmentList.remove(position);
                notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context.getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.SHAREDPREFERENCES, Context.MODE_PRIVATE);
                map.put("email", sharedPreferences.getString("email", ""));
                String id = String.valueOf(appointmentList.get(position).getId());
                map.put("id", id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate, tvTime, tvDoctor, tvClinic, tvAddress, tvSpecialization, tvDetails;
        ImageButton btnDelete;
        ImageView ivClinic;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.textView_appointmentDate);
            tvTime = itemView.findViewById(R.id.textView_appointmentTime);
            tvDetails = itemView.findViewById(R.id.textView_appointmentDetails);
            tvDoctor = itemView.findViewById(R.id.textView_doctor);
            tvClinic = itemView.findViewById(R.id.textView_clinic);
            tvAddress = itemView.findViewById(R.id.textView_clinicAddress);
            tvSpecialization = itemView.findViewById(R.id.textView_specialization);
            btnDelete = itemView.findViewById(R.id.imageButton_cancelAppointment);
            ivClinic = itemView.findViewById(R.id.imageView_clinic);
        }
    }
}
