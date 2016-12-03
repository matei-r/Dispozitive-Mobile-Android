package com.example.matei.lab_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;

public class UserProfileActivity extends AppCompatActivity {
    TextView welcomeMessage,logout;
    ListView instrumentListView;
    ArrayList<Instrument> instrumentList;
    String id;
    String name;
    Button deleteBtn;
    int selected = 0;
    ArrayList<String> idSelected = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        welcomeMessage = (TextView) findViewById(R.id.textViewName);
        deleteBtn = (Button) findViewById(R.id.deleteButton);

        Intent profileIntent = getIntent();
        name = profileIntent.getStringExtra("name");
        id = profileIntent.getStringExtra("id");

        String message = "Welcome to your profile " + name + "!";
        welcomeMessage.setText(message);

        instrumentListView = (ListView) findViewById(R.id.listview);

        loadList();

        instrumentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView ctv = (CheckedTextView) view.findViewById(R.id.checkTextView1);
                Instrument i = (Instrument) parent.getItemAtPosition(position);
                if(ctv.isChecked()){
                    selected++;
                    idSelected.add(String.valueOf(i.getId()));
                } else {
                    selected--;
                    idSelected.remove(String.valueOf(i.getId()));
                }
                deleteBtn.setText(String.format("Delete selected (%d)",selected));
            }
        });

        instrumentListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long idq) {
                Instrument i = (Instrument) parent.getItemAtPosition(position);
                Intent intent = new Intent(UserProfileActivity.this,UpdateActivity.class);
                intent.putExtra("id",String.valueOf(i.getId()));
                intent.putExtra("name",i.getName());
                intent.putExtra("type",i.getType());
                intent.putExtra("user_id",id);
                intent.putExtra("user_name",name);
                startActivity(intent);
                return true;
            }
        });

    }

    private void loadList() {

        instrumentList = new ArrayList<Instrument>();

        String GET_URL = "http://192.168.1.101/am/getInstruments.php";
        Map<String,String> params = new HashMap<String,String>();
        params.put("id",id);


        CustomRequest jsonArrayRequest = new CustomRequest(Request.Method.POST,GET_URL,params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("data");
                            for(int i = 0;i < arr.length();i++) {
                                JSONObject instrument = (JSONObject) arr.get(i);
                                String name = instrument.getString("name");
                                String type = instrument.getString("type");
                                int id = instrument.getInt("id");
                                instrumentList.add(new Instrument(id,name,type));
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

        jsonArrayRequest.setShouldCache(false);
        Volley.newRequestQueue(UserProfileActivity.this).add(jsonArrayRequest);
        InstrumentAdapter adapter = new InstrumentAdapter(this,R.layout.list_item,instrumentList);
        instrumentListView.setAdapter(adapter);
        instrumentListView.setItemsCanFocus(false);
        instrumentListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    public void DeleteSelected(View view){
        String getSelected = "";
        for(int i=0;i<idSelected.size();i++){
            getSelected += idSelected.get(i) + ";";
        }
        if(idSelected.size() > 0) {

            getSelected = getSelected.substring(0, getSelected.length() - 1);

            String DELETE_URL = "http://192.168.1.101/am/deleteInstruments.php";
            Map<String, String> params = new HashMap<String, String>();
            params.put("ids", getSelected);


            CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST,DELETE_URL,params,
                    new Response.Listener<JSONObject>(){
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getBoolean("success")){
                                    Intent intent = new Intent(UserProfileActivity.this,UserProfileActivity.class);
                                    intent.putExtra("id",id);
                                    intent.putExtra("name",name);
                                    startActivity(intent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                                    builder.setMessage("Delete Failed")
                                            .setNegativeButton("Retry",null)
                                            .create()
                                            .show();
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

            Volley.newRequestQueue(UserProfileActivity.this).add(jsonObjectRequest);
        }
    }

    public void AddNew(View view){
        Intent intent = new Intent(UserProfileActivity.this,AddActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("name",name);
        startActivity(intent);
    }

}
