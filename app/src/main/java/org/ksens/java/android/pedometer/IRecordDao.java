package org.ksens.java.android.pedometer;

import java.util.List;

public interface IRecordDao {
    List<RecordItem> findAll ();
    RecordItem findById (Long id);
    void save (RecordItem item);
    void delete (RecordItem todoItem);
    List<RecordItem>findAllReverse();
}
