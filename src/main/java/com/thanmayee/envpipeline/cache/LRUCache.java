package com.thanmayee.envpipeline.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache<K, V> {

    private class Node {
        K key;
        V value;
        Node previous;
        Node next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node> cache = new HashMap<>();

    private final Node head = new Node(null, null);
    private final Node tail = new Node(null, null);

    public LRUCache(int capacity) {

        if (capacity < 1) {
            throw new IllegalArgumentException(
                "Cache capacity must be at least 1"
            );
        }

        this.capacity = capacity;

        head.next = tail;
        tail.previous = head;
    }

    public synchronized V get(K key) {

        Node node = cache.get(key);

        if (node == null) {
            return null;
        }

        moveToFront(node);

        return node.value;
    }

    public synchronized void put(K key, V value) {

        Node existing = cache.get(key);

        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        Node node = new Node(key, value);

        cache.put(key, node);
        addToFront(node);

        if (cache.size() > capacity) {
            Node leastRecentlyUsed = tail.previous;

            removeNode(leastRecentlyUsed);
            cache.remove(leastRecentlyUsed.key);
        }
    }

    public synchronized int size() {
        return cache.size();
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    private void addToFront(Node node) {

        node.next = head.next;
        node.previous = head;

        head.next.previous = node;
        head.next = node;
    }

    private void removeNode(Node node) {

        node.previous.next = node.next;
        node.next.previous = node.previous;
    }
    
    public synchronized void clear() {
        cache.clear();

        head.next = tail;
        tail.previous = head;
    }
}
