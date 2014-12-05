package com.mattkula.giffer.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.mattkula.giffer.R;
import com.mattkula.giffer.Utils;

public class MainActivity extends Activity {

    private ToggleButton btnStatus;
    private ImageView imgLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStatus = (ToggleButton)findViewById(R.id.btn_enable_disable);
        btnStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    Utils.showNotification(MainActivity.this);
                } else {
                    Utils.hideNotification(MainActivity.this);
                }
            }
        });
        imgLogo = (ImageView)findViewById(R.id.img_giphy_logo);
        Utils.showGiphyLogo(this, imgLogo, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
