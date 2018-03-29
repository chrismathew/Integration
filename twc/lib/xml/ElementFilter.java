package com.twc.eis.lib.xml;

import java.util.Iterator;

import com.twc.eis.lib.file.FileUtil;

public class ElementFilter {
	
	public boolean applyFilter(Element element, Attribute attribute) {return true;}
	public boolean applyFilter(Element element) {return true;}

	
	public final Element filter(Element element) {
		
		return filter(element, true);
		
		
	}

	public final Element filter(Element element, boolean deep) {
		
		Element retval = element.copy();
		
		filterAll(retval, deep);
		
		return retval;
	}
	
	
	private final void filterAll(Element retval, boolean deep) {
		
		if ( ! applyFilter( retval ) ) {
			
			if ( retval.hasParent() )
				retval.getParent().deleteChild(retval);

			return;

		}
		
		Iterator<Attribute> i = retval.getAttributes().values().iterator();
		
		while ( i.hasNext() )

			if ( ! applyFilter( retval, i.next() ) )
				i.remove();
				
								
		if ( deep )
			
			for (Element e : retval.getAllChildren() )
				filterAll(e, deep);
		
		return;
	}
	

	public static void main(String[] args) {

		try {
			
			String xml = FileUtil
			.fileToString("/home/takadiri/project/DSB/2.0/xml/tsdl/test.xml");
			
			Element doc = Element.parse( xml );
			
			Element result = doc.xfind("//AsbMessage")[0];
			//result = result.xfind(".")[0];
			
			System.out.println(result.toString());
			
			
			ElementFilter filter = new ElementFilter() {
				
				
				public boolean applyFilter(Element element) {
					
					String name = element.getName();
					
					element.setName("O" + name);
					element.setValue("TEST");
					
					return true;
					
				}
				
				public boolean applyFilter(Element element, Attribute attribute) {
					
					if ( attribute.getName().toLowerCase().startsWith("i") || attribute.getName().toLowerCase().startsWith("v") ) {
						return false;
					}
				
					attribute.setName( attribute.getName() + "X") ;
					
					return true;
					
				}

				
				
			};
			
			
			//filter.filter(result, true);
			//System.out.println( result.toString() );

			System.out.println(result.toString());
			
			System.out.println( filter.filter(result) );
			
			System.out.println(result.toString());
			
			

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}

}
