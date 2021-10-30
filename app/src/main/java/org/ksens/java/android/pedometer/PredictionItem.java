package org.ksens.java.android.pedometer;

import com.github.signaflo.timeseries.TimePeriod;

import java.util.ArrayList;

public class PredictionItem {
    private ArrayList<RecordItem> RecordItemList;
    private String DateStart;
    private String DateEnd;
    private TimePeriod Period;
    private TimePeriod Seasonality;
    private Integer Forecast;

    public PredictionItem(){ }

    public PredictionItem(ArrayList<RecordItem> RecordItemList, String DateStart, String DateEnd, TimePeriod Period, TimePeriod Seasonality, Integer Forecast){
        this.RecordItemList = RecordItemList;
        this.DateStart = DateStart;
        this.DateEnd = DateEnd;
        this.Period = Period;
        this.Seasonality = Seasonality;
        this.Forecast = Forecast;
    }

    public void SetRecordItemList(ArrayList<RecordItem> RecordItemList){
        this.RecordItemList = RecordItemList;
    }

    public ArrayList<RecordItem> GetRecordItemList(){
        return RecordItemList;
    }

    public void SetDateStart(String DateStart){
        this.DateStart = DateStart;
    }

    public String GetDateStart(){
        return DateStart;
    }

    public void SetDateEnd(String DateEnd){
        this.DateEnd = DateEnd;
    }

    public String GetDateEnd(){
        return DateEnd;
    }

    public void SetPeriod(TimePeriod Period){
        this.Period = Period;
    }

    public TimePeriod GetPeriod(){
        return Period;
    }

    public void SetSeasonality(TimePeriod Seasonality){
        this.Seasonality = Seasonality;
    }

    public TimePeriod GetSeasonality(){
        return Seasonality;
    }

    public void SetForecast(Integer Forecast){
        this.Forecast = Forecast;
    }

    public Integer GetForecast(){
        return Forecast;
    }
}
