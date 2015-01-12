package com.mattkula.giffer.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.mattkula.giffer.ApiKeys;
import com.mattkula.giffer.R;
import com.mattkula.giffer.Utils;
import com.mattkula.giffer.datamodels.ImageResult;

import java.util.ArrayList;

public class SearchActivity extends Activity {

    private EditText searchBox;
    private GridView searchGrid;

    private ArrayList<ImageResult> searchResults = new ArrayList<ImageResult>();

    private ClipboardManager clipboardManager;

    private String searchTerm;
    private boolean isSearching = false;
    private int totalSearchCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchTerm = "silly+dogs";
        clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);

        searchBox = (EditText)findViewById(R.id.edit_search);
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    searchTerm = searchBox.getText().toString();
                    search(true);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        searchGrid = (GridView)findViewById(R.id.grid_search);
        searchGrid.setAdapter(new GifSearchAdapter());
        searchGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageResult result = searchResults.get(i);
                loadImageIntoView(result.gifURL, view);
            }
        });
        searchGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageResult result = searchResults.get(i);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("gif url", result.fullGifURL));
                Toast.makeText(SearchActivity.this, "URL copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ImageView giphyLogo = (ImageView)findViewById(R.id.img_giphy_logo);
        Utils.showGiphyLogo(this, giphyLogo, false);

        search(true);

        searchGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if (!isSearching
                        && absListView.getLastVisiblePosition() == searchResults.size() - 1
                        && searchResults.size() < totalSearchCount) {
                    search(false);
                }
            }
        });
    }

    private void search(final boolean clearResults) {
        isSearching = true;
        if (clearResults) {
            searchResults.clear();
            totalSearchCount = -1;
        }
        searchTerm = TextUtils.join("+", searchTerm.trim().split("\\s+"));
        String requestURL = String.format("http://api.giphy.com/v1/gifs/search?api_key=%s&q=%s&offset=%d",
                ApiKeys.GIPHY_API_KEY,
                searchTerm,
                searchResults.size());
        Ion.with(this)
                .load(requestURL)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray array = result.getAsJsonArray("data");
                        for (JsonElement object : array) {
                            JsonObject image = object.getAsJsonObject();
                            ImageResult imageResult = ImageResult.getFromJsonObject(image);
                            searchResults.add(imageResult);
                        }
                        totalSearchCount = result.getAsJsonObject("pagination").get("total_count").getAsInt();
                        ((BaseAdapter) searchGrid.getAdapter()).notifyDataSetChanged();
                        // Commented out because of the out of memory error, don't keep searching while scrolling
//                        isSearching = false;
                    }
                });

    }

    private void loadImageIntoView(String url, View view) {
        ImageView imageView = (ImageView)view.findViewById(R.id.image_view);
        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.result_loading);
        progressBar.setVisibility(View.VISIBLE);

        Ion.with(SearchActivity.this)
                .load(url)
                .withBitmap()
                .intoImageView(imageView)
                .setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, ImageView result) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private class GifSearchAdapter extends BaseAdapter {

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = View.inflate(SearchActivity.this, R.layout.grid_item_result, null);
            }
            ImageResult image = (ImageResult)getItem(i);
            loadImageIntoView(image.stillURL, view);

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
