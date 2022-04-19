/**
 *
 */
package com.datastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param <K>
 * @param <V>
 */
public class MyLinearMap<K, V> implements Map<K, V> {
    private List<Entry> entries = new ArrayList<Entry>();

    public class Entry implements Map.Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() { return key; }
        @Override
        public V getValue() { return value; }
        @Override
        public V setValue(V newValue) {
            value = newValue;

            return newValue;
        }
    }

    @Override
    public void clear() { entries.clear(); }

    @Override
    public boolean containsKey(Object target) { return findEntry(target) != null; }

    /**
     *
     * @param target
     * @return
     */
    private Entry findEntry(Object target) {
        for(Entry entry: entries) {
            if(equals(target, entry.getKey())) {
                return entry;
            }
        }
        return null;
    }

    private boolean equals(Object target, Object obj) {
        if(target == null) {
            return obj == null;
        }

        return target.equals(obj);
    }

    @Override
    public boolean containsValue(Object target) {
        for(Map.Entry<K, V> entry: entries) {
            if(equals(target, entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() { throw new UnsupportedOperationException(); }

    @Override
    public V get(Object key) {
        for(Entry entry: entries) {
            if(equals(key, entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public boolean isEmpty() { return entries.isEmpty(); }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<K>();
        for(Entry entry: entries) {
            set.add(entry.getKey());
        }

        return set;
    }

    @Override
    public V put(K key, V value) {
        for(Entry entry : entries) {
            if(equals(key, entry.getKey())) {
                entry.setValue(value);

                return value;
            }
        }
        Entry e = new Entry(key, value);
        entries.add(e);

        return value;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(Map.Entry<? extends K, ? extends V> entry: map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        Entry e = findEntry(key);

        if(e == null) {
            return null;
        } else {
            V value = e.getValue();
            entries.remove(e);

            return value;
        }
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Collection<V> values() {
        Set<V> set = new HashSet<V>();
        for(Entry entry: entries) {
            set.add(entry.getValue());
        }

        return set;
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new MyLinearMap<String, Integer>();

        map.put("Word1", 1);
        map.put("Word2", 2);

        Integer value = map.get("Word1");
        System.out.println(value);

        for(String key: map.keySet()) {
            System.out.println(key + ", " + map.get(key));
        }
    }

    protected Collection<? extends java.util.Map.Entry<K, V>> getEntries() {
        return entries;
    }

}

