package net.bluedash.pretty.cache;

public interface CachePool {

	public void put(String key, String value);

	public String get(String key);

	public boolean has(String key);

	public void remove(String key);
	
	public void reset();
	
	public void setCapacity(int capacity);
	
	public int getCapacity();
	
	public int currentSize();
}
