package com.thanmayee.envpipeline.cache;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LRUCacheTest {

    @Test
    void shouldEvictLeastRecentlyUsedItem() {

        LRUCache<String, Integer> cache = new LRUCache<>(3);

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        cache.put("D", 4);

        assertNull(cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(3, cache.get("C"));
        assertEquals(4, cache.get("D"));
    }

    @Test
    void shouldUpdateRecencyAfterGet() {

        LRUCache<String, Integer> cache = new LRUCache<>(3);

        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        cache.get("A");

        cache.put("D", 4);

        assertEquals(1, cache.get("A"));
        assertNull(cache.get("B"));
    }

    @Test
    void shouldWorkWithCapacityOne() {

        LRUCache<String, Integer> cache = new LRUCache<>(1);

        cache.put("A", 1);
        cache.put("B", 2);

        assertNull(cache.get("A"));
        assertEquals(2, cache.get("B"));
        assertEquals(1, cache.size());
    }

    @Test
    void shouldUpdateExistingKeyWithoutIncreasingSize() {

        LRUCache<String, Integer> cache = new LRUCache<>(2);

        cache.put("A", 1);
        cache.put("B", 2);

        cache.put("A", 100);

        assertEquals(100, cache.get("A"));
        assertEquals(2, cache.size());
    }

    @Test
    void shouldRejectInvalidCapacity() {

        assertThrows(
            IllegalArgumentException.class,
            () -> new LRUCache<String, Integer>(0)
        );
    }
}
