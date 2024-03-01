package com.example.msl;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Converters.AppointmentHistoryAdapter;
import com.example.msl.Converters.DateConverter;
import com.example.msl.Converters.DocumentAdapter;
import com.example.msl.Converters.DocumentSearchAdapter;
import com.example.msl.Converters.IllnessAdapter;
import com.example.msl.Instance.Appointment;
import com.example.msl.Instance.Document;
import com.example.msl.Instance.Illness;
import com.example.msl.Instance.User;
import com.example.msl.databinding.ActivityFilesBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilesActivity extends DrawerActivity {

    ActivityFilesBinding activityFilesBinding;
    FloatingActionButton fabAdd ;
    ListView listView;
    TextView spinnerSearch;
    Dialog dialog;
    List<Document> documentList = new ArrayList<>();
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityFilesBinding = ActivityFilesBinding.inflate(getLayoutInflater());
        setContentView(activityFilesBinding.getRoot());
        generateActivityTitle("Medical Files");
        fabAdd = findViewById(R.id.floatingActionButton_addPdf);
        listView = findViewById(R.id.listView_documents);
        spinnerSearch = findViewById(R.id.textView_searchFile);

        sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String URL = User.URL + "createMedicalHistory.php?email=" + email + "";

        generateDocuments(URL);
        DocumentAdapter documentAdapter = new DocumentAdapter(getApplicationContext(),
                R.layout.listview_document, documentList, getLayoutInflater());
        listView.setAdapter(documentAdapter);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), RecogniseTextActivity.class), 200);
            }
        });

        spinnerSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(FilesActivity.this);
                dialog.setContentView(R.layout.dialog_searchable_documents);
                dialog.getWindow().setLayout(1000, 1000);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                EditText search = dialog.findViewById(R.id.editText_dialog_document);
                ListView listView = dialog.findViewById(R.id.listView_documentsToSearch);
                Button button = dialog.findViewById(R.id.button_searchDocument);
                ArrayAdapter<Document> listAdapter = new DocumentSearchAdapter(getApplicationContext(), documentList);
                listView.setAdapter(listAdapter);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String searchText = search.getText().toString().trim();
                        List<Document> filteredDocuments = filterDocumentsByBody(searchText);
                        updateDocumentsList(filteredDocuments);
                        dialog.dismiss();
                    }
                });

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
                        Document document = listAdapter.getItem(position);
                        if (document != null) {
                            spinnerSearch.setText(document.getTitle());
                            List<Document> filteredDocuments = new ArrayList<>();
                            filteredDocuments.add(document);
                            updateDocumentsList(filteredDocuments);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK) {
            sharedPreferences = getSharedPreferences(LoginActivity.SHAREDPREFERENCES, MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "");
            String URL = User.URL + "createMedicalHistory.php?email=" + email + "";
            generateDocuments(URL);
        }}


    private void generateDocuments(String URL){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(response!=null ){
                    JSONObject jsonObject = new JSONObject();
                    for(int i=0;i<response.length();i++){
                        try {
                            jsonObject=response.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String title = jsonObject.getString("title");
                            String body = jsonObject.getString("body");
                            int userId = jsonObject.getInt("user_id");
                            Date uploadingData = DateConverter.fromString(jsonObject.getString("upload_date"));
                            Document document = new Document(title,body,uploadingData,id,userId);
                            if (!documentList.contains(document)) {
                                documentList.add(document);
                            }

                            DocumentAdapter currentAdapter = (DocumentAdapter) listView.getAdapter();
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
                // Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String utf8String = new String(response.data, "UTF-8");
                    JSONArray jsonArray = new JSONArray(utf8String);
                    return Response.success(jsonArray, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void updateDocumentsList(List<Document> filteredDocuments) {
        DocumentAdapter documentAdapter = new DocumentAdapter(getApplicationContext(),
                R.layout.listview_document, filteredDocuments, getLayoutInflater());
        listView.setAdapter(documentAdapter);
    }

    private List<Document> filterDocumentsByBody(String searchText) {
        List<Document> filteredList = new ArrayList<>();
        for (Document document : documentList) {
            if (document.getBody().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(document);
            }
        }
        return filteredList;
    }
}