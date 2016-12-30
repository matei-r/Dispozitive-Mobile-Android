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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class UserProfileActivity extends AppCompatActivity {
    TextView welcomeMessage,logout;
    ListView instrumentListView;
    ArrayList<Instrument> instrumentList;
    String id;
    String name;
    Button deleteBtn;
    int selected = 0;
    ArrayList<String> idSelected = new ArrayList<String>();
    BarChart barChart;

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

        onResume();

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

    public interface VolleyCallback{
        void onSuccess(JSONArray result);
    }

    public void loadList(final VolleyCallback callback) {

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
                            callback.onSuccess(arr);
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

        Volley.newRequestQueue(UserProfileActivity.this).add(jsonArrayRequest);

    }

    public void onResume(){
        super.onResume();
        loadList(new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray result) {

                instrumentList.clear();
                int i;

                for(i=0;i<result.length();i++){
                    try {
                        JSONObject obj = result.getJSONObject(i);
                        int id = obj.getInt("id");
                        String name = obj.getString("name");
                        String type = obj.getString("type");
                        Instrument instr = new Instrument(id,name,type);
                        instrumentList.add(instr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                InstrumentAdapter adapter = new InstrumentAdapter(UserProfileActivity.this,R.layout.list_item,instrumentList);
                instrumentListView.setAdapter(adapter);
                instrumentListView.setItemsCanFocus(false);
                instrumentListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

                //buildChart();
            }
        });
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

    public ArrayList<Integer> getChartData(){
        String[] typeArray;
        typeArray = getResources().getStringArray(R.array.type_array);
        int i,k;
        ArrayList<Integer> data = new ArrayList<Integer>();

        for(k=0;k<typeArray.length;k++) {
            int freq = 0;
            for (i = 0; i < instrumentList.size(); i++) {
                if(typeArray[k].equals(instrumentList.get(i).getType())){
                    freq++;
                }
            }
            data.add(freq);
        }
        return data;
    }

    public void buildChart(){

        barChart = (BarChart) findViewById(R.id.barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();

        ArrayList<Integer> data = getChartData();

        int i;
        for(i=0;i<data.size();i++){
            int freq = data.get(i);
            Log.d("Index",String.valueOf(freq));
            entries.add(new BarEntry(freq, i));
        }

        BarDataSet barDataSet = new BarDataSet(entries,"Ceva");

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add((IBarDataSet) barDataSet);

        BarData theData = new BarData(dataSets);
        barChart.setData(theData);
    }
}
