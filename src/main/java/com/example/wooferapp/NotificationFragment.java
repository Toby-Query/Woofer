package com.example.wooferapp;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class NotificationFragment extends Fragment {

    TextView tt;
    private LinearLayout linearLayout;
    private Button refreshButton;
    private ArrayList<ArrayList<String>> posts;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private FloatingActionButton btnPickImage;
    private Uri imageUri;

    private Button retrieveButton;

    ArrayList<Integer> count=new ArrayList<>();

    private static String RETRIEVE_URL = "https://lamp.ms.wits.ac.za/home/s2596286/retrieve.php?image_name=";
    int counter=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        linearLayout = rootView.findViewById(R.id.addStatus);
        refreshButton = rootView.findViewById(R.id.refreshButton);
        View nContainer= inflater.inflate(R.layout.statusdesign,null);
        tt=nContainer.findViewById(R.id.mystatus);
        Button b=rootView.findViewById(R.id.send);
        btnPickImage = rootView.findViewById(R.id.fab);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentTime = Calendar.getInstance().getTime();
                EditText e = rootView.findViewById(R.id.mytext);
                String special = e.getText().toString().trim();
                if(special.equals("")){
                    Toast.makeText(getActivity(), "Input text status", Toast.LENGTH_SHORT).show();
                    return;
                }
                TextView t = new TextView(getActivity());
                t.setBackgroundResource(R.drawable.designstat);
                t.setTextColor(getResources().getColor(R.color.white));
                t.setLayoutParams(tt.getLayoutParams());
                t.setTextSize(20);
                t.setPadding(tt.getPaddingStart(), tt.getPaddingTop(), tt.getPaddingEnd(), tt.getPaddingEnd());
                t.setText(Login.username + " : " + currentTime.toString().substring(0, 16) + "\n \n" + e.getText().toString().trim());
                e.setText("");

                // Add the new status at the beginning of the linear layout
                linearLayout.addView(t, 0);

                // Remove the last status if the maximum number of statuses is reached
                System.out.println(e.getText().toString() + "My boy");
                JsonTask b = (JsonTask) new JsonTask(new JsonTaskListener() {
                    @Override
                    public void onTaskComplete(String result) {
                        // Do nothing here
                    }
                }).execute("https://lamp.ms.wits.ac.za/home/s2596286/uploadstatus.php?brand=" + Login.username + "&brand2=" + special + "&brand3=" + currentTime.toString().substring(0, 16));

                fetchStatusUpdates();
            }
        });

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchStatusUpdates();
            }
        });

        fetchStatusUpdates();

        return rootView;
    }


    public interface ImageRetrievalCallback {
        void onImageRetrieved(Bitmap bitmap);
    }
    private class RetrieveImageTask extends AsyncTask<String, Void, Bitmap> {
        private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();

                // Check if the image size exceeds the maximum file size
                long imageSize = connection.getContentLength();
                if (imageSize >= MAX_FILE_SIZE) {
                    // Compress the image
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(input, null, options);
                    int scale = calculateScale(options, MAX_FILE_SIZE);
                    input.close();

                    // Reopen the connection and download the compressed image
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    input = connection.getInputStream();
                    BitmapFactory.Options scaledOptions = new BitmapFactory.Options();
                    scaledOptions.inSampleSize = scale;
                    return BitmapFactory.decodeStream(input, null, scaledOptions);
                } else {
                    // Image size is within the limit, decode it without compression
                    return BitmapFactory.decodeStream(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private int calculateScale(BitmapFactory.Options options, long targetSize) {
            int scale = 1;
            int width = options.outWidth;
            int height = options.outHeight;
            long fileSize = width * height * 4; // Assuming 4 bytes per pixel (ARGB_8888)

            while (fileSize > targetSize) {
                width /= 2;
                height /= 2;
                scale *= 2;
                fileSize = width * height * 4;
            }

            return scale;
        }
    }


    private void openGallery() {
        Toast.makeText(getActivity(),"Image must be less than 2MB", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        counter++;
    }

    private void uploadImageToServer(Uri imageUri, String imageName, String text) {
        try {
            // Open an InputStream from the imageUri
            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);

            // Create a byte array to store the image data
            byte[] imageData = getBytesFromInputStream(inputStream);

            // Create a request body with the image data
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageData);

            // Create the request body part for the image file
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", imageName, requestBody);

            // Create the request body for other form data (if any)
            RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), "Image description");

            // Create the Retrofit instance
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://lamp.ms.wits.ac.za/home/s2596286/")  // Replace with your server URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // Create the service interface for the API
            ApiService apiService = retrofit.create(ApiService.class);
            Date currentTime = Calendar.getInstance().getTime();
            // Call the API method to upload the image
            Call<ResponseBody> call = apiService.uploadImage(filePart, descriptionPart, Login.username, text,currentTime.toString().substring(0, 16) );
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        // Image upload successful
                        Toast.makeText(getActivity(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Image upload failed
                        Toast.makeText(getActivity(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // Image upload failed
                    Toast.makeText(getActivity(), "Image upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception if unable to open InputStream or read image data
            Toast.makeText(getActivity(), "Failed to read image data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {

                EditText e = getActivity().findViewById(R.id.mytext);
                //fetchStatusUpdates();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                LinearLayout linearLayout1 = new LinearLayout(getActivity());
                LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                linearLayout1.setLayoutParams(tt.getLayoutParams());
                linearLayout1.setOrientation(LinearLayout.VERTICAL);
                linearLayout1.setBackgroundResource(R.drawable.designstat);

                // Create TextView
                TextView textView = new TextView(getActivity());
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                Date currentTime = Calendar.getInstance().getTime();
                textView.setText(Login.username+ " : "+currentTime.toString().substring(0, 16)+"\n \n"+e.getText().toString().trim());
                //e.setText("");
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setPadding(10, 10, 10, 10);
                textView.setTextSize(20);
                textView.setPadding(tt.getPaddingStart(), tt.getPaddingTop(), tt.getPaddingEnd(), tt.getPaddingEnd());



                // Create ImageView
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                imageView.setPadding(25,15,25,50);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY); // Preserve aspect ratio and fill parent width

// Calculate image height based on aspect ratio and parent width
                int parentWidth = getResources().getDisplayMetrics().widthPixels;
                int imageHeight = (int) (parentWidth / (float) bitmap.getWidth() * bitmap.getHeight());
                imageView.getLayoutParams().height = imageHeight;
                // Add TextView and ImageView to the LinearLayout
                linearLayout1.addView(textView);
                linearLayout1.addView(imageView);

                ArrayList<View> views=new ArrayList<>();
                for(int i=0; i<linearLayout.getChildCount();i++){
                    views.add(linearLayout.getChildAt(i));
                }
                linearLayout.removeAllViews();
                linearLayout.addView(linearLayout1);
                for(View v:views){
                    linearLayout.addView(v);
                }

                // Call the method to upload the image to the server

                Collections.sort(count);
                System.out.println("----------------------------"+imageView);
                uploadImageToServer(imageUri, (count.get(count.size()-1)+1+counter)+".jpg", e.getText().toString().trim());
                e.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private void fetchStatusUpdates() {
        // Clear the existing status updates
        linearLayout.removeAllViews();

        // Fetch status updates from the server
        new JsonTask(new JsonTaskListener() {
            @Override
            public void onTaskComplete(String result) throws IOException, ExecutionException, InterruptedException {
                processJSON(result);
                displayStatusUpdates();
            }
        }).execute("https://lamp.ms.wits.ac.za/home/s2596286/fetchstatus.php");
    }

    private void processJSON(String json) {
        posts = new ArrayList<>();

        try {
            JSONArray all = new JSONArray(json);
            for (int i = 0; i < all.length(); i++) {
                JSONObject item = all.getJSONObject(i);
                String poster = item.getString("POSTER");
                String message = item.getString("POST");
                String date = item.getString("DATE");
                String ID = item.getString("ID");
                String locate = item.getString("file_path");
                String file_nam =item.getString("file_name");


                counter++;
                count.add(counter);
                if(!count.contains(Integer.parseInt(ID))){
                    count.add(Integer.parseInt(ID));
                }
                Friends b= new Friends();
                b.kil();

                if(Friends.frie.contains(poster) || poster.equals(Login.username)){

                ArrayList<String> one = new ArrayList<>();
                one.add(poster);
                one.add(message);
                one.add(date);
                one.add(ID);
                one.add(locate);
                one.add(file_nam);
                posts.add(one);}
                System.out.println("From process, these are posts : "+posts);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayStatusUpdates() throws IOException, ExecutionException, InterruptedException {
        Collections.reverse(posts);
        for (ArrayList<String> pos : posts) {
            String username = pos.get(0);
            String message = pos.get(1);
            String date = pos.get(2);
            String file_name=pos.get(5);

            View statusTextView = createStatusTextView(username, date, message, file_name);
            linearLayout.addView(statusTextView);
        }
    }

    private View createStatusTextView(String username, String date, String message, String file_name) throws IOException, ExecutionException, InterruptedException {
        TextView textView = new TextView(getActivity());
        if(file_name.equals("null")){
        textView.setBackgroundResource(R.drawable.designstat);
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setPadding(tt.getPaddingStart(), tt.getPaddingTop(),tt.getPaddingEnd(),tt.getPaddingEnd());
        textView.setLayoutParams(tt.getLayoutParams());
        textView.setText(username + " : " + date + "\n \n" + message);
        textView.setTextSize(20);}
        else{
            RetrieveImageTask task = new RetrieveImageTask();
            task.execute(RETRIEVE_URL+file_name);
            //task.onPostExecute(task.get());
            System.out.println(RETRIEVE_URL+file_name);

            EditText e = getActivity().findViewById(R.id.mytext);
            //fetchStatusUpdates();
            Bitmap bitmap = task.get();
            System.out.println("This is my bitmap: "+bitmap);
            LinearLayout linearLayout1 = new LinearLayout(getActivity());
            LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            linearLayout1.setLayoutParams(tt.getLayoutParams());
            linearLayout1.setOrientation(LinearLayout.VERTICAL);
            linearLayout1.setBackgroundResource(R.drawable.designstat);

            // Create TextView
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            Date currentTime = Calendar.getInstance().getTime();
            textView.setText(username+ " : "+date+"\n \n"+message);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setPadding(10, 10, 10, 10);
            textView.setTextSize(20);
            textView.setPadding(tt.getPaddingStart(), tt.getPaddingTop(), tt.getPaddingEnd(), tt.getPaddingEnd());



            // Create ImageView
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            imageView.setPadding(25,15,25,50);
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY); // Preserve aspect ratio and fill parent width

// Calculate image height based on aspect ratio and parent width
            int parentWidth = getResources().getDisplayMetrics().widthPixels;
            int imageHeight = (int) (parentWidth / (float) bitmap.getWidth() * bitmap.getHeight());
            imageView.getLayoutParams().height = imageHeight;
            // Add TextView and ImageView to the LinearLayout
            linearLayout1.addView(textView);
            linearLayout1.addView(imageView);
            return linearLayout1;
        }


        return textView;
    }

    private interface JsonTaskListener {
        void onTaskComplete(String result) throws IOException, ExecutionException, InterruptedException;
    }

    private static class JsonTask extends AsyncTask<String, Void, String> {
        private JsonTaskListener listener;

        public JsonTask(JsonTaskListener listener) {
            this.listener = listener;
        }

        public JsonTask() {
            // Empty constructor
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (listener != null) {
                try {
                    listener.onTaskComplete(result);
                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
