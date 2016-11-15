package com.example.matei.lab_android;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextName,editTextUsername,editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        final EditText editTextRepeatPassword = (EditText) findViewById(R.id.editTextRepeatPassword);

        final Button registerButton = (Button) findViewById(R.id.registerButton);
        final TextView backLink = (TextView) findViewById(R.id.backLink);

        backLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(loginIntent);
            }
        });

        final TextView textViewError = (TextView) findViewById(R.id.textViewError);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Password = s.toString();
                String pass = editTextRepeatPassword.getText().toString();
                if (!Password.equals(pass)) {
                    textViewError.setText("Passwords must match!");
                } else {
                    textViewError.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextRepeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String Password = s.toString();
                String pass = editTextPassword.getText().toString();
                if (!Password.equals(pass)) {
                    textViewError.setText("Passwords must match!");
                } else {
                    textViewError.setText("");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void OnRegister(View view){
        String name = editTextName.getText().toString();
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        String REGISTER_URL = "http://192.168.1.102/am/register.php";
        Map<String,String> params = new HashMap<String,String>();
        params.put("name",name);
        params.put("username",username);
        params.put("password",password);

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST,REGISTER_URL,params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                                startActivity(intent);
                            } else {
                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Failed")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
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
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegisterActivity.this);
                builder.setMessage(error.toString())
                        .setNegativeButton("Retry",null)
                        .create()
                        .show();
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }
}
