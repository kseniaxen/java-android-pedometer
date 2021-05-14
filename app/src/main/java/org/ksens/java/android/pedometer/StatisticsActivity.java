package org.ksens.java.android.pedometer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsActivity extends AppCompatActivity {

    private IRecordDao recordDao = Global.recordDao;
    private DecimalFormat df;
    private int KEY_WEEK=7;
    private int KEY_MONTH=12;
    private Button statisticsWeekButton;
    private Button statisticsMonthButton;
    private AnyChartView statisticsChartWeekAnyChartView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TextView statisticsTotalKmTextView = findViewById(R.id.statisticsTotalKmTextView);
        TextView statisticsTotalStepsTextView = findViewById(R.id.statisticsTotalStepsTextView);
        statisticsWeekButton = findViewById(R.id.statisticsWeekButton);
        statisticsMonthButton = findViewById(R.id.statisticsMonthButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        statisticsChartWeekAnyChartView = findViewById(R.id.statisticsChartWeekAnyChartView);
        TableLayout statisticsTableLayout = findViewById(R.id.statisticsTableLayout);

        Double totalKm = recordDao.findAll().stream().mapToDouble(x -> x.getKm()).sum();
        Integer totalSteps = recordDao.findAll().stream().mapToInt(x -> x.getSteps()).sum();
        df = new DecimalFormat("#.##");
        statisticsTotalKmTextView.setText(df.format(totalKm));
        statisticsTotalStepsTextView.setText(totalSteps.toString());

        onButtonWeekState();

        statisticsWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonWeekState();
            }
        });

        statisticsMonthButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*
                statisticsChartWeekAnyChartView.setVisibility(View.INVISIBLE);
                statisticsWeekButton.setBackgroundColor(getResources().getColor(R.color.background));
                statisticsWeekButton.setTextColor(getResources().getColor(R.color.textToday));
                statisticsMonthButton.setBackgroundColor(getResources().getColor(R.color.textGoal));
                statisticsMonthButton.setTextColor(getResources().getColor(R.color.black));

                List<RecordItem> recordItemsData = RecordItem.findWithQuery(RecordItem.class, "SELECT DISTINCT date FROM RECORD_ITEM");
                List<String> date = recordItemsData.stream().map(RecordItem::getDate).collect(Collectors.toList());

                List <String> months = new ArrayList<>();
                for (String d:date) {
                    Date dateFormat = null;
                    try {
                        dateFormat = new SimpleDateFormat("dd.MM.yyyy").parse(d);
                        DateFormat dateFormatMonth = new SimpleDateFormat("MM");
                        months.add(dateFormatMonth.format(dateFormat));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                List<Integer> recordItemSteps = new ArrayList<>();
                List<String> uniqueMonths = months.stream().distinct().collect(Collectors.toList());
                int steps=0;
                for (String month:uniqueMonths){
                    for (RecordItem recordItem:recordDao.findAll()) {
                        try {
                            Date dateFormat = new SimpleDateFormat("dd.MM.yyyy").parse(recordItem.getDate());
                            DateFormat dateFormatMonth = new SimpleDateFormat("MM");
                            if(dateFormatMonth.format(dateFormat).equals(month)){
                                steps += recordItem.getSteps();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    recordItemSteps.add(steps);
                    steps = 0;
                }

                Collections.reverse(recordItemSteps);
                Collections.reverse(uniqueMonths);

                for (int i = 0; i < uniqueMonths.size(); i++){
                    if(i >= KEY_MONTH){
                        break;
                    }
                    if(uniqueMonths.get(i).equals(null) && recordItemSteps.get(i).equals(null)){
                        data.add(new ValueDataEntry("0",0));
                    }else{
                        Log.d("DATA", uniqueMonths.get(i) + " "+ recordItemSteps.get(i).toString());
                        data.add(new ValueDataEntry(uniqueMonths.get(i), recordItemSteps.get(i)));
                    }
                }
                 */

            }
        });

        bottomNavigationView.setSelectedItemId(R.id.menuStatistics);
        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuStatistics:
                    return true;
                case R.id.menuToday:
                    Intent intentToday = new Intent(StatisticsActivity.this, MainActivity.class);
                    StatisticsActivity.this.startActivity(intentToday);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHistory:
                    Intent intentHistory = new Intent(StatisticsActivity.this, HistoryActivity.class);
                    StatisticsActivity.this.startActivity(intentHistory);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onButtonWeekState(){
        statisticsChartWeekAnyChartView.setVisibility(View.VISIBLE);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        statisticsMonthButton.setBackgroundColor(getResources().getColor(R.color.background));
        statisticsMonthButton.setTextColor(getResources().getColor(R.color.textToday));

        statisticsWeekButton.setBackgroundColor(getResources().getColor(R.color.textGoal));
        statisticsWeekButton.setTextColor(getResources().getColor(R.color.black));

        List<RecordItem> recordItemsData = RecordItem.findWithQuery(RecordItem.class, "SELECT DISTINCT date FROM RECORD_ITEM");
        List<String> date = recordItemsData.stream().map(RecordItem::getDate).collect(Collectors.toList());

        List<Integer> recordItemSteps = new ArrayList<>();
        int steps = 0;

        for (String d:date){
            for (RecordItem recordItem:recordDao.findAll()) {
                if(recordItem.getDate().equals(d)){
                    steps += recordItem.getSteps();
                }
            }
            recordItemSteps.add(steps);
            steps = 0;
        }

        Collections.reverse(recordItemSteps);
        Collections.reverse(date);

        //ProgressBar progress_circular = findViewById(R.id.progress_circular);
        //statisticsChartWeekAnyChartView.setProgressBar(progress_circular);

        for (int i = 0; i < date.size(); i++){
            if(i >= KEY_WEEK){
                break;
            }
            if(date.get(i).equals(null) && recordItemSteps.get(i).equals(null)){
                data.add(new ValueDataEntry("0",0));
            }else{
                data.add(new ValueDataEntry(date.get(i), recordItemSteps.get(i)));
            }
        }
        Column column = cartesian.column(data);
        column.color("purple");
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(0d)
                .format(getResources().getString(R.string.step) +": {%Value}{groupsSeparator: }");

        cartesian.animation(true);
        cartesian.title(getResources().getString(R.string.week_activity));


        cartesian.yScale().minimum(0d);

        // cartesian.yAxis(0).labels().format("${%Value}{groupsSeparator: }");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        cartesian.xAxis(0).title(getResources().getString(R.string.days));
        cartesian.yAxis(0).title(getResources().getString(R.string.steps));

        statisticsChartWeekAnyChartView.setChart(cartesian);
    }
}