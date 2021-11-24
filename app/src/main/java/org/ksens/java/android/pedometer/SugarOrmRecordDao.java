package org.ksens.java.android.pedometer;

import java.util.Collections;
import java.util.List;

public class SugarOrmRecordDao implements IRecordDao {
    @Override
    public List<RecordItem> findAll() {
        Global.items.clear();
        Global.items.addAll(RecordItem.listAll(RecordItem.class));
        return Global.items;
    }
    @Override
    public RecordItem findById(Long id) {
        return RecordItem.findById(RecordItem.class, id);
    }
    @Override
    public void save(RecordItem item) {
        item.save();
        findAll();
    }
    @Override
    public void delete(RecordItem item) {
        item.delete();
        findAll();
    }
    @Override
    public List<RecordItem> findAllReverse() {
        Global.items.clear();
        Global.items.addAll(RecordItem.listAll(RecordItem.class));
        Collections.reverse(Global.items);
        return Global.items;
    }
}
