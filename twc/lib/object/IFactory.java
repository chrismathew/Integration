package com.twc.eis.lib.object;

import java.util.Map;

// Author: Ty Akadiri (April 1999)
public interface IFactory {

	public Object newInstance() throws Exception;
	public Object newInstance(Map properties) throws Exception;

   public boolean isValid( Object o );
   
   //   public boolean isValid( Object o, Properties properties, ObjectPool pool );
   // pool.init( Properties properties )??? 
   // pool.clear( Properties properties )??? 

   
   
/*	
   public Object beforeCheckIn( Object o );

   public Object beforeCheckOut( Object o );		
  */
   
}
