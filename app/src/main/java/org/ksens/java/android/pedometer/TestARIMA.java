package org.ksens.java.android.pedometer;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.model.arima.Arima;
import com.github.signaflo.timeseries.model.arima.ArimaOrder;
import com.github.signaflo.timeseries.forecast.Forecast;

public class TestARIMA {
    private IRecordDao recordDao = Global.recordDao;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TestARIMA(){
        int size=0;
        for (RecordItem recordItem: recordDao.findAll()){
            if (recordItem.getSteps()>1000){
                size++;
            }
        }
        double[] values = new double[size];
        int i=0;
        for(RecordItem recordItem:recordDao.findAll()){
            if(recordItem.getSteps()>1000) {
                values[i] = recordItem.getSteps();
                Log.d("Val " + i, String.valueOf(values[i]));
                i++;
            }
        }
        TimePeriod day = TimePeriod.oneDay();

        TimeSeries series = TimeSeries.from(day,values);

        ArimaOrder order = ArimaOrder.order(0, 0, 0, 1, 1, 1);

        TimePeriod week = TimePeriod.oneWeek();

        Arima model = Arima.model(series, order, week);

        Forecast forecast = model.forecast(1);
        Log.d("AIC ", String.valueOf(model.aic())); // Get and display the model AIC
        Log.d("Coef ",String.valueOf(model.coefficients())); // Get and display the estimated coefficients
        Log.d("Errors ",java.util.Arrays.toString(model.stdErrors()));
        Log.d("Forecast",String.valueOf(forecast));
    }
}
