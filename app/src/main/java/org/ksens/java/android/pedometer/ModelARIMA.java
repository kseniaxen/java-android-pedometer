package org.ksens.java.android.pedometer;

import com.github.signaflo.timeseries.forecast.Forecast;

public class ModelARIMA {
    public Forecast Steps;
    public Double Aic;
    public Integer p;
    public Integer d;
    public Integer q;
    public Integer P;
    public Integer D;
    public Integer Q;
    public ModelARIMA(Forecast Steps, Double Aic, Integer p, Integer d, Integer q, Integer P, Integer D, Integer Q){
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
