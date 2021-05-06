package org.ksens.java.android.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HistoryActivity extends AppCompatActivity {

    // константа: код запроса на переход к главной активити
    // добавления новой задачи
    public static final int MAIN_ACTIVITY_REQUEST_CODE = 0;
    // константа: код запроса на переход к активити со статистикой
    // добавления новой задачи
    public static final int STATISTICS_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menuHistory);

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuStatistics:
                    Intent intentStatistics = new Intent(HistoryActivity.this, StatisticsActivity.class);
                    HistoryActivity.this.startActivityForResult(intentStatistics, STATISTICS_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuToday:
                    Intent intentToday = new Intent(HistoryActivity.this, MainActivity.class);
                    HistoryActivity.this.startActivityForResult(intentToday, MAIN_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHistory:
                    return true;
            }
            return false;
        });
    }
}