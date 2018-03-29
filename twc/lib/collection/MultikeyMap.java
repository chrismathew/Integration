package com.twc.eis.lib.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultikeyMap<T1, T2> implements Serializable {
	
	private Map<T1, T2> map = new HashMap<T1, T2>();
	private ListMap<T2, T1> keyRegistry = new ListMap<T2, T1>();
	
	public ListMap<T2, T1> getKeyRegistry() {
		return keyRegistry;
	}


	public void setKeyRegistry(ListMap<T2, T1> keyRegistry) {
		this.keyRegistry = keyRegistry;
	}


	public Map<T1, T2> getMap() {
		return map;
	}


	public void setMap(Map<T1, T2> map) {
		this.map = map;
	}


	public MultikeyMap() {
	}
	
	
	public final void put(T2 obj, T1... keys) {
		
		Map<T1, T2> map = getMap();
		ListMap<T2, T1> keyRegistry = getKeyRegistry();
		
		for (T1 key : keys) {
			
			T2 o = map.get(key);
			
			if ( o == null ) {
				map.put(key, obj);
				keyRegistry.put(obj, key);
			}
			
			
		}
				
	}

	public final void removeKey(T1... keys) {
		
		Map<T1, T2> map = getMap();
		ListMap<T2, T1> keyRegistry = getKeyRegistry();
		
		T2 obj;
		for (T1 key : keys) {
			
			obj = map.get(key);
			
			map.remove(key);
			
			keyRegistry.get(obj).remove(key);
		}
				
	}

	public final void remove(T1 key) {
		
		Map<T1, T2> map = getMap();
		ListMap<T2, T1> keyRegistry = getKeyRegistry();
		
		T2 obj = map.remove(key);
		
		for ( T1 otherKey : keyRegistry.get(obj) )
			map.remove(otherKey);
		
		keyRegistry.remove(obj);
		
	}
	
	public final T2 get(T1... keys) {
		
		T2 retval = null;
		
		Map<T1, T2> map = getMap();
		
		
		for (T1 key : keys) {
			
			retval = map.get(key);
			if ( retval != null )
				break;
			
		}
		
		return retval;
	}
	
	
	public List<T2> getAll() {
		
		Map<T2, T2> temp = new HashMap<T2, T2>();
		
		for ( T2 obj : getMap().values() )
			if ( temp.get(obj) == null )
				temp.put(obj, obj);
		
		return new ArrayList<T2>( temp.values() );
		
		
		
	}
	
	public static void main(String[] args) {
		
		
		MultikeyMap m = new MultikeyMap<String, String>();
		
		String s1, s2;
		
		s1="1";
		s2="2";
				
		m.put(s1, "ONE");
		m.put(s1, "WAN");
		

		m.put(s2, "TWO", "TU", "TOO");
	
		
		System.out.println( m.get("ONE") );
		System.out.println( m.get("WAN") );
		
		System.out.println( m.get("TU") );
		System.out.println( m.get("TWO") );
		
		m.remove("TOO");
		
		System.out.println( m.get("TWO") );

	}
	
		

}

