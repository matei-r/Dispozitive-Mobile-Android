package com.example.matei.lab_android;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static android.app.PendingIntent.getActivity;

public class LoginActivity extends AppCompatActivity {
    EditText editTextUsername,editTextPassword;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        final TextView registerLink = (TextView) findViewById(R.id.registerLink);

        final Button loginButton = (Button) findViewById(R.id.buttonLogin);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void OnLogin(View view){
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        String LOGIN_URL = "http://192.168.1.101/am/login.php";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username",username);
        params.put("password",password);

        CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST,LOGIN_URL,params,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                Intent intent = new Intent(LoginActivity.this,UserProfileActivity.class);
                                intent.putExtra("name",response.getString("name"));
                                intent.putExtra("id",response.getString("id"));
                                startActivity(intent);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
                progressBar.setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(error.toString())
                        .setNegativeButton("Retry",null)
                        .create()
                        .show();
            }
        });

        Volley.newRequestQueue(LoginActivity.this).add(jsonObjectRequest);
    }
}
