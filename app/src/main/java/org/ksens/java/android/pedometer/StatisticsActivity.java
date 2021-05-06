package org.ksens.java.android.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StatisticsActivity extends AppCompatActivity {

    // константа: код запроса на переход к главной активити
    // добавления новой задачи
    public static final int MAIN_ACTIVITY_REQUEST_CODE = 0;
    // константа: код запроса на переход к активити с историей
    // добавления новой задачи
    public static final int HISTORY_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menuStatistics);

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuStatistics:
                    return true;
                case R.id.menuToday:
                    Intent intentToday = new Intent(StatisticsActivity.this, MainActivity.class);
                    StatisticsActivity.this.startActivityForResult(intentToday, MAIN_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHistory:
                    Intent intentHistory = new Intent(StatisticsActivity.this, HistoryActivity.class);
                    StatisticsActivity.this.startActivityForResult(intentHistory, HISTORY_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }
}