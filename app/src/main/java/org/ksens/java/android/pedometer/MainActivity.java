package org.ksens.java.android.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;

    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;

    private Long pauseAt = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menuToday);

        TextView mainStepsTakenTextView = findViewById(R.id.mainStepsTakenTextView);
        Button mainStartButton = findViewById(R.id.mainStartButton);
        Button mainPauseButton = findViewById(R.id.mainPauseButton);
        Button mainResetButton = findViewById(R.id.mainResetButton);
        CircularProgressBar mainProgressCircular = findViewById(R.id.mainProgressCircular);
        Chronometer mainTimeChronometer = findViewById(R.id.mainTimeChronometer);

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuStatistics:
                    Intent intentStatistics = new Intent(MainActivity.this, StatisticsActivity.class);
                    MainActivity.this.startActivity(intentStatistics);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuToday:
                    return true;
                case R.id.menuHistory:
                    Intent intentHistory = new Intent(MainActivity.this, HistoryActivity.class);
                    MainActivity.this.startActivity(intentHistory);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

        mainStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainTimeChronometer.setBase(SystemClock.elapsedRealtime() - pauseAt);
                mainTimeChronometer.start();
                Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
                sensorManager.registerListener(MainActivity.this,stepSensor,SensorManager.SENSOR_DELAY_UI);

                mainPauseButton.setVisibility(View.VISIBLE);
                mainPauseButton.setEnabled(true);

                mainResetButton.setVisibility(View.VISIBLE);
                mainResetButton.setEnabled(true);

                mainStartButton.setVisibility(View.INVISIBLE);
                mainStartButton.setEnabled(false);
                running = true;
            }
        });

        mainPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAt = SystemClock.elapsedRealtime() - mainTimeChronometer.getBase();
                mainTimeChronometer.stop();
                sensorManager.unregisterListener(MainActivity.this);
                mainStartButton.setVisibility(View.VISIBLE);
                mainStartButton.setEnabled(true);
                running = false;
            }
        });

        mainResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainTimeChronometer.setBase(SystemClock.elapsedRealtime());
                mainTimeChronometer.stop();
                pauseAt = 0L;
                mainPauseButton.setVisibility(View.INVISIBLE);
                mainPauseButton.setEnabled(false);

                mainResetButton.setVisibility(View.INVISIBLE);
                mainResetButton.setEnabled(false);

                mainStartButton.setVisibility(View.VISIBLE);
                mainStartButton.setEnabled(true);

                previousTotalSteps = totalSteps;
                mainStepsTakenTextView.setText(String.valueOf(0));
                mainProgressCircular.setProgressWithAnimation(0f);
                saveData();
                running = false;
                sensorManager.unregisterListener(MainActivity.this);
            }
        });
        loadData();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(running){
            Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if(stepSensor == null){
                Toast.makeText(this,"No sensor detected on this device",Toast.LENGTH_SHORT).show();
            }else{
                sensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
        sensorManager.unregisterListener(MainActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!running){
            sensorManager.unregisterListener(MainActivity.this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(running){
            totalSteps = event != null ? event.values[0] : 0;
            Integer currentSteps = (int)totalSteps - (int)previousTotalSteps;
            TextView mainStepsTakenTextView = findViewById(R.id.mainStepsTakenTextView);
            mainStepsTakenTextView.setText(String.valueOf(currentSteps));
            CircularProgressBar mainProgressCircular = findViewById(R.id.mainProgressCircular);
            mainProgressCircular.setProgress(currentSteps.floatValue());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key",previousTotalSteps);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key", 0f);
        previousTotalSteps = savedNumber;

    }
}