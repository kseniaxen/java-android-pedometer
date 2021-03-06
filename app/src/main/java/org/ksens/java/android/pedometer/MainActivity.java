package org.ksens.java.android.pedometer;

import androidx.annotation.RequiresApi;
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
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.signaflo.timeseries.TimePeriod;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private final double HUMAN_STEP_LENGTH_M = 0.7;
    private final Integer MIN_AGE = 1;
    private final Integer MAX_AGE = 100;
    private SensorManager sensorManager;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private Integer Age = MIN_AGE;
    private Long pauseAt = 0L;
    private final String selectedDateString =
            new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    private final String DateEndPrediction = new SimpleDateFormat("dd.MM.yyyy").format(new Date().from(LocalDate.now().minusDays(31).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    private IRecordDao recordDao = Global.recordDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(LoadDataAge() <= 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.dialogApplyAge);
            builder.setCancelable(false);
            final NumberPicker numberPicker = new NumberPicker(MainActivity.this);
            numberPicker.setMinValue(MIN_AGE);
            numberPicker.setMaxValue(MAX_AGE);
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
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PredictionARIMA predictionARIMA = new PredictionARIMA(LoadDataAge());
                List<Integer> predictionSteps = predictionARIMA.GetPredictionValues(
                        predictionARIMA.CreatePredictionItem(
                                DateEndPrediction,
                                selectedDateString,
                                TimePeriod.oneDay(),
                                TimePeriod.oneDay(),
                                1)
                );
                mainTotalMaxTextView.post(new Runnable() {
                    public void run() {
                        mainTotalMaxTextView.setText(
                                predictionSteps.get(0).toString()
                        );
                        mainProgressCircular.setProgressMax(predictionSteps.get(0).floatValue());
                        saveTodaySteps(predictionSteps.get(0));
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
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
                case R.id.menuGoals:
                    saveDataTime();
                    Intent intentGoals = new Intent(MainActivity.this, GoalsActivity.class);
                    MainActivity.this.startActivity(intentGoals);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuSettings:
                    saveDataTime();
                    Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                    MainActivity.this.startActivity(intentSettings);
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
                long resetTimeMemory = SystemClock.elapsedRealtime() - mainTimeChronometer.getBase();
                mainTimeChronometer.setBase(SystemClock.elapsedRealtime());
                mainTimeChronometer.stop();
                pauseAt = 0L;
                mainPauseButton.setVisibility(View.INVISIBLE);
                mainPauseButton.setEnabled(false);
                mainResetButton.setVisibility(View.INVISIBLE);
                mainResetButton.setEnabled(false);
                mainStartButton.setVisibility(View.VISIBLE);
                mainStartButton.setEnabled(true);
                Integer currentSteps = (int)totalSteps - (int)previousTotalSteps;
                if(currentSteps > 0){
                    recordDao.save(new RecordItem(
                            currentSteps,
                            resetTimeMemory,
                            calcKilometers(currentSteps),
                            selectedDateString
                    ));
                    Toast.makeText(MainActivity.this,R.string.addRecord,Toast.LENGTH_LONG).show();
                }
                previousTotalSteps = totalSteps;
                mainStepsTakenTextView.setText(String.valueOf(0));
                mainProgressCircular.setProgressWithAnimation(0f);
                mainKmTextView.setText(String.valueOf(0));
                saveData();
                running = false;
                sensorManager.unregisterListener(MainActivity.this);
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
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
    public void SaveDataAge(int age) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("age", age);
        editor.apply();
    }
    private void saveTodaySteps(int steps) {
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("steps_today",steps);
        editor.apply();
    }
    public int LoadDataAge(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("age",0);
    }
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key", 0f);
        previousTotalSteps = savedNumber;
        long savedTime = sharedPreferences.getLong("time", 0L);
        pauseAt = savedTime;
    }
    private double calcKilometers(int steps){
        return (steps*HUMAN_STEP_LENGTH_M)/1000;
    }
}