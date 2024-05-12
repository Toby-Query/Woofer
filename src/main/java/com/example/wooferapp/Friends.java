package com.example.wooferapp;

import android.os.AsyncTask;
import android.util.Log;

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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Friends {
    static ArrayList<String> frie=new ArrayList<>();
    String friend;

    public void kill(String frienddd) throws ExecutionException, InterruptedException {
        //System.out.println(frienddd);
        JsonTask b= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/addfro.php?brand="+Login.username+"&brand2="+frienddd);
        b.onPostExecute(b.get());

    }

    public void kil() throws ExecutionException, InterruptedException {
        JsonTask a= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/fecthfro.php?brand="+Login.username);
        a.onPostExecute(a.get());
    }


    public void processJSON(String json){
        frie=new ArrayList<>();
        try {
            JSONArray all = new JSONArray(json);
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String name = item.getString("MYFRIEND");
                frie.add(name);
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
//            if (pd.isShowing()){
//                pd.dismiss();
//            }
            if(result!=null) {processJSON(result);};
            //txtJson.setText(result);
        }
    }
}
