package com.example.wooferapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SettingFragment extends Fragment {
    String username, password;
    ArrayList<String> names=new ArrayList<>();
    ArrayList<Button> but=new ArrayList<>();
    ArrayList<Button> buttons = new ArrayList<>();
    LinearLayout linearLayout;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View mContainer = inflater.inflate(R.layout.fragment_setting, container, false);
        linearLayout = mContainer.findViewById(R.id.addFrie);
        Button refreshButton = mContainer.findViewById(R.id.refreshButton);
        updateFriends();
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateFriends(); // Call the method to update friends and refresh UI
            }
        });

        return mContainer;

    }


    public void updateFriends(){
        names.clear();
        buttons.clear();
        linearLayout.removeAllViews();

        JsonTask m = (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/woof.php?brand=" + Login.username);
        String web = null;
        try {
            web = m.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (web != null) {
            try {
                processJSON(web);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        int id = 0;
        for (String i : names) {
            LinearLayout lin = new LinearLayout(getActivity());
            TextView textView = new TextView(getActivity());
            Button button = new Button(getActivity());

            // Set up layout parameters and properties
            lin.setWeightSum(1);
            lin.setOrientation(LinearLayout.HORIZONTAL);


            button.setId(id);
            id++;
            button.setTextSize(18);
            Friends nwe=new Friends();
            try {
                nwe.kil();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // ...

            if (Friends.frie.contains(i)) {
                button.setBackgroundResource(R.color.white);
                button.setText("Friend");
                button.setTextColor(getResources().getColor(R.color.black));
            } else {
                button.setText("Add");
                button.setBackgroundResource(R.color.teal_200);
            }
            button.setAllCaps(false);

            textView.setLayoutParams(new ViewGroup.LayoutParams(500, ViewGroup.LayoutParams.FILL_PARENT));
            textView.setText(i);
            textView.setTextSize(35);
            textView.setPadding(50, 0, 0, 0);
            textView.setTextColor(getResources().getColor(R.color.white));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT);
            LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lin.setLayoutParams(textViewLayoutParams);
            layoutParams.weight = 1.0f;

            textView.setLayoutParams(layoutParams);
            button.setLayoutParams(buttonLayoutParams);
            lin.addView(textView);
            lin.addView(button);
            linearLayout.addView(lin);
            buttons.add(button);

        }

        for (Button button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = buttons.indexOf(button);

                    if (Friends.frie.contains(names.get(index))) {
                        Toast.makeText(getActivity(), "Already have friend " + names.get(index), Toast.LENGTH_SHORT).show();
                    } else {
                        Friends.frie.add(names.get(index));

                    }

                    Friends m = new Friends();
                    try {
                        m.kill(names.get(index));
                    } catch (ExecutionException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    button.setText("Friend");
                    button.setTextColor(getResources().getColor(R.color.black));
                    button.setBackgroundResource(R.color.white);

                }
            });
        }
    }

    public void processJSON(String json) throws JSONException {
        try {
            JSONArray all = new JSONArray(json);
            for (int i=0; i<all.length(); i++){
                JSONObject item=all.getJSONObject(i);
                String name = item.getString("U_NAME");
                String description = item.getString("PWORD");
                //System.out.println(name+" "+description);
                this.password=description;
                this.username=name;
                names.add(name);
            }
            //if(this.password !=null && this.password.equals(password))
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
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
            if(result!=null){
                try {
                    processJSON(result);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}