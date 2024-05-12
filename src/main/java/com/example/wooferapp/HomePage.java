
package com.example.wooferapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class HomePage extends AppCompatActivity{
    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private EditText editTextRecipient;
    private EditText editextsend;
    private Button buttonSend;
    //private List<HomepageChat> messageList;
    String username, password;

    Login h;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    PageViewer pageViewer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.myblack)));
        tabLayout=findViewById(R.id.tab_layout);

        int tabPosition = 0; // Replace with the desired tab position

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                // Get the TextView within the tab's view
                TextView tabTextView = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab_item, null);
                tabTextView.setText(tab.getText());
                // Set the text alignment to center
                tabTextView.setGravity(Gravity.CENTER);
                // Set the desired text color for the selected tab
                if (i == 0) {
                    tabTextView.setTextColor(getResources().getColor(R.color.teal_200));} // Replace with the desired color resource
                tab.setCustomView(tabTextView);
            }
        }

        viewPager2=findViewById(R.id.view_page);
        pageViewer=new PageViewer(this);
        viewPager2.setAdapter(pageViewer);

        JsonTask m= (JsonTask) new JsonTask().execute("https://lamp.ms.wits.ac.za/home/s2596286/woof.php?brand="+Login.username);
        String web= null;
        try {
            web = m.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        m.onPostExecute(web);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
//
                    if (tab != null) {
                        // Get the TextView within the tab's view
                        TextView tabTextView = (TextView) LayoutInflater.from(HomePage.this).inflate(R.layout.custom_tab_item, null);
                        tabTextView.setText(tab.getText());
                        tabTextView.setGravity(Gravity.CENTER);
                        // Set the desired text color for the selected tab
                       // if (i == tabPosition) {
                            tabTextView.setTextColor(getResources().getColor(R.color.teal_200)); // Replace with the desired color resource

                        tab.setCustomView(tabTextView);
                   // }
               }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//                TextView tabTextView = tab.getCustomView().findViewById(android.R.id.text1);
//                tabTextView.setTextColor(getResources().getColor(R.color.white));
                if (tab != null) {
                    // Get the TextView within the tab's view
                    TextView tabTextView = (TextView) LayoutInflater.from(HomePage.this).inflate(R.layout.custom_tab_item, null);
                    tabTextView.setText(tab.getText());
                    tabTextView.setGravity(Gravity.CENTER);
                    // Set the desired text color for the selected tab
                    // if (i == tabPosition) {
                    tabTextView.setTextColor(getResources().getColor(R.color.white)); // Replace with the desired color resource

                    tab.setCustomView(tabTextView);
                    // }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

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

            }
            //if(this.password !=null && this.password.equals(password))
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
