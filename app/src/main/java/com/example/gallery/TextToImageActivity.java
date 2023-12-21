package com.example.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gallery.adapter.TextToImageAdapter;
import com.example.gallery.object.AI_Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TextToImageActivity extends AppCompatActivity {
    List<AI_Image> imageList;
    TextToImageAdapter adapter;
    AI_Image aiImage;
    Button btnGen;
    EditText editText;
    RecyclerView recyclerView;
    String gg;
    String url;
    Toolbar topBar;
    LottieAnimationView loadingAnimation;
    //    String url = "https://api.openai.com/v1/images/generations";
    //String accesstoken = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_image);
        // Hide actionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnGen = findViewById(R.id.btnGen);
        editText = findViewById(R.id.editText2);
        recyclerView = findViewById(R.id.recyclerview);
        loadingAnimation = findViewById(R.id.animationView);
        topBar = findViewById(R.id.topAppBar);

        imageList = new ArrayList<>();
        adapter = new TextToImageAdapter(imageList, TextToImageActivity.this);
        recyclerView.setAdapter(adapter);
        GridLayoutManager ggm = new GridLayoutManager(TextToImageActivity.this, 2);
        recyclerView.setLayoutManager(ggm);
        btnGen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageList.isEmpty()){
                    imageList.clear();
                    adapter.notifyDataSetChanged();
                }
                String question = editText.getText().toString().trim();
                gg = question;
                url = "https://lexica.art/api/v1/search?q=" + gg;
//               url = url+question;
                if (!question.equals("")) {
                    Toast.makeText(TextToImageActivity.this, "Message is send", Toast.LENGTH_SHORT).show();
                    callApi(gg);
                } else {
                    Toast.makeText(TextToImageActivity.this, "Please enter iamge title", Toast.LENGTH_SHORT).show();
                }
            }

        });

        topBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TextToImageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    void callApi(String question) {


        loadingAnimation.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONArray jsonArray ;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    jsonArray = jsonObject.getJSONArray("images");
                    Toast.makeText(TextToImageActivity.this, " inside the api call ", Toast.LENGTH_SHORT).show();
                    Log.e("responseData", response.toString());
                    Log.e("ImageUrl", jsonArray.getJSONObject(0).getString("srcSmall"));

                    for (int i = 0; i < jsonArray.length(); i++) {
                        aiImage = new AI_Image(jsonArray.getJSONObject(i).getString("srcSmall"));
                        imageList.add(aiImage);
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                loadingAnimation.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TextToImageActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse", error.toString());
            }
        }) {

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(request);
    }

}