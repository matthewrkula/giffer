package com.mattkula.giffer.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mattkula.giffer.R;
import com.mattkula.giffer.datamodels.ImageResult;

import java.util.ArrayList;

public class SearchActivity extends Activity {

    private Button searchBtn;
    private EditText searchBox;
    private GridView searchGrid;

    String url = "http://api.giphy.com/v1/gifs/search?q=funny+cat&api_key=dc6zaTOxFJmzC";

    ArrayList<ImageResult> searchResults = new ArrayList<ImageResult>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBox = (EditText)findViewById(R.id.edit_search);
        searchBtn = (Button)findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(searchBox.getText().toString());
            }
        });

        searchGrid = (GridView)findViewById(R.id.grid_search);
        searchGrid.setAdapter(new GifSearchAdapter());
        searchGrid.setNumColumns(3);
        searchGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView imageView = (ImageView)view.findViewById(R.id.image_view);
                ImageResult result = searchResults.get(i);

                Ion.with(SearchActivity.this)
                        .load(result.gifURL)
                        .withBitmap()
                        .placeholder(getResources().getDrawable(R.drawable.ic_launcher))
                        .intoImageView(imageView);
            }
        });

        search("funny+dog");
    }

    private void search(String term) {
        Ion.with(this)
                .load("http://api.giphy.com/v1/gifs/search?q=" + term + "&api_key=dc6zaTOxFJmzC")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        searchResults.clear();
                        JsonArray array = result.getAsJsonArray("data");
                        for (JsonElement object : array) {
                            JsonObject image = object.getAsJsonObject();
                            ImageResult imageResult = ImageResult.getFromJsonObject(image);
                            searchResults.add(imageResult);
                        }
                        ((BaseAdapter)searchGrid.getAdapter()).notifyDataSetChanged();
                    }
                });

    }

    private class GifSearchAdapter extends BaseAdapter {

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(SearchActivity.this, R.layout.grid_item_result, null);
            }
            ImageView imageView = (ImageView)view.findViewById(R.id.image_view);
            ImageResult image = (ImageResult)getItem(i);

            Ion.with(SearchActivity.this)
                    .load(image.stillURL)
                    .withBitmap()
                    .placeholder(getResources().getDrawable(R.drawable.ic_launcher))
                    .intoImageView(imageView);

            return view;
        }

        @Override
        public int getCount() {
            return searchResults.size();
        }

        @Override
        public Object getItem(int i) {
            return searchResults.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
    }
}
