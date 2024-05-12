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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ExpandableListDataPump {

    static ArrayList<String> friends;

    static List<List<String>> h=new ArrayList<List<String>>();
    public static HashMap<String, List<String>> getData() throws ExecutionException, InterruptedException {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        //System.out.println("Special out"+Friends.frie);
        List<String> j;
        ExpandableListDataPump exp;

        Friends m=new Friends();
        try {
            m.kil();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        h.clear();

        for(String i: Friends.frie){

            j=new ArrayList<>();
            //j.add(i);
            exp=new ExpandableListDataPump();
            exp.addFriends(i);  //run addFriends to make friends nonempty
            //System.out.println(friends);
            //System.out.println("What is"+friends);
            for(String k: friends){
                j.add(k);
            }
            h.add(j); //h has every list
            //System.out.println();
        }

        int count=0;
        for(List<String> lists: h){


            if (!Friends.frie.isEmpty()){
                expandableListDetail.put(Friends.frie.get(count),lists );
                System.out.println(Friends.frie.get(count)+ " has friends \n"+lists );
            }
            if(count<Friends.frie.size()-1){count++;}
        }
        System.out.println("Hashmapo "+expandableListDetail);

//

        return expandableListDetail;
    }

    public void addFriends(String special) throws ExecutionException, InterruptedException {
        JsonTask a= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/fecthfro.php?brand="+special);
        a.onPostExecute(a.get());
    }

    public void processJSON(String json){
        friends=new ArrayList<>();
        try {
            JSONArray all = new JSONArray(json);
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String name = item.getString("MYFRIEND");
               friends.add(name);
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
                    Log.d("Response: ", "> " + line);
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
        }
    }

}
