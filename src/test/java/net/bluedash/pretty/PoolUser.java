package net.bluedash.pretty;

import java.util.ConcurrentModificationException;
import java.util.Random;

import net.bluedash.pretty.cache.CachePool;
import net.bluedash.pretty.cache.CachePoolFactory;

public class PoolUser implements Runnable {

	private Random r = new Random();

	private CachePool pool = CachePoolFactory
			.getSharedConcurrentInMemoryHeapCachePool();

	@Override
	public void run() {
		for (int i = 0; i < pool.getCapacity(); i++) {
			String m = Integer.toString((r.nextInt() % pool.getCapacity()));

			System.out.print("+");
			pool.put(m, m);

			System.out.print("-");
			pool.get(m);

		}
		System.out.println(".");
	}
}
