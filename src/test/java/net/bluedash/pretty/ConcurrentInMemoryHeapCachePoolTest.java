package net.bluedash.pretty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.bluedash.pretty.cache.CachePool;
import net.bluedash.pretty.cache.CachePoolFactory;

import org.junit.Test;

public class ConcurrentInMemoryHeapCachePoolTest {

	@Test
	public void testSingleThreadUsage() {
		CachePool pool = CachePoolFactory.newConcurrentInMemoryHeapCachePool();
		pool.setCapacity(2);
		assertEquals(2, pool.getCapacity());

		assertEquals(0, pool.currentSize());

		pool.put("hello", "Hello, world!");
		assertEquals(1, pool.currentSize());

		pool.put("good", "Good Day!");
		assertEquals(2, pool.currentSize());
		assertTrue(pool.has("good"));

		assertEquals("Hello, world!", pool.get("hello"));

		pool.put("extra", "+1");
		assertEquals(2, pool.currentSize());
		assertFalse(pool.has("good"));
	}

	@Test
	public void testConcurrentUsage() throws InterruptedException {
		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < 100; i++) {
			Random r = new Random();
			CachePoolFactory.sharedConcurrentInMemoryHeapCachePool()
					.setCapacity(Math.abs(r.nextInt() % 100) + 1);
			PoolUser user = new PoolUser();
			Thread clientThread = new Thread(user);
			clientThread.start();
			threads.add(clientThread);
		}

		for (Thread t : threads) {
			t.join();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgument() {
		CachePool pool = CachePoolFactory.newConcurrentInMemoryHeapCachePool();
		pool.setCapacity(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIllegalArgument2() {
		CachePoolFactory.newConcurrentInMemoryHeapCachePool(0);
	}

}
