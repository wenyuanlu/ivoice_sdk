package com.example.qcaudioad.base;


import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null && showLeftGoBackView()) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean showLeftGoBackView () {
        return true;
    }

    public void isShowActionBar (boolean isShow) {
        ActionBar actionBar = getSupportActionBar();

        if (isShow) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    @Override
    public void onClick (View v) {

    }

    public void showLoadAdErrorToast () {
        Toast.makeText(this, "请先加载广告资源", Toast.LENGTH_SHORT).show();
    }
}
