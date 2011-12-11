package net.bluedash.pretty.cache;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class ConcurrentInMemoryHeapCachePool implements CachePool {

	/* ConcurrentLinkedQueue is FIFO */
	private ConcurrentLinkedQueue<Entry> pool = new ConcurrentLinkedQueue<Entry>();

	private AtomicInteger capacity = new AtomicInteger(256);

	private AtomicInteger currentSize = new AtomicInteger(0);

	public ConcurrentInMemoryHeapCachePool() {
		super();
	}

	public ConcurrentInMemoryHeapCachePool(int capacity) {

		if (capacity < 1) {
			throw new IllegalArgumentException();
		}

		this.capacity.set(capacity);
	}

	@Override
	public void put(String key, String value) {
		Entry entry = new Entry(key, value);

		synchronized (this) {
			boolean duplicateEntryFound = false;

			for (Iterator<Entry> iter = pool.iterator(); iter.hasNext();) {
				if (iter.next().equals(entry)) {
					duplicateEntryFound = true;
					iter.remove(); /* remove the existing one */
					pool.add(entry); /* add the new one at tail */
					break;
				}
			}

			if (!duplicateEntryFound) { /* add the new entry */
				if (pool.size() >= capacity.get()) { /* if the pool is full */
					pool.poll(); /* remove the first-in element */
					currentSize.decrementAndGet();
				}

				pool.add(entry);
				currentSize.incrementAndGet();
			}
		}

	}

	@Override
	public String get(String key) {
		synchronized (pool) {
			boolean entryFound = false;
			Entry newEntry = new Entry("", "");

			for (Iterator<Entry> iter = pool.iterator(); iter.hasNext();) {
				Entry entry = iter.next();

				if (entry.getKey().equals(key)) {
					newEntry = entry;
					iter.remove(); /* remove is safe */
					entryFound = true; /* but add something is not safe */
					break; /* so we use entryFound and newEntry to add it later */
				}
			}

			if (entryFound) {
				pool.add(newEntry); /* add back to the tail */
				return newEntry.getValue();
			}
		}
		return null;
	}

	@Override
	public boolean has(String key) {
		synchronized (pool) {
			for (Entry entry : pool) {
				if (entry.getKey().equals(key))
					return true;
			}
		}
		return false;
	}

	@Override
	public void remove(String key) {
		synchronized (this) {
			for (Iterator<Entry> iter = pool.iterator(); iter.hasNext();) {
				Entry entry = iter.next();
				if (entry.getKey().equals(key)) {
					iter.remove();
					currentSize.decrementAndGet();
					return;
				}
			}
		}
	}

	@Override
	public void reset() {
		synchronized (this) {
			pool.clear();
			currentSize.set(0);
		}
	}

	@Override
	public void setCapacity(int capacity) {
		if (capacity < 1) {
			throw new IllegalArgumentException();
		}

		synchronized (this) {
			reset();
			this.capacity.set(capacity);
		}
	}

	@Override
	public int getCapacity() {
		return this.capacity.get();
	}

	@Override
	public int currentSize() {
		return this.currentSize.get();
	}

	@NotThreadSafe
	private class Entry {
		private String key;
		private String value;

		public Entry(String key, String value) {
			super();
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}

			if (!(obj instanceof Entry)) {
				return false;
			}

			if (this.key.equals(((Entry) obj).getKey())) {
				return true;
			}
			return false;
		}

	}

}
