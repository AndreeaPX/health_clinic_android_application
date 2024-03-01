package com.example.msl;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Instance.User;
import com.example.msl.databinding.ActivityProfileBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends DrawerActivity {

    ActivityProfileBinding activityProfileBinding;
    TextView tvUserName, tvEmail, tvPhone, tvAddress, tvBirthday, tvOccupation, tvGender, tvCity;
    Button btnEdit, btnDelete;
    ImageButton btnUploadPicture;
    SharedPreferences sharedPreferences;
    ImageView ivProfile;
    private final String URL_EDIT = User.URL+"editProfile.php";
    private final String URL_DELETE = User.URL+"deleteUser.php";
    private final String URL_PHOTO = User.URL+"uploadPicture.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(activityProfileBinding.getRoot());
        generateActivityTitle("Profile");
        initiateComponents();
        sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString("email","");
        String URL = User.URL+"/createProfile.php?email="+email+"";
        generateProfile(URL);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile(createUser(), URL_EDIT, email);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile(URL_DELETE);
            }
        });

        btnUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePhoto();
            }
        });

    }

    private void initiateComponents(){
        tvUserName = findViewById(R.id.textView_profileUserName);
        tvEmail = findViewById(R.id.textView_email);
        tvPhone = findViewById(R.id.textView_phone);
        tvAddress = findViewById(R.id.textView_address);
        tvBirthday=findViewById(R.id.textView_birthday);
        tvOccupation=findViewById(R.id.textView_occupation);
        tvGender=findViewById(R.id.textView_gender);
        tvCity=findViewById(R.id.textView_city);
        btnEdit=findViewById(R.id.button_edit);
        btnDelete=findViewById(R.id.button_delete);
        btnUploadPicture = findViewById(R.id.imageButton_uploadPicture);
        ivProfile = findViewById(R.id.imageView_profile);
    }

    private void refreshProfile(String URL) {
        clearProfileData();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                updateProfileData(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void clearProfileData() {
        tvUserName.setText("");
        tvEmail.setText("");
        tvPhone.setText("");
        tvCity.setText("");
        tvAddress.setText("");
        tvBirthday.setText("");
        tvGender.setText("");
        tvOccupation.setText("");
        ivProfile.setImageResource(R.drawable.person_icon);
    }

    private void updateProfileData(JSONArray response) {
        try {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < response.length(); i++) {
                jsonObject = response.getJSONObject(i);
                String userName = jsonObject.getString("lastName") + " " + jsonObject.getString("firstName");
                tvUserName.setText(userName);
                tvEmail.setText(jsonObject.getString("email"));
                tvPhone.setText(jsonObject.getString("phone"));
                tvCity.setText(jsonObject.getString("city"));
                tvAddress.setText(jsonObject.getString("address"));
                tvBirthday.setText(jsonObject.getString("birthday"));
                tvGender.setText(jsonObject.getString("gender"));
                tvOccupation.setText(jsonObject.getString("occupation"));

                String photo = jsonObject.getString("picture");
                if (!photo.isEmpty()) {
                    Picasso.get().load(photo).into(ivProfile);
                }
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String email = sharedPreferences.getString("email", "");
        String URL = User.URL + "/createProfile.php?email=" + email + "";
        refreshProfile(URL);
    }

    private void generateProfile(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = new JSONObject();
                for(int i=0;i<response.length();i++){
                    try{
                        jsonObject = response.getJSONObject(i);
                        String userName = jsonObject.getString("lastName")+ " " + jsonObject.getString("firstName");
                        tvUserName.setText(userName);
                        tvEmail.setText(jsonObject.getString("email"));
                        tvPhone.setText(jsonObject.getString("phone"));
                        tvCity.setText(jsonObject.getString("city"));
                        tvAddress.setText(jsonObject.getString("address"));
                        tvBirthday.setText(jsonObject.getString("birthday"));
                        tvGender.setText(jsonObject.getString("gender"));
                        tvOccupation.setText(jsonObject.getString("occupation"));
                        String photo = jsonObject.getString("picture");
                        if(!photo.isEmpty()){
                            Picasso.get().load(photo).into(new Target(){
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    ivProfile.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                    Toast.makeText(getApplicationContext(), R.string.picture_err, Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    ivProfile.setImageResource(R.drawable.person_icon);
                                }
                            });
                        }
                    }catch (JSONException e){
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        Log.e("MYAPP", "exception", e);
                    }
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private User createUser(){
            String email = tvEmail.getText().toString();
            String password =  sharedPreferences.getString("password","");
            String name = tvUserName.getText().toString();
            int init = name.indexOf(" ");
            int length = name.length();
            String lastName = name.substring(0,init).trim();
            String firstName = name.substring(init,length).trim();
            String phone = tvPhone.getText().toString();
            String address = tvAddress.getText().toString();
            Date birthday = DateConverter.fromString(tvBirthday.getText().toString().trim());
            String gender = tvGender.getText().toString();
            String occupation = tvOccupation.getText().toString();
            String city = tvCity.getText().toString();
            return new User(email, password, firstName, lastName, phone, address, birthday, gender, occupation, city);
    }

    private void editProfile(User user, String URL, String email) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    if(!Objects.equals(response, "1062")) {
                        sharedPreferences.edit().putString("email", tvEmail.getText().toString().trim()).apply();
                        Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(),R.string.email_saved , Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("email_shared", user.getEmail());
                map.put("email", email);
                map.put("password", user.getPassword());
                map.put("firstName", user.getFirstName());
                map.put("lastName", user.getLastName());
                map.put("phone", user.getPhone());
                map.put("address", user.getAddress());
                map.put("gender", user.getGender());
                map.put("birthday", DateConverter.fromDate(user.getBirthday()));
                map.put("occupation", user.getOccupation());
                map.put("city", user.getCity());
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void deleteProfile(String URL) {
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sharedPreferences.edit().clear().apply();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                map.put("email", sharedPreferences.getString("email",""));
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void choosePhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

        sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString("email","");
        String URL = User.URL+"/createProfile.php?email="+email+"";
        refreshProfile(URL);
        generateProfile(URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            uploadPicture(filePath);
        }
    }

    private void uploadPicture(Uri filePath) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        Bitmap bitmap;

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            final String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_PHOTO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String success = jsonObject.getString("success");
                                if (success.equals("1")) {
                                    Toast.makeText(getApplicationContext(), R.string.picture_saved, Toast.LENGTH_LONG).show();
                                    Picasso.get().load(filePath).into(ivProfile);
                                    sharedPreferences.edit().putString("profilePicture", filePath.toString()).apply();
                                } else {
                                    Toast.makeText(getApplicationContext(), R.string.picture_err_upload, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                               // e.printStackTrace();
                               // Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                           // Toast.makeText(getApplicationContext(), R.string.try_again, Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    String email = sharedPreferences.getString("email", "");
                    map.put("email", email);
                    map.put("photo", imageString);
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), R.string.picture_err_upload, Toast.LENGTH_LONG).show();
        }
    }

}