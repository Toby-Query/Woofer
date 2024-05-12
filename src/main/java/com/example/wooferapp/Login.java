package com.example.wooferapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class Login  extends Activity {

    ProgressDialog pd;

    static String username, password;

    public String getUsername(){
        return username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myDB=new DatabaseHelper(this, "app");
        setContentView(R.layout.activity_login);


    }

    public void doRegister(View v) {

        Intent i=new Intent(this,Register.class);
        startActivity(i);
    }

    public void doLogin(View v) throws ExecutionException, InterruptedException {
        EditText username = findViewById(R.id.edittextusername);
        EditText password = findViewById(R.id.edittextpassword);
        String user_username = username.getText().toString().trim();
        String user_password = password.getText().toString().trim();
        JsonTask m= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/woofer.php?brand="+user_username);
        String web=m.get();
        m.onPostExecute(web);

        System.out.println("This is web "+web + " and "+user_password);
        if(user_password.equals(this.password) && user_username.equals(this.username)){
            Intent i = new Intent(Login.this, HomePage.class);
            startActivity(i);}
        else if(user_password.length()==0 || user_username.length()==0){
            Toast.makeText(this,"Leave no entry blank", Toast.LENGTH_SHORT).show();
        }
        else if(!web.contains(user_username)){
            Toast.makeText(this, "Account does not exist", Toast.LENGTH_SHORT).show();
        }
        else{ Toast.makeText(this, "Wrong Password. Try again.", Toast.LENGTH_SHORT).show(); }
        password.setText("");
    }

    public void processJSON(String json){
        try {
            JSONArray all = new JSONArray(json);
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String name = item.getString("U_NAME");
                String description = item.getString("PWORD");
                //System.out.println(name+" "+description);
                this.password=description;
                this.username=name;
            }
            //if(this.password !=null && this.password.equals(password))
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void forgotPassword(View view) {
        Intent i = new Intent(this, Reset.class);
        startActivity(i);
    }


    private class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            processJSON(result);
        }
    }

}