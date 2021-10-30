package org.ksens.java.android.pedometer;

import com.github.signaflo.timeseries.TimePeriod;

public interface IPredictionARIMADao {
    PredictionItem CreatePredictionItem(String DateStart, String DateEnd, TimePeriod Period, TimePeriod Seasonality, Integer Forecast);
    Integer PredictionPerMonthForOneDay(PredictionItem predictionItem);
}
