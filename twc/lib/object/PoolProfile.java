package com.twc.eis.lib.object;

import java.util.Map;

public class PoolProfile {
	
	private String name;

	private IFactory factory;
	
	private Map properties;

	private long expirationTime = Long.MAX_VALUE;
	private int initialPoolSize = 0;
	private int maxPoolSize = Integer.MAX_VALUE;
	
	
	public PoolProfile(String name, IFactory factory) throws NullPointerException {
		
		if (name == null ) throw new NullPointerException("Profile name cannot be null!");
		
		setName(name);
		setFactory( factory );
	}
		
	public final String getName() {
		return name;
	}

	private final void setName(String name) {
		this.name = name.toUpperCase();
	}

	public final long getExpirationTime() {
		return expirationTime;
	}

	public final void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}

	public final int getInitialPoolSize() {
		return initialPoolSize;
	}

	public final void setInitialPoolSize(int initialCount) {
		this.initialPoolSize = initialCount;
	}

	public final int getMaxPoolSize() {
		return maxPoolSize;
	}

	public final void setMaxPoolSize(int maxCount) {
		this.maxPoolSize = maxCount;
	}

	public final Map getProperties() {
		return properties;
	}

	public final void setProperties(Map properties) {		
		this.properties = properties;							
	}
		
	public final IFactory getFactory() {
		return factory;
	}

	public final void setFactory(IFactory factory) {
		this.factory = factory;
	}

	
}
