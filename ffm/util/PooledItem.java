package gov.hhs.cms.base.common.util;

import java.util.UUID;
import java.util.concurrent.Semaphore;


public class PooledItem<T> implements Comparable<PooledItem<T>>{
	private T pooledResource;
	private Semaphore permits = new Semaphore(1);
	private String identifier = UUID.randomUUID().toString();

	public T borrowItem() {
		try {
			permits.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getItem();
	}

	public T getItem() {
		return pooledResource;
	}

	public void returnItem() throws IllegalStateException {
		if (getAvailablePermits() != 0){
			throw new IllegalStateException("Cannot return a PooledItem which has non-zero permits");
		}
		permits.release();
	}

	public void setItem(T item) {
		this.pooledResource = item;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!(other instanceof PooledItem)) {
			return false;
		}
		if (!this.identifier.equals(((PooledItem) other).getIdentifier())) {
			return false;
		}
		return true;
	}
	
	public int getAvailablePermits(){
		return permits.availablePermits();
	}

	public int hashCode() {
		return identifier.hashCode();
	}

	public int compareTo(PooledItem<T> arg0) {
		if (permits.availablePermits() > arg0.getAvailablePermits()){
			return 1;
		}
		if (permits.availablePermits() < arg0.getAvailablePermits()){
			return -1;
		}
		return identifier.compareTo(arg0.identifier);
	}
	
	public String toString(){
		return "PooledItem: { identifier : \"" + identifier + "\", type: \"" + pooledResource.getClass() + "\", permits: \"" + permits.availablePermits() + "\"}";
	}
}