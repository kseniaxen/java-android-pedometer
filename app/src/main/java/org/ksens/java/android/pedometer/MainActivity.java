package org.ksens.java.android.pedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // константа: код запроса на переход к активити со статистикой
    // добавления новой задачи
    public static final int STATISTICS_ACTIVITY_REQUEST_CODE = 0;
    // константа: код запроса на переход к активити с историей
    // добавления новой задачи
    public static final int HISTORY_ACTIVITY_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.menuToday);

        bottomNavigationView.setOnNavigationItemSelectedListener((menuItem)->{
            switch(menuItem.getItemId()){
                case R.id.menuStatistics:
                    Intent intentStatistics = new Intent(MainActivity.this, StatisticsActivity.class);
                    MainActivity.this.startActivityForResult(intentStatistics, STATISTICS_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuToday:
                    return true;
                case R.id.menuHistory:
                    Intent intentHistory = new Intent(MainActivity.this, HistoryActivity.class);
                    MainActivity.this.startActivityForResult(intentHistory, HISTORY_ACTIVITY_REQUEST_CODE);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }
}