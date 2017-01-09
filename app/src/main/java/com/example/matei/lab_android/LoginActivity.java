package com.example.matei.lab_android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static android.app.PendingIntent.getActivity;

public class LoginActivity extends AppCompatActivity {

    DBController controller;

    boolean connected = true;
    EditText editTextUsername,editTextPassword;
    ProgressDialog progressDialog;
    ProgressDialog progressDialog2;

    private void checkConnection(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getActiveNetworkInfo() == null){
            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("No internet connection detected!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which){
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
            connected = false;
        } else {
            connected = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        controller = new DBController(this);

        checkConnection();

        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        final TextView registerLink = (TextView) findViewById(R.id.registerLink);

        synchronize();

        //controller.refreashTables();

        final Button loginButton = (Button) findViewById(R.id.buttonLogin);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });


    }

    private void synchronize() {

        if(connected == true) {

            progressDialog2 = new ProgressDialog(LoginActivity.this);
            progressDialog2.setMessage("Loading,please wait...");
            progressDialog2.setTitle("Synchronizing data");
            progressDialog2.setCancelable(false);
            progressDialog2.show();

            ArrayList<String> queryList = controller.getAllQuerys();

            String querys = "";
            for(int i=0;i<queryList.size();i++){
                querys += queryList.get(i) + ";";
            }

            if(querys.length() > 0) {

                controller.deleteTableContent("querys");

                querys = querys.substring(0, querys.length() - 1);

                String LOGIN_URL = "http://192.168.1.102:8080/am/synchronize.php";
                Map<String, String> params = new HashMap<String, String>();
                params.put("querys", querys);

                CustomRequest jsonObjectRequest = new CustomRequest(Request.Method.POST, LOGIN_URL, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getBoolean("success")) {
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setMessage("Login Failed")
                                                .setNegativeButton("Retry", null)
                                                .create()
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage(e.toString())
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(error.toString())
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                });

                Volley.newRequestQueue(LoginActivity.this).add(jsonObjectRequest);

            }

            progressDialog2.hide();
        }
    }

    @Override
    public void onRestart(){
        super.onRestart();
        if(progressDialog != null) {
            progressDialog.hide();
        }
        if(progressDialog2 != null) {
            progressDialog2.hide();
        }
    }

    public void OnLogin(View view){
        if(connected == true) {
            OnLoginOnline();
        } else {
            OnLoginOffline();
        }
    }

    private void OnLoginOnline() {

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading,please wait...");
        progressDialog.setTitle("Signing in");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Volley.newRequestQueue(LoginActivity.this).cancelAll("LoginTag");
            }
        });
        progressDialog.show();

        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        String LOGIN_URL = "http://192.168.1.102:8080/am/login.php";
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
                                intent.putExtra("email",response.getString("email"));
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry",null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(error.toString())
                        .setNegativeButton("Retry",null)
                        .create()
                        .show();
            }
        });

        jsonObjectRequest.setTag("LoginTag");

        Volley.newRequestQueue(LoginActivity.this).add(jsonObjectRequest);

    }

    private void OnLoginOffline(){
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        User user = controller.findUser(username,password);
        if(user != null) {
            Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("id", Integer.toString(user.getId()));
            intent.putExtra("email", user.getEmail());
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage("Login Failed")
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }
    }
}
