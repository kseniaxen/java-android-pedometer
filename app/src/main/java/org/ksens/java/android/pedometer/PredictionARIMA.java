package org.ksens.java.android.pedometer;

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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PredictionARIMA implements IPredictionARIMADao {
    @RequiresApi(api = Build.VERSION_CODES.O)
    private DateTimeFormatter DateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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

    private final Integer WINSORIZING_BOTTOM_LINE = 1000;

    public class ModelARIMA {
        public Double Steps;
        public Double Aic;
        public Integer p;
        public Integer d;
        public Integer q;
        public Integer P;
        public Integer D;
        public Integer Q;
        public ModelARIMA(Double Steps, Double Aic, Integer p, Integer d, Integer q, Integer P, Integer D, Integer Q){
            this.Steps = Steps;
            this.Aic = Aic;
            this.p = p;
            this.d = d;
            this.q = q;
            this.P = P;
            this.D = D;
            this.Q = Q;
        }
        public Double GetAic(){
            return this.Aic;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public PredictionItem CreatePredictionItem(String dateStart, String dateEnd, TimePeriod period, TimePeriod seasonality, Integer forecast) {
        ArrayList<RecordItem> recordItems = new ArrayList<>();
        List<RecordItem> itemsQuery = RecordItem.findWithQuery(RecordItem.class, "SELECT * FROM RECORD_ITEM");

        LocalDate startDate = LocalDate.parse(dateStart, DateFormat);
        LocalDate endDate = LocalDate.parse(dateEnd, DateFormat);

        long numOfDays = ChronoUnit.DAYS.between(startDate, endDate);

        List<LocalDate> listOfDates = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(numOfDays)
                .collect(Collectors.toList());
        for(RecordItem recordItem: itemsQuery){
            if (listOfDates.stream().anyMatch(i -> i.format(DateFormat).equals(recordItem.getDate()))) {
                recordItems.add(recordItem);
            }
        }

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
            if(recordItem.getSteps() < WINSORIZING_BOTTOM_LINE) {
                recordItem.setSteps(WINSORIZING_BOTTOM_LINE);
            }
        }

        //newRecordItems.forEach(recordItem -> Log.d("RECORD ", recordItem.getDate() + " " + recordItem.getSteps()));

        return new PredictionItem(recordItems, dateStart, dateEnd, period, seasonality, forecast);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Integer PredictionPerMonthForOneDay(PredictionItem predictionItem) {
        ArrayList<RecordItem> records = predictionItem.GetRecordItemList();
        double[] steps = new double[records.size()];
        int key = 0;
        for(RecordItem recordItem: records){
            steps[key] = recordItem.getSteps();
            Log.d("STEPS",String.valueOf(steps[key]));
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
                                models.add(new ModelARIMA(forecast.pointEstimates().mean(),model.aic(),i,j,k,l,m,n));
                            }
                        }
                    }
                }
            }
        }
/*
        ArimaOrder order = ArimaOrder.order(0, 1, 0, 0, 0, 0);
        Arima model = Arima.model(series, order, predictionItem.GetSeasonality());
        Forecast forecast = model.forecast(predictionItem.GetForecast());
        models.add(new ModelARIMA(forecast.pointEstimates().mean(),model.aic(),0, 1, 1, 1, 1, 1));
 */
        models.forEach(model -> {
            Log.d("Model", model.Steps + " " + model.Aic + " p " + model.p + " d "+ model.d + " q "+ model.q + " P "+ model.P + " D "+ model.D + " Q "+ model.Q + " ");
        });

        ModelARIMA chooseModel = models
                .stream()
                .min(Comparator.comparing(ModelARIMA::GetAic))
                .orElseThrow(NoSuchElementException::new);
        Log.d("Model !!!!!!!", chooseModel.Steps + " " + chooseModel.Aic + " p " + chooseModel.p + " d "+ chooseModel.d + " q "+ chooseModel.q + " P "+ chooseModel.P + " D "+ chooseModel.D + " Q "+ chooseModel.Q + " ");

        return chooseModel.Steps.intValue();
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
