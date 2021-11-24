package org.ksens.java.android.pedometer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordListAdapter extends ArrayAdapter<RecordItem> {
    private LayoutInflater inflater;
    private int itemLayout;
    private List<RecordItem> items;
    private Context context;
    private DecimalFormat df;
    // конструктор, который принимает ссылку на активити, дескриптор представления,
    // список моделей данных
    public RecordListAdapter(@NonNull Context context, int resource, @NonNull List<RecordItem> objects) {
        // вызов родительской версии конструктора
        super(context, resource, objects);
        // инициализация поля инфлейтера объектом инфлейтера, настроенным
        // на работу с определенной активити при помощи аргумента context
        this.inflater = LayoutInflater.from(context);
        // инициализация поля дескриптора представления
        // дескриптором макета пункта списка
        this.itemLayout = resource;
        // инициализация поля списка моделей данных
        this.items = objects;
        // инициализация поля графического контекста
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = inflater.inflate(this.itemLayout, parent, false);
        } else {
            view = convertView;
        }
        TextView dateView = view.findViewById(R.id.recordListItemDateTextView);
        TextView timeView = view.findViewById(R.id.recordListItemTimeTextView);
        TextView stepsView = view.findViewById(R.id.recordListItemStepsTextView);
        TextView kmView = view.findViewById(R.id.recordListItemKmTextView);
        //с конца списка (индекс, начиная с последнего)
        RecordItem item = items.get(position);
        DateFormat outputFormat = new SimpleDateFormat("MM/dd");
        DateFormat inputFormat = new SimpleDateFormat("dd.MM.yyyy");
        String inputText = item.getDate();
        Date date = null;
        try {
            date = inputFormat.parse(inputText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputText = outputFormat.format(date);
        dateView.setText(outputText);
        SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(item.getTime());
        timeView.setText(timeFormat.format(calendar.getTime()));
        stepsView.setText(item.getSteps().toString());
        df = new DecimalFormat("#.##");
        kmView.setText(df.format(item.getKm()));
        return view;
    }
}
