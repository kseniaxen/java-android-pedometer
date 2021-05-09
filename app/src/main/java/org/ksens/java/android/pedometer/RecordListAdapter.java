package org.ksens.java.android.pedometer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecordListAdapter extends ArrayAdapter<RecordItem> {
    private LayoutInflater inflater;
    private int itemLayout;
    private List<RecordItem> items;
    private Context context;

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

        dateView.setText(new SimpleDateFormat("MM/dd").format(item.getDate()));
        String formattedDouble = new DecimalFormat("#0.00").format(transformTime(item.getTime()));
        timeView.setText(formattedDouble);
        stepsView.setText(String.valueOf(item.getSteps()));
        kmView.setText(String.valueOf(item.getKm()));
        return view;
    }

    private double transformTime(long time){
        return time/60000;
    }
}
