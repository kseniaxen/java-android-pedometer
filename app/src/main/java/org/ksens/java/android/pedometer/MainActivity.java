package org.ksens.java.android.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final double HUMAN_STEP_LENGTH_M = 0.7;
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
        Shader textShader = new LinearGradient(0, 0, 0, mainStepsTakenTextView.getTextSize(),
                new int[]{
                        Color.parseColor("#b860f4"),
                        Color.parseColor("#913cf5"),
                }, null, Shader.TileMode.CLAMP);
        mainStepsTakenTextView.getPaint().setShader(textShader);
        ImageButton mainStartButton = findViewById(R.id.mainStartButton);
        ImageButton mainPauseButton = findViewById(R.id.mainPauseButton);
        ImageButton mainResetButton = findViewById(R.id.mainResetButton);
        TextView mainKmTextView = findViewById(R.id.mainKmTextView);
        TextView mainTotalMaxTextView = findViewById(R.id.mainTotalMaxTextView);
        CircularProgressBar mainProgressCircular = findViewById(R.id.mainProgressCircular);
        Chronometer mainTimeChronometer = findViewById(R.id.mainTimeChronometer);
        TextView mainDateTextView = findViewById(R.id.mainDateTextView);

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("EEEE, d MMM", Locale.getDefault());
        mainDateTextView.setText(dateFormat.format(currentDate));

        loadData();
        mainTotalMaxTextView.setText(String.valueOf(loadDataGoal()));
        mainProgressCircular.setProgressMax(Float.valueOf(loadDataGoal()));
        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuStatistics:
                    saveDataTime();
                    Intent intentStatistics = new Intent(MainActivity.this, StatisticsActivity.class);
                    MainActivity.this.startActivity(intentStatistics);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuToday:
                    return true;
                case R.id.menuHistory:
                    saveDataTime();
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
                mainKmTextView.setText(String.valueOf(0));
                saveData();
                running = false;
                sensorManager.unregisterListener(MainActivity.this);
            }
        });

        mainTotalMaxTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialogApplyGoal);// Set up the input
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int value = Integer.parseInt(input.getText().toString());
                            saveDataGoal(value);
                            mainTotalMaxTextView.setText(String.valueOf(value));
                            mainProgressCircular.setProgressMax(Float.valueOf(value));
                        } catch (NumberFormatException e) {
                            dialog.cancel();
                            Toast.makeText(MainActivity.this,R.string.dialogError,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.primary));
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.primary));
                    }
                });
                dialog.show();
                return true;
            }
        });

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
            TextView mainKmTextView = findViewById(R.id.mainKmTextView);
            String formattedDouble = new DecimalFormat("#0.00").format(calcKilometers(currentSteps));
            mainKmTextView.setText(formattedDouble);
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

    private void saveDataTime(){
        Chronometer mainTimeChronometer = findViewById(R.id.mainTimeChronometer);
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("time",SystemClock.elapsedRealtime() - mainTimeChronometer.getBase());
        editor.apply();
    }

    private void saveDataGoal(int goal) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("goal",goal);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key", 0f);
        previousTotalSteps = savedNumber;
        long savedTime = sharedPreferences.getLong("time", 0L);
        pauseAt = savedTime;
    }

    private int loadDataGoal(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("goal", 2500);
    }

    private double calcKilometers(int steps){
        return (steps*HUMAN_STEP_LENGTH_M)/1000;
    }
}