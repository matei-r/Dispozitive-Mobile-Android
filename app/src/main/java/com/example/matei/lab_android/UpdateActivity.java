package com.example.matei.lab_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    Spinner spinner;
    EditText nameEdit;
    String id;
    String instr_name;
    String instr_type;
    String user_id;
    String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Intent updateIntent = getIntent();
        id = updateIntent.getStringExtra("id");
        instr_name = updateIntent.getStringExtra("name");
        instr_type = updateIntent.getStringExtra("type");
        user_id = updateIntent.getStringExtra("user_id");
        user_name = updateIntent.getStringExtra("user_name");

        spinner = (Spinner) findViewById(R.id.chooseType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.type_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(instr_type));
        nameEdit = (EditText) findViewById(R.id.editName);

        nameEdit.setText(instr_name);
    }

    public void updateInstrument(View view){

        String name = nameEdit.getText().toString();
        String type = spinner.getSelectedItem().toString();
        String instr_id = id;

        String UPDATE_URL = "http://192.168.1.101/am/updateInstrument.php";
        Map<String,String> params = new HashMap<String,String>();
        params.put("name",name);
        params.put("type",type);
        params.put("id",instr_id);

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST,UPDATE_URL,params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                Intent intent = new Intent(UpdateActivity.this,UserProfileActivity.class);
                                intent.putExtra("id",user_id);
                                intent.putExtra("name",user_name);
                                startActivity(intent);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateActivity.this);
                                builder.setMessage("Updating Failed")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(UpdateActivity.this);
                builder.setMessage(error.toString())
                        .setNegativeButton("Retry",null)
                        .create()
                        .show();
            }
        });

        Volley.newRequestQueue(UpdateActivity.this).add(jsonObjectRequest);
    }
}
