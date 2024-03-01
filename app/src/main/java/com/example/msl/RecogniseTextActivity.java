package com.example.msl;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Instance.Document;
import com.example.msl.Instance.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RecogniseTextActivity extends AppCompatActivity {

    MaterialButton btnInputImage, btnRecogniseText, btnSaveText;
    ShapeableImageView imageView;
    EditText etRecognisedText, etTitle;
    Uri uri = null;
    private static final int REQUEST_CAMERA_CODE=100;
    private static final int REQUEST_STORAGE_CODE=101;
    private static final String URL_SAVE_TEXT = User.URL+"addDocument.php";

    private String[] cameraPermissions;
    private String[] storagePermissions;

    ProgressDialog progressDialog;
    TextRecognizer textRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognise_text);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        initiateComponents();
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE};

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        btnInputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputImageDialog();
            }
        });

        btnRecogniseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri ==null){
                    Toast.makeText(getApplicationContext(),R.string.select_image_,Toast.LENGTH_LONG).show();
                }else {
                    recogniseTextFromImage();
                }
            }
        });


        Intent intent = getIntent();
        btnSaveText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    String title = generateTitle();
                    String body = etRecognisedText.getText().toString();
                    String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
                    Document document = new Document(title,body, DateConverter.fromString(currentDate));
                    addRaport(URL_SAVE_TEXT,document);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    private void recogniseTextFromImage() {
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(this, uri);
            progressDialog.setMessage("Recognising text...");

            Task<Text> textTask = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            progressDialog.dismiss();
                            String recognisedText = text.getText();
                            etRecognisedText.setText(recognisedText);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),  e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void addRaport(String URL, Document document) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_LONG).show();
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
                map.put("title",document.getTitle());
                map.put("body",document.getBody());
                map.put("date",DateConverter.fromDate(document.getUploadingDate()));
                return map;
            }
        };

       RequestQueue requestQueue = Volley.newRequestQueue(this);
       requestQueue.add(stringRequest);
    }


    private void initiateComponents(){
        btnInputImage=findViewById(R.id.btn_inputImage);
        btnRecogniseText=findViewById(R.id.btn_recogniseText);
        imageView=findViewById(R.id.iv_imageSelected);
        etRecognisedText=findViewById(R.id.et_recognisedText);
        etTitle=findViewById(R.id.et_getTitleForText);
        btnSaveText = findViewById(R.id.btnSaveText);
    }

    private String generateTitle(){
        String input=etTitle.getText().toString().trim().replaceAll("\\s+", " ");;
        String formattedInput = input.toLowerCase().replace(" ", "_");
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy", Locale.ENGLISH));
        String title = formattedInput + "_" + currentDate;
        return title;
    }

    private boolean validateData(){
        if(etTitle.getText().toString().isEmpty() || etTitle.getText().toString() ==null){
            Toast.makeText(getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
            return false;
        }
        if(etRecognisedText.getText().toString().isEmpty() || etRecognisedText.getText().toString() ==null){
            Toast.makeText(getApplicationContext(),R.string.error_data_adding,Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void showInputImageDialog() {
        PopupMenu popupMenu = new PopupMenu(this,btnInputImage);
        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id= item.getItemId();
                if(id==1){
                    if(checkCameraPermissions()){
                        pickImageCamera();
                    }else {
                        requestCameraPermission();
                    }
                }else if(id==2){
                    if(checkStoragePermissions()){
                        pickImageFromGallery();
                    }else {
                        requestStoragePermission();
                    }
                }
                return true;
            }
        });
    }

    private void pickImageFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        uri = data.getData();
                        imageView.setImageURI(uri);
                    }else {
                        Toast.makeText(getApplicationContext(),R.string.cancel_,Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private void pickImageCamera(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Sample");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Description");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==Activity.RESULT_OK){
                        imageView.setImageURI(uri);
                    }else {
                        Toast.makeText(getApplicationContext(),R.string.cancel_,Toast.LENGTH_LONG).show();
                    }
                }
            }
    );

    private boolean checkStoragePermissions(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean cameraResult = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean storageResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return cameraResult && storageResult;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,REQUEST_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CAMERA_CODE:{
                if(grantResults.length>0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.camera_permission, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            case REQUEST_STORAGE_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickImageFromGallery();
                    }else {
                        Toast.makeText(getApplicationContext(), R.string.storage_permission,Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }
}