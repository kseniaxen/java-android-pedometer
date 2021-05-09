package org.ksens.java.android.pedometer;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordItem extends SugarRecord {
    //количество шагов
    private Integer steps;
    //количество минут ходьбы в миллисекундах
    private Long time;
    //пройдено километров
    private Double km;
    //дата
    private  String date;

    public RecordItem() {

    }

    public RecordItem(Integer steps, Long time, Double km, String date) {
        this.steps = steps;
        this.time = time;
        this.km = km;
        this.date = date;
    }

    public RecordItem(Integer steps, Long time, Double km) {
        this.steps = steps;
        this.time = time;
        this.km = km;
        this.date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    }

    public Integer getSteps() {
        return steps;
    }

    public void setSteps(Integer steps) {
        this.steps = steps;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getKm() {
        return km;
    }

    public void setKm(Double km) {
        this.km = km;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RecordItem {" +
                "id= " + id +
                ", steps= '" + steps + '\'' +
                ", time= '" + time + '\'' +
                ", km= '" + km + '\'' +
                ", date= '" + date + '\'' +
                '}';
    }
}
