package com.mehdi.fakenews.ACTIVITYS;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mehdi.fakenews.R;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        ImageView ss = findViewById(R.id.back);
        ss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button sss = findViewById(R.id.pr);
        sss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Setting.this, "Not avalable yet", Toast.LENGTH_LONG).show();
            }
        });




        Switch s = findViewById(R.id.not);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor p = PreferenceManager.getDefaultSharedPreferences(Setting.this).edit();
                p.putBoolean("not", isChecked);
                p.apply();
            }
        });


    }
}
