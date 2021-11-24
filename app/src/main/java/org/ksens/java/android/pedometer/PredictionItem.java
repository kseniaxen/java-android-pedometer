package org.ksens.java.android.pedometer;

import com.github.signaflo.timeseries.TimePeriod;

import java.util.ArrayList;

public class PredictionItem {
    /** Список RecordItem записанных данных с БД - RecordItemList*/
    private ArrayList<RecordItem> RecordItemList;
    /** Начало периода (дата) прогнозирования -  DateStart*/
    private String DateStart;
    /** Конец периода (дата) прогнозирования -  DateEnd*/
    private String DateEnd;
    /** Единица значения периода за который производится прогнозирования -  Period*/
    private TimePeriod Period;
    /** Сезонность данных за который производится прогнозирования -  Seasonality*/
    private TimePeriod Seasonality;
    /** Количество прогнозированных периодов -  Forecast*/
    private Integer Forecast;
    /**
     * Конструктор - создание нового объекта PredictionItem
     */
    public PredictionItem(){ }
    /**
     * Конструктор - создание нового объекта PredictionItem с параметрами
     * @param RecordItemList - список RecordItem записанных данных с БД
     * @param DateStart - начало периода (дата) прогнозирования
     * @param DateEnd - конец периода (дата) прогнозирования
     * @param Period - единица значения периода за который производится прогнозирования
     * @param Seasonality - сезонность данных за который производится прогнозирования
     * @param Forecast - количество прогнозированных периодов
     */
    public PredictionItem(ArrayList<RecordItem> RecordItemList, String DateStart, String DateEnd, TimePeriod Period, TimePeriod Seasonality, Integer Forecast){
        this.RecordItemList = RecordItemList;
        this.DateStart = DateStart;
        this.DateEnd = DateEnd;
        this.Period = Period;
        this.Seasonality = Seasonality;
        this.Forecast = Forecast;
    }
    /**
     * Процедура определения списка  {@link PredictionItem#RecordItemList}
     * @param RecordItemList - список RecordItem записанных данных с БД
     */
    public void SetRecordItemList(ArrayList<RecordItem> RecordItemList){
        this.RecordItemList = RecordItemList;
    }
    /**
     * Функция получения значения поля {@link PredictionItem#RecordItemList}
     * @return возвращаем список
     */
    public ArrayList<RecordItem> GetRecordItemList(){
        return RecordItemList;
    }
    /**
     * Процедура определения начальной даты прогнозирования {@link PredictionItem#DateStart}
     * @param DateStart - начало периода (дата) прогнозирования
     */
    public void SetDateStart(String DateStart){
        this.DateStart = DateStart;
    }
    /**
     * Функция получения значения поля {@link PredictionItem#DateStart}
     * @return возвращаем начальной даты
     */
    public String GetDateStart(){
        return DateStart;
    }
    /**
     * Процедура определения конечной даты прогнозирования {@link PredictionItem#DateEnd}
     * @param DateEnd - конец периода (дата) прогнозирования
     */
    public void SetDateEnd(String DateEnd){
        this.DateEnd = DateEnd;
    }
    /**
     * Функция получения значения поля {@link PredictionItem#DateEnd}
     * @return возвращаем конечной даты
     */
    public String GetDateEnd(){
        return DateEnd;
    }
    /**
     * Процедура определения единицы значения периода за который производится прогнозирования {@link PredictionItem#Period}
     * @param Period - единица значения периода за который производится прогнозирования
     */
    public void SetPeriod(TimePeriod Period){
        this.Period = Period;
    }
    /**
     * Функция получения значения поля {@link PredictionItem#Period}
     * @return возвращаем единицы значения периода
     */
    public TimePeriod GetPeriod(){
        return Period;
    }
    /**
     * Процедура определения сезонность данных за который производится прогнозирования {@link PredictionItem#Seasonality}
     * @param Seasonality - сезонность данных за который производится прогнозирования
     */
    public void SetSeasonality(TimePeriod Seasonality){
        this.Seasonality = Seasonality;
    }
    /**
     * Функция получения значения поля {@link PredictionItem#Seasonality}
     * @return возвращаем сезонность
     */
    public TimePeriod GetSeasonality(){
        return Seasonality;
    }
    /**
     * Процедура определения количество прогнозированных периодов {@link PredictionItem#Forecast}
     * @param Forecast - количество прогнозированных периодов
     */
    public void SetForecast(Integer Forecast){
        this.Forecast = Forecast;
    }
    /**
     * Функция получения значения поля {@link PredictionItem#Forecast}
     * @return возвращаем количество прогнозированных периодов
     */
    public Integer GetForecast(){
        return Forecast;
    }
}
