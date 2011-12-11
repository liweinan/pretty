package net.bluedash.pretty.cache;

public class CachePoolFactory {

	private static CachePool sharedConcurrentInMemoryHeapCachePool = new ConcurrentInMemoryHeapCachePool();

	public static CachePool newConcurrentInMemoryHeapCachePool(int capacity) {
		return new ConcurrentInMemoryHeapCachePool(capacity);
	}

	public static CachePool newConcurrentInMemoryHeapCachePool() {
		return new ConcurrentInMemoryHeapCachePool();
	}

	public static CachePool sharedConcurrentInMemoryHeapCachePool() {
		return sharedConcurrentInMemoryHeapCachePool;
	}

}
