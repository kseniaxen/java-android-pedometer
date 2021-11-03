package org.ksens.java.android.pedometer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsActivity extends AppCompatActivity {

    private IRecordDao recordDao = Global.recordDao;
    private DecimalFormat df;
    private int KEY_WEEK = 7;
    private int KEY_MONTH = 12;
    private Button statisticsWeekButton;
    private Button statisticsMonthButton;
    private AnyChartView statisticsChartWeekAnyChartView;
    private TableLayout statisticsTableLayout;
    private final String CurrentDate =
            new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    @RequiresApi(api = Build.VERSION_CODES.O)
    private final String EndDateWeek = new SimpleDateFormat("dd.MM.yyyy").format(new Date().from(LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    @RequiresApi(api = Build.VERSION_CODES.O)
    private DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        statisticsTableLayout = findViewById(R.id.statisticsTableLayout);

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
                statisticsChartWeekAnyChartView.setVisibility(View.INVISIBLE);
                statisticsTableLayout.setVisibility(View.VISIBLE);
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
                        DateFormat dateFormatMonth = new SimpleDateFormat("MM.yyyy");
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
                            DateFormat dateFormatMonth = new SimpleDateFormat("MM.yyyy");
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
/*
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
                statisticsTableLayout.removeAllViews();
                TableRow row = (TableRow) LayoutInflater.from(StatisticsActivity.this).inflate(getResources().getLayout(R.layout.table_row), null);
                ((TextView)row.findViewById(R.id.attrib_name)).setText(getResources().getString(R.string.month));
                ((TextView)row.findViewById(R.id.attrib_value)).setText(getResources().getString(R.string.steps));
                ((TextView)row.findViewById(R.id.attrib_name)).setAllCaps(true);
                ((TextView)row.findViewById(R.id.attrib_value)).setAllCaps(true);
                ((TextView)row.findViewById(R.id.attrib_name)).setTextSize(18);
                ((TextView)row.findViewById(R.id.attrib_value)).setTextSize(18);
                row.setBackgroundColor(getResources().getColor(R.color.white));
                statisticsTableLayout.addView(row);
                for(int i = 0; i < uniqueMonths.size(); i++){
                    if(i >= KEY_MONTH){
                        break;
                    }
                    row = (TableRow) LayoutInflater.from(StatisticsActivity.this).inflate(getResources().getLayout(R.layout.table_row), null);
                    Date dateFormat = null;
                    try {
                        dateFormat = new SimpleDateFormat("MM.yyyy").parse(uniqueMonths.get(i));
                        DateFormat dateFormatMonth = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
                        ((TextView)row.findViewById(R.id.attrib_name)).setText(dateFormatMonth.format(dateFormat));
                        ((TextView)row.findViewById(R.id.attrib_value)).setText(recordItemSteps.get(i).toString());
                        statisticsTableLayout.addView(row);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                statisticsTableLayout.requestLayout();
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
                case R.id.menuGoals:
                    Intent intentGoals = new Intent(StatisticsActivity.this, GoalsActivity.class);
                    StatisticsActivity.this.startActivity(intentGoals);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onButtonWeekState(){
        statisticsTableLayout.setVisibility(View.INVISIBLE);
        statisticsChartWeekAnyChartView.setVisibility(View.VISIBLE);
        Cartesian cartesian = AnyChart.column();
        List<DataEntry> data = new ArrayList<>();
        statisticsMonthButton.setBackgroundColor(getResources().getColor(R.color.background));
        statisticsMonthButton.setTextColor(getResources().getColor(R.color.textToday));

        statisticsWeekButton.setBackgroundColor(getResources().getColor(R.color.textGoal));
        statisticsWeekButton.setTextColor(getResources().getColor(R.color.black));

        ArrayList<RecordItem> recordItems = new ArrayList<>();
        List<RecordItem> itemsQuery = RecordItem.findWithQuery(RecordItem.class, "SELECT * FROM RECORD_ITEM");

        LocalDate startDate = LocalDate.parse(EndDateWeek, DateFormat);
        LocalDate endDate = LocalDate.parse(CurrentDate, DateFormat);

        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);

        List<LocalDate> listOfDates = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(numOfDays)
                .collect(Collectors.toList());
        ArrayList<LocalDate> datesUse = new ArrayList<>();
        for(RecordItem recordItem: itemsQuery){
            if (listOfDates.stream().anyMatch(i -> i.format(DateFormat).equals(recordItem.getDate()))) {
                recordItems.add(recordItem);
                datesUse.add(LocalDate.parse(recordItem.getDate(),DateFormat));
            }
        }

        Set<LocalDate> mapDates = new HashSet<>(datesUse);
        datesUse.clear();
        datesUse.addAll(mapDates);

        ArrayList<LocalDate> listOfNoUseDates = new ArrayList<>();
        listOfDates.forEach(date -> {
            if(datesUse.stream().noneMatch(i -> i.equals(date))){
                listOfNoUseDates.add(date);
            }
        });
        listOfNoUseDates.forEach(date ->
                recordItems.add(new RecordItem(
                        0,
                        0L,
                        0.0,
                        date.format(DateFormat)
                ))
        );

        Map<String, RecordItem> recordItemMap = new HashMap<>();
        for(RecordItem recordItem : recordItems){
            RecordItem current = recordItemMap.get(recordItem.getDate());
            if(current == null){
                recordItemMap.put(recordItem.getDate(), recordItem);
            }else{
                current.setSteps(current.getSteps() + recordItem.getSteps());
            }
        }

        Collection<RecordItem> collection = recordItemMap.values();
        ArrayList<RecordItem> newRecordItems = new ArrayList<>(collection);
        newRecordItems.sort((a,b) -> ParseDate(a.getDate()).compareTo(ParseDate(b.getDate())));

        newRecordItems.forEach(item -> {
            data.add(new ValueDataEntry(item.getDate(), item.getSteps()));
        });

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

    private Date ParseDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}