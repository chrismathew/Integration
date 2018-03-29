package com.twc.eis.lib.collection;

import java.io.Serializable;
import java.util.Map;

public class MapMap<T1, T2, T3> implements Serializable {
	
	private Map<T1, Map<T2, T3>> map = null;
	
	
	public MapMap() {
		initInstance();
	}
	
	
	public final void put(T1 type, T2 key, T3 o) {
		
		Map<T2, T3> map;
		
		if ( (map = getMap().get(type)) == null) {
			
			map = new java.util.HashMap<T2, T3>();
			
			getMap().put(type, map);
			
		}
		
		
		map.put(key, o);
		
	}
	
	public final T3 get(T1 type, T2 key) {
		
		if ( get(type) == null )
			return null;
		else				
			return getMap().get(type).get(key);
		
		
	}
	
	public final Map<T2, T3> get(Object type) {
		return getMap().get(type);
	}
	
	private void initInstance() {
		
		setMap( new java.util.HashMap< T1, Map<T2, T3> > () );
	
	}


	private Map<T1, Map<T2, T3>> getMap() {
		return map;
	}


	private void setMap(Map<T1, Map<T2, T3>> map) {
		this.map = map;
	}
	
		

}
