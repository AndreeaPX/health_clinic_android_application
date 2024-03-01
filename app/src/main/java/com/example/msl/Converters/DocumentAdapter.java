package com.example.msl.Converters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.msl.Instance.Document;
import com.example.msl.Instance.Illness;
import com.example.msl.Instance.User;
import com.example.msl.LoginActivity;
import com.example.msl.R;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DocumentAdapter extends ArrayAdapter<Document> {

    TextView tvTitle;
    ImageButton btnDelete, btnViewBody;
    private int resource;
    private List<Document> objects;
    private LayoutInflater inflater;
    SharedPreferences sharedPreferences = getContext().getSharedPreferences(LoginActivity.SHAREDPREFERENCES,Context.MODE_PRIVATE);
    String URL = User.URL + "deleteDocument.php";
    TextToSpeech textToSpeech;


    public DocumentAdapter(@NonNull Context context, int resource,  @NonNull List<Document> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.resource = resource;
        this.objects=objects;
        this.inflater=inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       View row = inflater.inflate(this.resource,parent,false);
       tvTitle = row.findViewById(R.id.textView_textTitle);
       btnDelete = row.findViewById(R.id.imageButton_deleteDocument);
       btnViewBody = row.findViewById(R.id.imageButton_bodyDocument);
       Document document = objects.get(position);
       tvTitle.setText(document.getTitle());
       if(position%2==0){
           row.setBackgroundColor(ContextCompat.getColor(getContext(),R.color.grey));
       }
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDocument(URL, position);
            }
        });

       btnViewBody.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(row.getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Light));
               @SuppressLint("ViewHolder") View view = inflater.inflate(R.layout.dialog_document, null);
               EditText body = view.findViewById(R.id.editText_documentBody);
               Button btnCancel = view.findViewById(R.id.button_cancelDialogDocument);

               EditText search = view.findViewById(R.id.EditText_SearchInDocument);
               Document document1 = objects.get(position);
               body.setText(document1.getBody());
               String textSearch = body.getText().toString();

               MaterialButton btnRead = view.findViewById(R.id.button_startReading);
               MaterialButton btnStopReading = view.findViewById(R.id.button_stopReading);

               textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                   @Override
                   public void onInit(int status) {
                       if(status!=TextToSpeech.ERROR){
                           textToSpeech.setLanguage(Locale.US);
                       }else {
                           Toast.makeText(getContext(),R.string.connection_err,Toast.LENGTH_LONG).show();
                       }
                   }
               });

               btnRead.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String toSpeak  = body.getText().toString();
                       if(toSpeak.equals("")){
                           Toast.makeText(getContext(),R.string.read_err,Toast.LENGTH_LONG).show();
                       }else {
                           textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null);
                       }
                   }
               });

               btnStopReading.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if(textToSpeech.isSpeaking()){
                           textToSpeech.stop();
                       }else {
                           Toast.makeText(getContext(),R.string.sound_err,Toast.LENGTH_LONG).show();
                       }
                   }
               });


               builder.setView(view);
               AlertDialog dialog = builder.create();
               dialog.show();

               btnCancel.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       dialog.dismiss();
                   }
               });

               search.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                   }

                   @Override
                   public void onTextChanged(CharSequence s, int start, int before, int count) {
                       String stringKey = search.getText().toString().toLowerCase();
                       body.setText(textSearch);
                       if (!stringKey.isEmpty()) {
                           String textLowerCase = textSearch.toLowerCase();
                           int indexStart = textLowerCase.indexOf(stringKey);
                           while (indexStart >= 0) {
                               int indexEnd = indexStart + stringKey.length();
                               Spannable spannableString = new SpannableStringBuilder(body.getText());
                               spannableString.setSpan(new UnderlineSpan(), indexStart, indexEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                               spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), indexStart, indexEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                               body.setText(spannableString);
                               indexStart = textLowerCase.indexOf(stringKey, indexEnd);
                           }
                       }
                   }

                   @Override
                   public void afterTextChanged(Editable s) {
                   }
               });

           }
       });

        return row;
    }

    private void deleteDocument(String URL, int position) {
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
                map.put("id", id);
                return map;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }
}
