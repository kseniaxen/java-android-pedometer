package org.ksens.java.android.pedometer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.signaflo.timeseries.TimePeriod;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GoalsActivity extends AppCompatActivity {
    private DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final String SelectedDateString =
            new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    private final String DateEndPrediction = new SimpleDateFormat("dd.MM.yyyy").format(new Date().from(LocalDate.now().minusDays(31).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    private final String DatesWeekEndPrediction =  new SimpleDateFormat("dd.MM.yyyy").format(new Date().from(LocalDate.now().plusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menuGoals);
        TableLayout goalsTableLayout = findViewById(R.id.goalsTableLayout);
        TextView goalsStepsTakenTextView = findViewById(R.id.goalsStepsTakenTextView);

        LocalDate startDate = LocalDate.parse(SelectedDateString, DateFormat);
        LocalDate endDate = LocalDate.parse(DatesWeekEndPrediction, DateFormat);

        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);

        List<LocalDate> listOfDates = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(numOfDays)
                .collect(Collectors.toList());
        ArrayList<String> days = new ArrayList<>();
        SimpleDateFormat formatDay = new SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault());
        listOfDates.forEach(date -> {
            days.add(formatDay.format(Date.from(date.atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant())));
        });

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                PredictionARIMA predictionARIMA = new PredictionARIMA(LoadDataAge());
                List<Integer> predictionSteps = predictionARIMA.PredictionPerMonthForOneDay(
                        predictionARIMA.CreatePredictionItem(
                                DateEndPrediction,
                                SelectedDateString,
                                TimePeriod.oneDay(),
                                TimePeriod.oneDay(),
                                7)
                );
                goalsStepsTakenTextView.post(new Runnable() {
                    public void run() {
                        goalsStepsTakenTextView.setText(
                                predictionSteps.get(0).toString()
                        );
                    }
                });
                goalsTableLayout.post(new Runnable() {
                    public void run() {
                        goalsTableLayout.removeAllViews();
                        TableRow row = (TableRow) LayoutInflater.from(GoalsActivity.this).inflate(getResources().getLayout(R.layout.table_row), null);
                        ((TextView) row.findViewById(R.id.attrib_name)).setAllCaps(false);
                        ((TextView) row.findViewById(R.id.attrib_value)).setAllCaps(false);
                        ((TextView) row.findViewById(R.id.attrib_name)).setText(getResources().getString(R.string.days));
                        ((TextView) row.findViewById(R.id.attrib_value)).setText(getResources().getString(R.string.steps));
                        ((TextView) row.findViewById(R.id.attrib_name)).setTextSize(20);
                        ((TextView) row.findViewById(R.id.attrib_value)).setTextSize(20);
                        ((TextView) row.findViewById(R.id.attrib_name)).setPadding(0, 0, 0, 15);
                        ((TextView) row.findViewById(R.id.attrib_value)).setPadding(0, 0, 0, 15);
                        row.setBackgroundColor(getResources().getColor(R.color.white));
                        goalsTableLayout.addView(row);
                        for (int i = 1; i < days.size(); i++) {
                            row = (TableRow) LayoutInflater.from(GoalsActivity.this).inflate(getResources().getLayout(R.layout.table_row), null);
                            ((TextView) row.findViewById(R.id.attrib_name)).setText(days.get(i));
                            ((TextView) row.findViewById(R.id.attrib_value)).setText(predictionSteps.get(i).toString());
                            ((TextView) row.findViewById(R.id.attrib_name)).setPadding(0, 0, 0, 10);
                            ((TextView) row.findViewById(R.id.attrib_value)).setPadding(0, 0, 0, 10);
                            row.setBackgroundColor(getResources().getColor(R.color.white));
                            goalsTableLayout.addView(row);
                        }
                        goalsTableLayout.requestLayout();
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem) -> {
            switch (menuItem.getItemId()) {
                case R.id.menuGoals:
                    return true;
                case R.id.menuToday:
                    Intent intentToday = new Intent(GoalsActivity.this, MainActivity.class);
                    GoalsActivity.this.startActivity(intentToday);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.menuHistory:
                    Intent intentHistory = new Intent(GoalsActivity.this, HistoryActivity.class);
                    GoalsActivity.this.startActivity(intentHistory);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.menuStatistics:
                    Intent intentStatistics = new Intent(GoalsActivity.this, StatisticsActivity.class);
                    GoalsActivity.this.startActivity(intentStatistics);
                    overridePendingTransition(0, 0);
                    return true;
                case R.id.menuSettings:
                    Intent intentSettings = new Intent(GoalsActivity.this, SettingsActivity.class);
                    GoalsActivity.this.startActivity(intentSettings);
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        });
    }

    public int LoadDataAge(){
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("age",0);
    }
}
