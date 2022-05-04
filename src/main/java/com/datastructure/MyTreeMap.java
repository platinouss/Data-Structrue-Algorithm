package com.datastructure;

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MyTreeMap<K, V> implements Map<K, V> {
    private int size = 0;
    private Node root = null;

    protected class Node {
        public K key;
        public V value;
        public Node left = null;
        public Node right = null;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(Object target) {
        return findNode(target) != null;
    }

    private Node findNode(Object target) {
        if (target == null) {
            throw new IllegalArgumentException();
        }

        @SuppressWarnings("unchecked")
        Comparable <? super K> k = (Comparable<? super K>) target;

        Node node = root;
        while (node != null) {
            int cmp = k.compareTo(node.key);
            // k가 node.key값 보다 클 때 cmp 값은 1
            // k가 node.key값 보다 작을 때 cmp 값은 -1
            // k와 node.key값 같을 때 0
            if (cmp < 0)
                node = node.left;
            else if (cmp > 0)
                node = node.right;
            else
                return node;
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
        return containsValueHelper(root, target);
    }

    private boolean containsValueHelper(Node node, Object target) {
        if (node == null) {
            return false;
        }
        if (equals(target, node.value)) {
            return true;
        }
        if (containsValueHelper(node.left, target)) {
            return true;
        }
        if (containsValueHelper(node.right, target)) {
            return true;
        }
        return false;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V get(Object key) {
        Node node = findNode(key);
        if(node == null) {
            return null;
        }

        return node.value;
    }

    @Override
    public boolean isEmpty() { return size == 0; }

    @Override
    public Set<K> keySet() {
        Set<K> set = new LinkedHashSet<K>();
        addInOrder(root, set);

        return set;
    }

    private void addInOrder(Node node, Set<K> set) {
        if (node == null) return;
        addInOrder(node.left, set);
        set.add(node.key);
        addInOrder(node.right, set);
    }

    @Override
    public V put(K key, V value) {
        if(key == null) {
            throw new NullPointerException();
        }

        if(root == null) {
            root = new Node(key, value);
            size++;

            return null;
        }

        return putHelper(root, key, value);
    }

    private V putHelper(Node node, K key, V value) {
        @SuppressWarnings("unchecked")
        Comparable <? super K> k = (Comparable<? super K>) key;
        int cmp = k.compareTo(node.key);

        if(cmp < 0) {
            if(node.left == null) {
                node.left = new Node(key, value);
                size++;

                return null;
            } else {
                putHelper(node.left, key, value);
            }
        }

        if(cmp > 0) {
            if(node.right == null) {
                node.right = new Node(key, value);
                size++;

                return null;
            } else {
                putHelper(node.right, key, value);
            }
        }

        V oldValue = node.value;
        node.value = value;

        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(Map.Entry<? extends K, ? extends V> entry: map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        //OPTIONAL TODO

        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Collection<V> values() {
        Set<V> set = new HashSet<V>();
        Deque<Node> stack = new LinkedList<>();
        stack.push(root);
        while(!stack.isEmpty()) {
            Node node = stack.pop();
            if(node == null) continue;
            set.add(node.value);
            stack.push(node.left);
            stack.push(node.right);
        }

        return set;
    }

    public static void main(String[] args) {
        Map<String, Integer> map = new MyTreeMap<>();
        map.put("Word1", 1);
        map.put("Word2", 2);

        Integer value = map.get("Word1");
        System.out.println(value);

        for(String key: map.keySet()) {
            System.out.println(key + ", " + map.get(key));
        }
    }

    public MyTreeMap<K, V>.Node makeNode(K key, V value) {
        return new Node(key, value);
    }

    public void setTree(Node node, int size) {
        this.root = node;
        this.size = size;
    }

    public int height() {
        return heightHelper(root);
    }

    private int heightHelper(Node node) {
        if(node == null) {
            return 0;
        }

        int left = heightHelper(node.left);
        int right = heightHelper(node.right);

        return Math.max(left, right) + 1;
    }
}
