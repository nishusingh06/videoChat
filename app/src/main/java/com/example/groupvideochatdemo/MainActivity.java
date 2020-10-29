package com.example.groupvideochatdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import android.content.Intent;
import android.os.Bundle;
import com.example.groupvideochatdemo.utils.Utils;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton btnStartCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        initListener();
    }

    private void initUi() {

        btnStartCall = findViewById(R.id.btnStartCall);
    }

    private void initListener() {

        btnStartCall.setOnClickListener(view -> {

            if (OpenTokConfig.isConfigsValid()) {
                startActivity(new Intent(this, CallActivity.class));
            } else {
                Utils.showAlert(this, getString(R.string.app_name), OpenTokConfig.errorMessage,
                        getString(R.string.no), getString(R.string.yes), isTrue -> {
                            if (!isTrue) {
                                MainActivity.this.finish();
                            }
                        });
            }
        });
    }
}