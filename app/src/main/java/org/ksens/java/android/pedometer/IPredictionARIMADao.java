package org.ksens.java.android.pedometer;

import com.github.signaflo.timeseries.TimePeriod;

import java.util.List;

public interface IPredictionARIMADao {
    PredictionItem CreatePredictionItem(String DateStart, String DateEnd, TimePeriod Period, TimePeriod Seasonality, Integer Forecast);
    List<Integer> PredictionPerMonthForOneDay(PredictionItem predictionItem);
}
