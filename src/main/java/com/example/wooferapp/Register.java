package com.example.wooferapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

public class Register extends Activity {
    String[] vals = new String[2];
    ProgressDialog pd;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

    }

    public void doRegister(View v) throws ExecutionException, InterruptedException {
        EditText username = findViewById(R.id.editTextusername);
        EditText password = findViewById(R.id.editTextpassword);
        EditText password2= findViewById(R.id.editconfirm);
        EditText email = findViewById(R.id.editemail);
        String user_email = email.getText().toString().trim();
        String user_username = username.getText().toString().trim();
        String user_password = password.getText().toString().trim();
        String user_confirm=password2.getText().toString().trim();
        JsonTask b= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/woofer.php?brand="+user_username);
        //JsonTask c= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/woofer3.php?brand="+user_email);
        if(user_password.length()==0 || user_username.length()==0 || user_email.length()==0 || user_confirm.length()==0 ){
            Toast.makeText(this, "Leave no entry blank", Toast.LENGTH_SHORT).show();
            return;}
        else if(!user_password.equals(user_confirm)){
            Toast.makeText(this, "Password mismatch on confirmation", Toast.LENGTH_SHORT).show();
            return;}

        else if(!b.get().equals("[]\n")){
            Toast.makeText(this,"Account already exists, consider password reset", Toast.LENGTH_SHORT).show();
            return;}
//        else if(!c.get().equals("[]\n")){
//            Toast.makeText(this, "Email already has account, consider using different email", Toast.LENGTH_SHORT).show();
//            return;
//        }
        JsonTask a= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/woofer2.php?brand="+user_username+"&brand2="+user_email+"&brand3="+user_password);
        Toast.makeText(Register.this, "WOOF! WOOF! Account created successfully!", Toast.LENGTH_SHORT).show();
        Intent i=new Intent(Register.this,Login.class);
        startActivity(i);
    }

    public void processJSON(String json){
        try {
            JSONArray all = new JSONArray(json);
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String name = item.getString("U_NAME");
                String description = item.getString("PWORD");
               // System.out.println(name+" "+description);
                this.password=description;
                this.username=name;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            if(result!=null) {processJSON(result);};
            //txtJson.setText(result);
        }
    }
}



