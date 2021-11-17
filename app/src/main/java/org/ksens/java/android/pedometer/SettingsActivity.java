package org.ksens.java.android.pedometer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {
    private final Integer MIN_AGE = 1;
    private final Integer MAX_AGE = 100;
    private Integer Age = MIN_AGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menuSettings);
        Button settingsAgeButton = findViewById(R.id.settingsAgeButton);

        settingsAgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(R.string.dialogApplyAge);
                final NumberPicker numberPicker = new NumberPicker(SettingsActivity.this);
                numberPicker.setMinValue(MIN_AGE);
                numberPicker.setMaxValue(MAX_AGE);
                numberPicker.setValue(LoadDataAge());
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        Age = newVal;
                    }
                });
                builder.setView(numberPicker);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SaveDataAge(Age);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
                    }
                });
                dialog.show();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuSettings:
                    return true;
                case R.id.menuToday:
                    Intent intentToday = new Intent(SettingsActivity.this, MainActivity.class);
                    SettingsActivity.this.startActivity(intentToday);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHistory:
                    Intent intentHistory = new Intent(SettingsActivity.this, HistoryActivity.class);
                    SettingsActivity.this.startActivity(intentHistory);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuStatistics:
                    Intent intentStatistics = new Intent(SettingsActivity.this, StatisticsActivity.class);
                    SettingsActivity.this.startActivity(intentStatistics);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuGoals:
                    Intent intentGoals = new Intent(SettingsActivity.this, GoalsActivity.class);
                    SettingsActivity.this.startActivity(intentGoals);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    private void SaveDataAge(int age) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("age", age);
        editor.apply();
    }

    private int LoadDataAge(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("age",0);
    }
}
