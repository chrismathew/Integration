package com.twc.eis.lib.object;

import java.util.Map;

public class GenericFactory implements IFactory {

		public GenericFactory() {}

		public Object newInstance() throws Exception {
			
			throw new Exception("Generic Factory - No class defined");
		
		}
			
		public Object newInstance(Map properties) throws Exception {
			
			String className = (String)properties.get("className");
		
			return Class.forName( className ).newInstance();
			
		}

		public boolean isValid( Object o ) { return true; }
	
}
