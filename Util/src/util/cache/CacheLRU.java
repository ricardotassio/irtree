package util.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An LRU cache, based on <code>LinkedHashMap</code>.<br>
 * This cache has a fixed maximum number of elements (<code>cacheSize</code>).
 * If the cache is full and another entry is added, the LRU (least recently used) entry is dropped.
 * <p>
 * This class is thread-safe. All methods of this class are synchronized.<br>
 * Author: Christian d'Heureuse (<a href="http://www.source-code.biz">www.source-code.biz</a>)<br>
 * License: <a href="http://www.gnu.org/licenses/lgpl.html">LGPL</a>.
 */
public class CacheLRU<K,V> extends LinkedHashMap<K, V> {
    private static final float   hashTableLoadFactor = 0.75f;
    private final int cacheMaxSize;
    /**
    * Creates a new LRU cache.
    * @param cacheSize the maximum number of entries that will be kept in this cache.
    */
    public CacheLRU (int cacheMaxSize) {
        super(cacheMaxSize, hashTableLoadFactor, true);
        this.cacheMaxSize = cacheMaxSize;
    }

    @Override
    protected boolean removeEldestEntry (Map.Entry<K,V> eldest){
        return this.size()>cacheMaxSize;
    }

} 
