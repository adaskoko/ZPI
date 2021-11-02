package com.example.zpi.bottomnavigation.ui.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HashList<K, V> {
    private ArrayList<K> keys;
    private ArrayList<ArrayList<V>> values;

    public HashList() {
        keys = new ArrayList<>();
        values = new ArrayList<>();
    }

    public boolean put(K key, V value) {
        if (keys.contains(key)) {
            int index = keys.indexOf(key);
            values.get(index).add(value);
            return true;
        } if (!keys.contains(key)) {
            keys.add(key);
            values.add(new ArrayList<>(Collections.singletonList(value)));
            return true;
        }
        return false;
    }

    public List<Section> getSections() {
        ArrayList<Section> sections = new ArrayList<>();

        for (int i = 0; i < keys.size(); i++) {
            sections.add(new Section(keys.get(i), values.get(i)));
        }
        return sections;
    }

    public boolean checkList() {
        return keys.size() == values.size();
    }
}
