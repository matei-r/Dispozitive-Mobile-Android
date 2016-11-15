package com.example.matei.lab_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    TextView welcomeMessage;
    ListView instrumentListView;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        welcomeMessage = (TextView) findViewById(R.id.textViewName);
        instrumentListView = (ListView) findViewById(R.id.listViewInstruments);

        Intent profileIntent = getIntent();
        String name = profileIntent.getStringExtra("name");
        id = profileIntent.getStringExtra("id");

        String message = "Welcome to your profile " + name + "!";
        welcomeMessage.setText(message);

        //loadList();
    }

    private void loadList() {
        final ArrayList<String> list = new ArrayList<String>();

        String LOGIN_URL = "http://192.168.1.102/am/getInstruments.php";
        Map<String,String> params = new HashMap<String,String>();
        params.put("id",id);

        CustomRequestJsonArray jsonArrayRequest = new CustomRequestJsonArray(Request.Method.POST,LOGIN_URL,null,params,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i = 0;i < response.length();i++) {
                                JSONObject instrument = (JSONObject) response.get(i);
                                String name = instrument.getString("name");
                                String type = instrument.getString("type");
                                String item = name + " " + type;
                                list.add(item);
                            }
                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                            builder.setMessage(e.toString())
                                    .setNegativeButton("Retry",null)
                                    .create()
                                    .show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                        builder.setMessage(error.toString())
                                .setNegativeButton("Retry",null)
                                .create()
                                .show();
                    }
                });
        Volley.newRequestQueue(this).add(jsonArrayRequest);

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);

        instrumentListView.setAdapter(adapter);
        instrumentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
            }
        });
    }
}
