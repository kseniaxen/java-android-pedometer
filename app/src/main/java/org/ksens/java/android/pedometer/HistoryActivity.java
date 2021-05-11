package org.ksens.java.android.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryActivity extends AppCompatActivity {

    private RecordListAdapter adapter = null;
    private IRecordDao recordDao = Global.recordDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menuHistory);
        // представление списка
        ListView recordItemsListView = findViewById(R.id.historyRecordItemsListView);
        // пользовательский соединитель представления со списком данных
        adapter =
                new RecordListAdapter(this, R.layout.record_list_item, recordDao.findAllReverse());
        // подключение списка моделей данных к представлению списка
        recordItemsListView.setAdapter(adapter);

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuStatistics:
                    Intent intentStatistics = new Intent(HistoryActivity.this, StatisticsActivity.class);
                    HistoryActivity.this.startActivity(intentStatistics);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuToday:
                    Intent intentToday = new Intent(HistoryActivity.this, MainActivity.class);
                    HistoryActivity.this.startActivity(intentToday);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHistory:
                    return true;
            }
            return false;
        });

    }
}