package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText searchEdtTxt;
    TextView wordTxtView;
    TextView phoneticTxtView;
    ListView meaningsLstView;
    TextView synonymsTxtView;
    TextView meaningfulnessTxtView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEdtTxt = (EditText) findViewById(R.id.search);
                wordTxtView = (TextView) findViewById(R.id.word);
                phoneticTxtView = (TextView) findViewById(R.id.phonetic);
                synonymsTxtView = (TextView) findViewById(R.id.synonyms);
                meaningfulnessTxtView = (TextView) findViewById(R.id.num);

                String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";
                String searchedWord = searchEdtTxt.getText().toString().toLowerCase();
                String URL = API_URL.concat(searchedWord);
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        try {
                            JSONObject resObj = responseArray.getJSONObject(0);
                            String word = resObj.getString("word");
                            wordTxtView.setText(word);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error fetching word", Toast.LENGTH_LONG).show();
                        }try{
                            JSONObject resObj = responseArray.getJSONObject(0);
                            String phonetic = resObj.getString("phonetic");
                            phoneticTxtView.setText(phonetic);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error fetching phonetic", Toast.LENGTH_LONG).show();
                        }try{
                            JSONObject resObj = responseArray.getJSONObject(0);
                            JSONArray resArr = resObj.getJSONArray("meanings");
                            JSONObject meaningObj = resArr.getJSONObject(0);
                            JSONArray defArr = meaningObj.getJSONArray("definitions");

                            List<String> pos = new ArrayList<String>();
                            for(int i=0;i<defArr.length();i++){
                                try{
                                    JSONObject defObj = defArr.getJSONObject(i);
                                    String def = defObj.getString("definition");
                                    pos.add(def);
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(), "Error fetching definitions", Toast.LENGTH_LONG).show();
                                }
                            }
                               ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.activity_meanings_list_view, R.id.meaning, pos);
                               meaningsLstView = findViewById(R.id.meanings);
                               meaningsLstView.setAdapter(arrayAdapter);
                               meaningfulnessTxtView.setText(pos.size()+" meanings");

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Error fetching meanings", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Cannot fetch data", Toast.LENGTH_LONG).show();
                    }
                });
                requestQueue.add(request);
            }
        });
    }
}