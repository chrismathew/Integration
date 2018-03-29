package com.twc.eis.lib.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

public class ListMap<T1, T2> implements Serializable {	
	
	/**
	 * 
	 */	
	private static final long serialVersionUID = -1475136066266292917L;
	
	private boolean isUnique = false;
	
	private Map<T1, List<T2>> map = null;
	
	
	public ListMap() {
		initInstance(-1);
	}

	public ListMap(int size) {
		initInstance(size);
	}
	
	public ListMap(boolean isUnique) {
		
		this();
		
		setUnique(isUnique);
		
	}
	
	public List<T2> getAll() {
		
		List<T2> retval = new ArrayList<T2>();
		
		for ( List<T2> list : getMap().values() )		
			retval.addAll(list);
					
		return retval;
	}
	

	public final void set(T1 type, List<T2> l) {
				
			getMap().put(type, l);
			
		
	}
	

	
	public final void put(T1 type, T2 o, boolean replaceAll) {
		
		List<T2> list;
		
		if ( (list = getMap().get(type)) == null) {
			
			list = new java.util.ArrayList<T2>();
			
			getMap().put(type, list);
			
		} else if ( replaceAll )
				list.clear();
		
		if ( ! isUnique() || ( isUnique() &&  ! list.contains(o)) )
			list.add(o);
		
	}
	
	public final void put(T1 type, T2 o) {
		
		put(type, o, false);
	}

	public final boolean remove(T1 type) {
				
		if ( getMap().get(type) != null) {
			
			getMap().remove(type);
			
			return true;
			
		}
		
		return  false;
	}

	
	public final boolean remove(T1 type, T2 o) {
		
		List<T2> list;
		
		if ( (list = getMap().get(type)) != null) {
			
			return list.remove(o);
			
		}
		
		return false;
	}
	
	
	public final List<T2> get(T1 type) {
		
//		List<T2> retval = getMap().get(type);
		
		
	//	if ( retval == null )
		//	return new ArrayList<T2>(0);
		//else			
			return getMap().get(type);
	}
	
	
	private void initInstance(int size) {
		
		setMap( size == -1 ?  new java.util.HashMap<T1, List<T2>>() : new java.util.HashMap<T1, List<T2>>(size) );
	
	}

	
	public void clear() {
		getMap().clear();
	}

	public Map<T1, List<T2>> getMap() {
		return map;
	}


	private void setMap(Map map) {
		this.map = map;
	}


	public final boolean isUnique() {
		return isUnique;
	}


	public final void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}
	
	
	public Set<T1> keySet() {
		return getMap().keySet();
	}
		
	public String toString() {
		
		
		StringBuffer retval = new StringBuffer();
		
		
		T1 key;
		T2 item;
		List<T2> list;
		int i = 1, j;
				
		for (Iterator mapIterator = getMap().keySet().iterator(); mapIterator.hasNext(); i++ ) {
						
			key = (T1)mapIterator.next();
			
			retval.append( key ).append(":").append("\n");
			
			list = getMap().get(key);
			
			j=1;
			for (Iterator listIterator = list.iterator(); listIterator.hasNext(); j++) {
								
				item = (T2)listIterator.next();

				retval.append(" ").append(item).append("\n");
				
			}
			
			retval.append("\n");
		}
		
		
		return retval.toString();
		
		
		
	}
	
	
}
