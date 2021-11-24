package org.ksens.java.android.pedometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import androidx.annotation.RequiresApi;

import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.model.arima.Arima;
import com.github.signaflo.timeseries.model.arima.ArimaOrder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PredictionARIMA implements IPredictionARIMADao {
    @RequiresApi(api = Build.VERSION_CODES.O)
    private DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final double HUMAN_STEP_LENGTH_M = 0.7;
    private final Integer START_p = 0;
    private final Integer START_d = 0;
    private final Integer START_q = 0;
    private final Integer START_P = 0;
    private final Integer START_D = 0;
    private final Integer START_Q = 0;

    private final Integer END_p = 1;
    private final Integer END_d = 1;
    private final Integer END_q = 1;
    private final Integer END_P = 1;
    private final Integer END_D = 1;
    private final Integer END_Q = 1;

    private Integer WinsorizingValue = 0;
    private IRecordDao recordDao = Global.recordDao;

    public PredictionARIMA(int age){
        this.WinsorizingValue = GetWinsorizingValue(age);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public PredictionItem CreatePredictionItem(String dateStart, String dateEnd, TimePeriod period, TimePeriod seasonality, Integer forecast) {

        ArrayList<RecordItem> recordItems = new ArrayList<>();
        //List<RecordItem> itemsQuery = RecordItem.findWithQuery(RecordItem.class, "SELECT * FROM RECORD_ITEM");

        List<RecordItem> itemsQuery = recordDao.findAll();
        LocalDate startDate = LocalDate.parse(dateStart, DateFormat);
        LocalDate endDate = LocalDate.parse(dateEnd, DateFormat);

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
                    WinsorizingValue,
                    0L,
                    CalcKilometers(WinsorizingValue),
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

        for(RecordItem recordItem: newRecordItems){
            if(recordItem.getSteps() <= 0) {
                recordItem.setSteps(WinsorizingValue);
            }
        }

        //newRecordItems.forEach(recordItem -> Log.d("RECORD ", recordItem.getDate() + " " + recordItem.getSteps()));

        return new PredictionItem(recordItems, dateStart, dateEnd, period, seasonality, forecast);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public List<Integer> GetPredictionValues(PredictionItem predictionItem) {
        ArrayList<RecordItem> records = predictionItem.GetRecordItemList();
        double[] steps = new double[records.size()];
        int key = 0;
        for(RecordItem recordItem: records){
            steps[key] = recordItem.getSteps();
            //Log.d("STEPS",String.valueOf(steps[key]));
            key++;
        }

        TimeSeries series = TimeSeries.from(predictionItem.GetPeriod(),steps);
        ArrayList<ModelARIMA> models = new ArrayList<>();

        for(int i = START_p; i <= END_p; i++){
            for (int j = START_d; j <= END_d; j++) {
                for (int k = START_q; k <= END_q; k++) {
                    for (int l = START_P; l <= END_P; l++) {
                        for (int m = START_D; m <= END_D; m++) {
                            for (int n = START_Q; n <= END_Q; n++) {
                                ArimaOrder order = ArimaOrder.order(i, j, k, l, m, n);
                                Arima model = Arima.model(series, order, predictionItem.GetSeasonality());
                                Forecast forecast = model.forecast(predictionItem.GetForecast());
                                models.add(new ModelARIMA(forecast,model.aic(),i,j,k,l,m,n));
                            }
                        }
                    }
                }
            }
        }

        ModelARIMA chooseModel = models
                .stream()
                .min(Comparator.comparing(ModelARIMA::GetAic))
                .orElseThrow(NoSuchElementException::new);
        //Log.d("Model !!!!!!!", chooseModel.Steps + " " + chooseModel.Aic + " p " + chooseModel.p + " d "+ chooseModel.d + " q "+ chooseModel.q + " P "+ chooseModel.P + " D "+ chooseModel.D + " Q "+ chooseModel.Q + " ");

        ArrayList<Integer> predictionSteps = new ArrayList<>();
        chooseModel.Steps.pointEstimates().asList().forEach(step -> {
            int currentStep = (step > 0) ? step.intValue() : WinsorizingValue;
            predictionSteps.add(currentStep);
        });

        return predictionSteps;
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

    private double CalcKilometers(int steps){
        return (steps*HUMAN_STEP_LENGTH_M)/1000;
    }

    private int GetWinsorizingValue(int age){
        if(age <= 11){
            return 10000;
        }else if(12 <= age && age <= 19){
            return 9000;
        }else if(20 <= age && age <= 65) {
            return 7000;
        }else{
            return 5000;
        }
    }

}
