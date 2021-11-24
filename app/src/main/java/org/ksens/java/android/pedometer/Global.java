package org.ksens.java.android.pedometer;

import java.util.ArrayList;
import java.util.List;

public class Global {
    public static List<RecordItem> items = new ArrayList<>();
    public static IRecordDao recordDao = new SugarOrmRecordDao();
}
