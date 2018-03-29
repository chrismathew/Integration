package com.twc.eis.lib.object;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

// Author: Ty Akadiri (April 1999)
public class PoolMember {

	private Object reference = null;
		
	private PoolProfile poolProfile = null;
	
	private long timestamp = 0;
	
	PoolMember(Object reference) {
		
		setReference(reference);
		setTimestamp(System.currentTimeMillis());
		
	}
	
	public final boolean equals(Object object) {
		return object instanceof PoolMember 
		&& ((PoolMember)object).getReference().equals(getReference());
	}

	public final void setPoolProfile(PoolProfile poolProfile) { this.poolProfile = poolProfile; }
	public final PoolProfile getPoolProfile() { return poolProfile; }
	
	
	public final Object getReference() { return reference; }
	
	
	public final long getTimestamp() { return timestamp; }
	  
	public final int hashCode() {
		return getReference().hashCode();
	}
	
	public void setReference(Object o) { reference = o; }
	
	public final void setTimestamp(long t) { timestamp = t; }
	
	
	public final String toString() {
		return getReference().toString();
	}

	
}
