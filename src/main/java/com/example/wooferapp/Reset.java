package com.example.wooferapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

public class Reset extends Activity {

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //myDB=new DatabaseHelper(this, "app");
        setContentView(R.layout.reset);


    }

    public void setPassword(View view) throws ExecutionException, InterruptedException {
        EditText username = findViewById(R.id.username);
        EditText key = findViewById(R.id.key);
        EditText password = findViewById(R.id.password1);
        EditText password2= findViewById(R.id.password2);
        String  user_username=username.getText().toString().trim();
        String user_key = key.getText().toString().trim();
        String user_password = password.getText().toString().trim();
        String user_confirm=password2.getText().toString().trim();

        JsonTask m= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/woofer.php?brand="+user_username);
        String web=m.get();
        m.onPostExecute(web);
        System.out.println(this.password+" vs "+user_username);
        if(user_key.equals(this.password) && user_username.equals(this.username)){
            JsonTask d= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/reset.php?brand="+user_password+"&brand2="+user_username+"&brand3="+user_key);
            String web1=d.get();
            d.onPostExecute(web1);
            key.setText("");
            password.setText("");
            password2.setText("");
            Toast.makeText(this, "Password Reset Successful", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(this, Login.class);
            startActivity(i);
        return;}
        if(user_password.length()==0 || user_username.length()==0 || user_key.length()==0 || user_confirm.length()==0 ){
            Toast.makeText(this, "Leave no entry blank", Toast.LENGTH_SHORT).show();
            return;}
        else if(!web.contains(user_username)){
            Toast.makeText(this, "Account does not exist", Toast.LENGTH_SHORT).show();
        }
        else if(!user_password.equals(user_confirm)){
            Toast.makeText(this, "Password mismatch on confirmation", Toast.LENGTH_SHORT).show();
            return;}
        else{ Toast.makeText(this, "Reset Key is incorrect. Please try again.", Toast.LENGTH_SHORT).show(); }

        key.setText("");
        password.setText("");
        password2.setText("");

    }


    public void processJSON(String json){
        try {
            JSONArray all = new JSONArray(json);
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String name = item.getString("U_NAME");
                String description = item.getString("RESETKEY");
                //System.out.println(name+" "+description);
                this.password=description;
                this.username=name;
            }
            //if(this.password !=null && this.password.equals(password))
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
//            pd = new ProgressDialog(Login.this);
//            pd.setMessage("Please wait");
//            pd.setCancelable(false);
//            pd.show();
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
//           R

            if(result!=null) {processJSON(result);};
            //txtJson.setText(result);
        }
    }

}
