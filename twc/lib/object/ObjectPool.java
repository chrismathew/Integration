package com.twc.eis.lib.object;

// Author: Ty Akadiri (April 1999)
import java.util.Map;
import java.util.List;
import java.util.Iterator;

public class ObjectPool {

	private static long defaultCleanupInterval = 5 * 60 * 1000;
	private static long defaultTimeout = 5 * 1000;


	public static void main(String[] args) {

		try {

			IFactory factory = new GenericFactory();
			
			Map properties = new java.util.HashMap();
			properties.put("className", "java.lang.Thread");
						
			PoolProfile profile = new PoolProfile("Thread", factory);

			profile.setProperties(properties);
			profile.setInitialPoolSize(10);

			ObjectPool pool = new ObjectPool(profile);
			
			Map properties2 = new java.util.HashMap();

			properties2.put("className", "java.lang.String");
			
			PoolProfile profile2 = new PoolProfile("String", factory);
			
			profile2.setProperties(properties2);
			profile2.setInitialPoolSize(55);
			
			pool.addProfile( profile2 );
											
			pool.init();
			
			System.out.println( pool.toString() );

			runTest(pool);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

	}

	private static void runTest(ObjectPool pool) {

		try {

			//com.twc.eis.lib.general.ProcessTimer timer = new com.twc.eis.lib.general.ProcessTimer();

			int count = 1000;

//			int id = timer.startTiming(null, "WITH POOL");
			
			long s1, s2;
			Object o;
			
			s1 = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				o = new Thread();
			}
			
			s2 = System.currentTimeMillis();			
			System.out.println("NOT POOLED: " + (s2-s1));

			
			s1 = System.currentTimeMillis();
			for (int i = 0; i < count; i++) {
				o = pool.getInstance("thread");								
				pool.release(o);
			}
			
			s2 = System.currentTimeMillis();			
			System.out.println("POOLED: " + (s2-s1));
			
									
			String s = (String)pool.getInstance("string");

			System.out.println( pool.toString() );
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}

	}

	private long timeout = 0;
	private long cleanupInterval = 0;
	private Map checkoutMap = null;
	private PoolProfile defaultProfile = null;
	private Map freePoolRegistry = null;
	private Map poolProfiles = null;
	private Map usedPoolRegistry = null;


	public ObjectPool(PoolProfile profile) {
		
		initInstance();
		addProfile(profile, true);
		
	}
	
	
	
	public synchronized void addProfile(PoolProfile profile, boolean defaultProfile) {

		String key = profile.getName();

		getPoolProfiles().put(key, profile);
		createFreePool(key);
		createUsedPool(key);
		
		if (defaultProfile)
			setDefaultProfile( profile );

	}

	public void addProfile(PoolProfile profile) {
		addProfile( profile, false );
	}

	
	private void addToFreePool(PoolMember member) {

		List pool = getFreePool(member.getPoolProfile().getName());
		
		synchronized(pool) {

			pool.add(member);

			// notify in case there are threads waiting on an available object from
			// the free pool
			pool.notifyAll();
		
		}


	}

	private void addToUsedPool(PoolMember member) {

		Map pool = getUsedPool(member.getPoolProfile().getName());

		synchronized ( pool ) {
			pool.put(member, member);
		}

	}

	protected Object createObject() throws Exception {
		return getDefaultProfile().getFactory().newInstance( getDefaultProfile().getProperties() );
	}

	protected Object createObject(PoolProfile profile) throws Exception {		
		return profile.getFactory().newInstance(profile.getProperties());
	}

	private PoolMember createPoolMember(PoolProfile profile, boolean reserved)
			throws Exception {

		// create a new object using the factory
		Object object = createObject(profile);

		// create a pool member wrapper to store the object
		PoolMember member = new PoolMember(object);
		member.setPoolProfile(profile);

		synchronized(this) {			
		
			if (reserved)
				// store the new object in the used pool
				addToUsedPool(member);
			else
				// store the new object in the free pool
				addToFreePool(member);
			
			}

		onCreatePoolMember(object);

		return member;

	}

	private Map getCheckoutMap() {
		
		synchronized(checkoutMap) {
			return checkoutMap;
		}
	}


	public PoolProfile getDefaultProfile() {
		
		return this.defaultProfile;
		
	}

	private List getFreePool(Object key) {

		return (List) getFreePoolRegistry().get(key);

	}
	
	private synchronized List createFreePool(Object key) {
		// only called when a profile is added before init of the pool, so no performance hit
		List pool = new java.util.ArrayList();
		
		getFreePoolRegistry().put(key, pool );
	
		return pool;		
	}

	
	private synchronized Map createUsedPool(Object key) {
		// only called when a profile is added before init of the pool, so no performance hit
		
		Map pool = new java.util.HashMap();
		
		getUsedPoolRegistry().put(key, pool );
	
		return pool;		
	}
	
	private Map getFreePoolRegistry() {
		
		synchronized( freePoolRegistry ) {
			return freePoolRegistry;
		}
		
	}

	private Map getPoolProfiles() {
		
		synchronized (poolProfiles) {
			return poolProfiles;
			
		}

	}

	private PoolProfile getProfile(String key) {

		return  (PoolProfile) getPoolProfiles().get(key.toUpperCase() );

	}

	private Map getUsedPool(String key) {

		return (Map) getUsedPoolRegistry().get(key);
	}

	
	private Map getUsedPoolRegistry() {
		
		synchronized(usedPoolRegistry ) {
			return usedPoolRegistry;
		}
		
	}	

	public Object getInstance() throws Exception {
		return getInstance( getDefaultProfile().getName() );
	}

	public Object getInstance(String name) throws Exception {
		
		return getInstance( getProfile(name) );
		
	}
	
	public synchronized Object getInstance(PoolProfile profile) throws Exception {

		PoolMember member = null;
		boolean isNew = false;
		
		String key = profile.getName();

		try {

		if (sizeFree(key) == 0 && sizeUsed(key) >= profile.getMaxPoolSize()) {
			
			wait( getTimeout()==0 ? getDefaultTimeout() : getTimeout() );
			
			
			if (sizeFree(key) == 0)				
				throw new Exception("Pool Size Limit Error!");			
			else {
				
				// create a new pool member and flag as reserved
				member = createPoolMember(profile, true);
							
				isNew = true;
									
			}
						

		} else if (sizeFree(key) == 0) {

			// create a new pool member and flag as reserved
			member = createPoolMember(profile, true);
						
			isNew = true;

		} else {

			member = (PoolMember) getFreePool(key).get(0);

			// pre-existing object: so...
			// remove the object from the free pool
			removeFromFreePool(key, 0);

			// add the object to the used pool
			addToUsedPool(member);
		}
			
		getCheckoutMap().put(member.getReference(), key);
					
		// mark the time the member was checked out
		member.setTimestamp(System.currentTimeMillis());

		// grab notification
		onGrab(member, true);

		// return the object reference of the member
		return member.getReference();
		
		} catch(Exception e) {
			
			onGrab(member, false);
			
			throw e;
		}
		
		
	}

	private synchronized Object[] harvestFreeMembers(Object key) {

		Object[] temp = getFreePool(key).toArray();

		getFreePool(key).clear();

		return temp;

	}

	public void init() {

		// fill the pool with the initial number of objects
		primePool();

		//startCleanupThread();

	}

	protected void initInstance() {

		setUsedPoolRegistry(new java.util.HashMap());
		setFreePoolRegistry(new java.util.HashMap());
		setCheckoutMap(new java.util.HashMap());
		setPoolProfiles(new java.util.HashMap());

	}

	protected void onCreatePoolMember(Object object) {
	}

	protected void onGrab(PoolMember member, boolean success) {
	}

	protected void onPrimePool(PoolProfile profile, boolean success) {
	}

	protected void onRelease(PoolMember member, boolean success) {
	}

	public void onRemoveExpired(Object object) {
	}

	private void primePool() {

		PoolProfile profile, defaultProfile;

		Object[] profiles = getPoolProfiles().values().toArray();
		
		/*

		if (profiles.length == 0) {

			profiles = new PoolProfile[1];

			profiles[0] = getDefaultProfile();

		}

*/
		
		for (int i = 0; i < profiles.length; i++) {

			profile = (PoolProfile) (profiles[i]);

			long initialSize = profile.getInitialPoolSize();

			long count = 0;

			while (count < initialSize) {

				try {

					createPoolMember(profile, false);

					++count;

				} catch (Exception e) {
					onPrimePool(profile, false);
				}

			}

			if (count > 0)
				onPrimePool(profile, true);
			else if (count == 0 && initialSize > 0)
				onPrimePool(profile, false);

		}

	}

	public boolean release(Object object) {

		boolean found = false;
		PoolMember member, objectKey;

		if (object != null) {

			String poolKey = (String)getCheckoutMap().get(object);
						
			objectKey = new PoolMember(object);
			
			synchronized( this ) {

				member = (PoolMember) getUsedPool(poolKey).remove(
						objectKey);
						
				getCheckoutMap().remove(object);
			
			}
			
			objectKey = null;
			
			if (member == null) {

				onRelease(objectKey, false);

				return false;
			}

						
			// add the object as a member back into the free pool
			addToFreePool(member);
			// remove the member from the used pool
			removeFromUsedPool(member);

			onRelease(member, true);

		}

		return true;

	}

	private void removeExpired() {

		Object o;

		PoolProfile profile, defaultProfile;

		Object[] keys = getFreePoolRegistry().keySet().toArray();

		for (int j = 0; j < keys.length; j++) {

			// 1. get an immutable copy of the free pool
			// and clear out the free pool
			Object[] freeMembers = harvestFreeMembers(keys[j]);

			profile = getProfile((String)keys[j]);

			for (int i = 0; i < freeMembers.length; i++) {

				PoolMember member = (PoolMember) (freeMembers[i]);

				if ((profile.getExpirationTime() != 0 && System
						.currentTimeMillis()
						- member.getTimestamp() < profile.getExpirationTime())
						&& profile.getFactory().isValid(member))

					// member is still valid - add it back into the free pool
					addToFreePool(member);

				else {

					onRemoveExpired(member.getReference());

					// clue the garbage collector that this object can be
					// deleted
					member.setReference(null);
					member = null;
				}
			}

		}

		keys = null;

		// invoke the garbage collector
		System.gc();

	}

	private synchronized void removeFromFreePool(Object key, int member) {

		getFreePool(key).remove(member);

	}

	private synchronized void removeFromUsedPool(PoolMember member) {

		getUsedPool(member.getPoolProfile().getName()).remove(member);

	}

	private void setCheckoutMap(Map checkoutMap) {
		this.checkoutMap = checkoutMap;
	}

	public void setDefaultProfile(PoolProfile defaultProfile) {
		this.defaultProfile = defaultProfile;
	}


	private void setFreePoolRegistry(Map freePoolRegistry) {
		this.freePoolRegistry = freePoolRegistry;
	}

	private void setPoolProfiles(Map poolProfiles) {
		this.poolProfiles = poolProfiles;
	}

	private void setUsedPoolRegistry(Map usedPoolRegistry) {
		this.usedPoolRegistry = usedPoolRegistry;
	}

	public synchronized int size(String key) {
		return sizeFree(key) + sizeUsed(key);
	}

	public int sizeFree(String key) {
		return getFreePool(key).size();
	}

	public int sizeUsed(String key) {
		return getUsedPool(key).size();
	}

	private void startCleanupThread() {

		Thread cleanUp = new Thread() {

			public void run() {

				while (true) {

					try {
						
						Thread.sleep( getCleanupInterval()==0 ? getDefaultCleanupInterval() : getCleanupInterval() );
					}

					catch (Exception e) {
					}

					removeExpired();

				}
			}
		}; // end of anonymous cleanUp Thread class

		cleanUp.start();
	}

	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		Iterator iterator;
		int numObject, numPool;
		String key;
		
		iterator = getFreePoolRegistry().keySet().iterator();
		
		numObject = numPool = 0;

		buffer.append("\n");
		buffer.append("===========").append("\n");
		buffer.append("Object Pool").append("\n");
		buffer.append("===========").append("\n");

		while ( iterator.hasNext() ) {

			key = (String)iterator.next();
			
			
			buffer.append("\n");
			buffer.append("   Pool ").append(++numPool).append("\n");
			buffer.append("   ------------\n");
			buffer.append("   Name: ").append(key).append("\n");
			buffer.append("   Factory: ").append(((PoolProfile)getPoolProfiles().get(key)).getFactory().getClass().getName()).append("\n");
			buffer.append("   Properties: ").append( ((PoolProfile)getPoolProfiles().get(key)).getProperties().toString()).append("\n");
			buffer.append("   Free objects: ").append( sizeFree(key) ).append("\n");
			buffer.append("   Used objects: ").append( sizeUsed(key) ).append("\n");
			buffer.append("   ------------\n");
			buffer.append("   Total: ").append( sizeUsed(key) + sizeFree(key) ).append("\n\n");
			
			//buffer.append("--------------------------------").append("\n");
			buffer.append("\n");
			
			numObject += sizeUsed(key) + sizeFree(key);
			
			
		}

		buffer.append("=================").append("\n");
		buffer.append("Total Objects: ").append(numObject).append("\n");
		buffer.append("=================").append("\n");
		
		iterator=null;
		
		return buffer.toString();

	}

	public final long getCleanupInterval() {
		return cleanupInterval;
	}

	public final void setCleanupInterval(long cleanupInterval) {
		this.cleanupInterval = cleanupInterval;
	}

	private static final long getDefaultCleanupInterval() {
		return defaultCleanupInterval;
	}

	private static final void setDefaultCleanupInterval(long defaultCleanupInterval) {
		ObjectPool.defaultCleanupInterval = defaultCleanupInterval;
	}

	private static final long getDefaultTimeout() {
		return defaultTimeout;
	}

	private static final void setDefaultTimeout(long defaultTimeout) {
		ObjectPool.defaultTimeout = defaultTimeout;
	}

	public final long getTimeout() {
		return timeout;
	}

	public final void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
