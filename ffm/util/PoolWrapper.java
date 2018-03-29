package gov.hhs.cms.base.common.util;

import java.util.TreeSet;

import org.apache.log4j.Logger;

public class PoolWrapper<T> {
	int maxSize;
	private static final Logger LOG = Logger.getLogger(PoolWrapper.class);
	
	

	public PoolWrapper(int intendedMaxSize) {
		maxSize = intendedMaxSize;
	}

	public int getSize() {
		return pool.size();
	}

	public boolean isFull() {
		return maxSize == getSize();
	}

	public boolean addItemToPool(T item) {
		synchronized (this) {
			if (!isFull()) {
				PooledItem<T> newPoolEntry = new PooledItem<T>();
				newPoolEntry.setItem(item);
				pool.add(newPoolEntry);
				LOG.debug("Adding a " + item + " to the pool.");
				return true;
			}
		}
		return false;
	}

	public PooledItem<T> borrowItem() {
		PooledItem<T> poolVal = null;
		boolean acquired = false;
		int acquireAttempts = 0;
		
		while (!acquired){
			synchronized (this) {
				poolVal = pool.last();
				if (poolVal.getAvailablePermits() >= 1) {
					// Block
					pool.remove(poolVal);
					LOG.debug("Borrowing a " + poolVal + " to the pool.");
					poolVal.borrowItem();
					pool.add(poolVal);
					acquired = true;
				}
			}
			if (acquired){
				return poolVal;
			}
			acquireAttempts += 1;
			if (acquireAttempts <= Integer.parseInt(FFEConfig.getProperty("PoolWrapper.MaxAcquireAttempts", "500"))) {
				try {
					LOG.debug("Waiting...");
					Thread.sleep(3);
					poolVal = null;
				} catch (InterruptedException e) {
					LOG.error(e);
					break;
				}
			} else {
				LOG.warn("Unable to acquire the item after " + Integer.parseInt(FFEConfig.getProperty("PoolWrapper.MaxAcquireAttempts", "500")) + " attempts.");
			}
		}
		return poolVal;
	}

	public void returnItem(PooledItem<T> item) {
		synchronized (this) {
			pool.remove(item);
			LOG.debug("Returning a " + item + " to the pool.");
			item.returnItem();
			pool.add(item);
		}
	}

	TreeSet<PooledItem<T>> pool = new TreeSet<PooledItem<T>>();

}