package com.example.msl.Converters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.Appointment;
import com.example.msl.Instance.Medication;
import com.example.msl.Instance.Rating;
import com.example.msl.Instance.User;
import com.example.msl.LoginActivity;
import com.example.msl.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppointmentHistoryAdapter extends RecyclerView.Adapter<AppointmentHistoryAdapter.AppointmentViewHolder> {

    private ArrayList<Appointment> appointmentList = new ArrayList<>();
    private ArrayList<Rating> ratingList = new ArrayList<>();
    private Context context;
    //private SharedPreferences sharedPreferences = context.getSharedPreferences(LoginActivity.SHAREDPREFERENCES, Context.MODE_PRIVATE);

    public AppointmentHistoryAdapter(Context context, ArrayList<Appointment> appointmentList, ArrayList<Rating> ratingList){
        this.context = context;
        this.appointmentList = appointmentList;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rv_history_row,parent,false);
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
        holder.btnRating.setImageResource(R.drawable.star_icon);

        float ratingValue = getRatingValueForAppointment(appointment.getId());
        if (ratingValue != 0) {
            holder.tvRating.setText(String.valueOf(ratingValue) + " stars");
        } else {
            holder.tvRating.setText("No rating");
        }


        holder.btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context parentContext = v.getRootView().getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(parentContext, androidx.appcompat.R.style.Base_Theme_AppCompat_Light));
                View dialogView = LayoutInflater.from(parentContext).inflate(R.layout.dialog_review, null);
                Button btnSave, btnCancel;
                btnCancel = dialogView.findViewById(R.id.button_cancelRating);
                btnSave = dialogView.findViewById(R.id.button_saveRating);

                RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar_appointment);
                EditText etComment=dialogView.findViewById(R.id.editText_comment);

                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ratingBar.getRating()!= 0 ){
                            Appointment appointmentForRating = appointmentList.get(holder.getAdapterPosition());
                            Rating rating = new Rating(appointmentForRating.getId(), ratingBar.getRating(), etComment.getText().toString());

                            String URL_ADD = User.URL + "addRating.php";
                            addRating(URL_ADD,rating);
                            notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(context.getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }



    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public class AppointmentViewHolder extends RecyclerView.ViewHolder{
        TextView tvDate, tvTime, tvDoctor, tvClinic, tvAddress, tvSpecialization, tvDetails, tvRating;
        ImageButton btnRating;
        ImageView ivClinic;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.textView_appointmentDate_);
            tvTime = itemView.findViewById(R.id.textView_appointmentTime_);
            tvDetails = itemView.findViewById(R.id.textView_appointmentDetails_);
            tvDoctor = itemView.findViewById(R.id.textView_doctor_);
            tvClinic = itemView.findViewById(R.id.textView_clinic_);
            tvAddress = itemView.findViewById(R.id.textView_clinicAddress_);
            tvSpecialization = itemView.findViewById(R.id.textView_specialization_);
            btnRating = itemView.findViewById(R.id.imageButton_rating_);
            ivClinic = itemView.findViewById(R.id.imageView_clinic_);
            tvRating = itemView.findViewById(R.id.textView_rating_stars);
        }
    }

    private void addRating(String URL, Rating rating) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ratingList.add(rating);
                notifyDataSetChanged();
                Toast.makeText(context.getApplicationContext(), response, Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id_appointment", String.valueOf(rating.getIdAppointment()));
                map.put("rating_value",String.valueOf(rating.getRating()));
                map.put("comment",rating.getComment());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(request);
    }

    private float getRatingValueForAppointment(int appointmentId) {
        for (Rating rating : ratingList) {
            if (rating.getIdAppointment() == appointmentId) {
                return rating.getRating();
            }
        }
        return 0;
    }
}
