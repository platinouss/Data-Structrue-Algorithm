package com.datastructure;

import java.util.List;
import java.util.Map;

public class MyHashMap<K, V> extends MyBetterMap<K, V> implements Map<K, V> {
    protected static final double FACTOR = 1.0;

    @Override
    public V put(K key, V value) {
        V oldValue = super.put(key, value);

        if(size() > maps.size() * FACTOR) {
            rehash();
        }

        return oldValue;
    }

    protected void rehash() {
        List<MyLinearMap<K, V>> oldMaps = maps;

        int newK = maps.size() * 2;
        makeMaps(newK);

        for(MyLinearMap<K, V> map: oldMaps) {
            for(Map.Entry<K, V> entry : map.getEntries()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new MyHashMap<>();

        for(int i=0; i<10; i++) {
            map.put(new Integer(i).toString(), i);
        }

        Integer value = map.get("3");
        System.out.println(value);
    }
}
