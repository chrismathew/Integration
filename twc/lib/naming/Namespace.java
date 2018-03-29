package com.twc.eis.lib.naming;

public class Namespace {
	
	
	public static boolean isRoot(String namespace) {
				
		return namespace.indexOf(".") < 0;
		
	}

	public static String climbNamespace(String namespace, int count) {
		
		if (namespace.indexOf(".") < 0)
			return null;

		String currentNode = namespace.substring(0, namespace.indexOf(".") );

		return climbNamespace(namespace, count, currentNode);

	}

	public static String climbNamespace(String namespace, int count,
			String currentNode) {

		// 224.123.2.4.111.2

		String retval = namespace;
		boolean back = count < 0;

		int pos;

		if (currentNode == null)
			pos = 0;
		else {
					
			pos = namespace.indexOf(currentNode +".");
			
			if ( pos < 0 ) {			
			
				pos = namespace.indexOf("." + currentNode);
				
				if (pos < 0)
					return currentNode;
				else
					pos += currentNode.length() + 1;
								
			}
				
		}
		
		
		if (!back)
			retval = namespace.substring(pos);
		else {
			
			if (pos == 0)
				return currentNode;
	
			retval = namespace.substring(0, pos - 1);
		}
			
		
		
		int i = 0;
		boolean toEnd = false;

		count = count < 0 ? -1 * count : count;

		while (toEnd || (i < count && pos >= 0)) {

			pos = back ? retval.lastIndexOf(".") : retval.indexOf(".");

			if (pos < 0)
				break;

			//retval = retval.substring(pos + 1 + (back ? -1 : 1)) ;
			retval = back ? retval.substring(0, pos) : retval.substring(pos + 1) ;
			
			i++;

		}

		return back ? namespace.substring( namespace.indexOf(retval) + retval.length() + 1 ) : retval;
	}

	public static String getLeafNamespace(String namespace) {

		return climbNamespace(namespace, -1000);

	}

	public static String getRootNamespace(String namespace) {

		return climbNamespace(namespace, 1000);

	}

	public static void main(String[] args) {

		String namespace = "100.200.1.4.99.111";

		String test;

		test = Namespace.climbNamespace(namespace, 2);

		test = Namespace.climbNamespace(test, 10);
		
		test = Namespace.climbNamespace(namespace, -3, Namespace.getLeafNamespace(test));
				
		test = Namespace.resolveNamespace(namespace, "/");

		test = Namespace.resolveNamespace(namespace, "../");

		test = Namespace.resolveNamespace(namespace, "../..");

		test = Namespace.resolveNamespace(namespace, "../../.././.");

		System.out.print(test);

	}

	
	
	public static String resolveNamespace(String namespace, String pathExpression) {
		
		/////  ./../parentID  /././
		
		// 123.23.4.5
		
		String retval = namespace;
		boolean processing = true;
		int pos = 0;
		char c;
		
		while (processing) {
			
		
			if ( pathExpression.startsWith("./") ) {
				
				retval = climbNamespace(namespace, -1, getLeafNamespace(retval)); 				
				pathExpression = pathExpression.substring(2);
			}
			
			
			else if ( pathExpression.startsWith("../") ) {
				
				retval = climbNamespace(namespace, 1, getLeafNamespace(retval)); 				
				pathExpression = pathExpression.substring(3);
				
				
			}

			else if ( pathExpression.startsWith("..") ) {
				
				retval = climbNamespace(namespace, 1, getLeafNamespace(retval)); 				
				pathExpression = pathExpression.substring(2);
				
				
			}
			
			else if ( pathExpression.startsWith("/..") ) {
				
				retval = climbNamespace(namespace, -2, getLeafNamespace(retval)); 				
				pathExpression = pathExpression.substring(3);
			}

			else if ( pathExpression.startsWith("/.") ) {
				
				retval = climbNamespace(namespace, -1, getLeafNamespace(retval)); 				
				pathExpression = pathExpression.substring(2);
			}


			else if ( pathExpression.startsWith("/") ) {
				
				retval = Namespace.getRootNamespace(namespace);
				pathExpression = pathExpression.substring(1);
				
			}		

			else if ( pathExpression.startsWith(".") ) {
				
				pathExpression = pathExpression.substring(1);
			}
			
			processing =
				pathExpression.length() > 0 && 
				( pathExpression.charAt(0) == '/' || pathExpression.charAt(0) == '.' );
						
			
		}
		
		
		return retval;
	}
	
	

}
