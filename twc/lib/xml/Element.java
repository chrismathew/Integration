/**
 * 

<ASBMessage version="2.0" id="200810201000000000">

  <Header>
      <Source id="DSB"/>
      <Context id="Request"/>
      <Property id="HeaderProperty" value="HeaderProperty1"/>
  </Header>

  <Body>

    <Action id="1">
      <Property id="test" value="test"/>
      <Property id="test2" value="test2"/>
    </Action>

    <Action id="2">
      <Property id="test3" value="test3"/>
      <Property id="test4" value="test4"/>
    </Action>

  </Body>

</ASBMessage>

 * 
 * A representation of XML - can contain Elements nested to any level
 * 
 * 
 */

package com.twc.eis.lib.xml;

import java.io.Serializable;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import com.twc.eis.asb.tsdl.Tsdl30Helper;
import com.twc.eis.lib.file.FileUtil;

public class Element implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5546023251095781310L;
	
	public static final Element[] NULL_ELEMENT_ARRAY = new Element[0]; 
	
	private static SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

	public static void main(String[] args) {

		try {

			//String xml = FileUtil.fileToString("/mnt/hp/fileshare/project/rr/novell/ent/design/IDM/tsdl/test.xml");
			String xml = FileUtil.fileToString("/home/takadiri/project/DSB/2.0/xml/tsdl/test.xml");


			long startTime = System.currentTimeMillis();
			Element doc = null;
			for (int i = 0; i < 1000; i++) {
			
			doc = Element.parse( xml );
			
			}
			
			System.out.println("PARSING TIME: " + (System.currentTimeMillis() - startTime));
			System.exit(1);
			
			Element[] result;	

			result = doc.xpath("/AsbMessage");

			result = result[0].xpath("//Header/Endpoint");
			
			Element[] result2 = result[0].xpath("/AsbMessage");

			result = doc.xpath("/AsbMessage//Metadata");
			result = doc.xpath("//Body/Metadata");
			result2 = doc.xpath("//Body/Device/DeviceType");

			result2[0].addChild(result[0].copy());
			
			result = doc.xpath("/AsbMessage//Property/..");
			
			//result = doc.xpath("/AsbMessage//PropertyList[@id=\"TemplateResolverkeys\"]//Property//Test");

			result = doc.xpath("//AsbMessage");

			result = doc.xpath("/Header");

			result = doc.xpath("//Header");

			result = doc.xpath("/AsbMessage/Source");


			result = doc.xpath("/AsbMessage//Properties[@operation=\"query\"]//Tag");

			result = doc.xpath("//Properties");

			//result = result[0].xpath("//Header");

			result2 = result[0].xpath("//Header");
			Element[] result23 = result[0].xpath("/AsbMessage/");

			Element [] actions = doc.xpath("//Action");



			for(Element action : actions){


				Element []properties = action.xpath("/Property");


				for(Element prop : properties)

					System.out.println(prop);

			}
			System.out.print(result);


		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}

	public static final String PATH_SEPARATOR = "/";

	private boolean falseRoot = false;

	private List allChildren = null;

	private List allParents = null;

	//private boolean hasChildren = false;

	//private int index = 0;
	private String namespace = "0";

	private Map<String, Attribute> myAttributes = null;

	private String name = null;

	private int nesting = 0;

	//private int nextIndex = 0;

	private int ordinal = 0;

	private Element parent = null;

	//private Map parents = null;

	private Element root = null;

	private String value = null;

	private boolean qbeEquality = true;

	public Element() {

		setRoot(this);
		
	}

	public Element(String name) {

		this(name, null);

	}

	public Element(String name, String value) {

		setRoot(this);
		setName(name);
		setValue(value);

	}


	public void deleteChild(Element element) {

		getAllChildren().remove(element);
		
		element.setParent(null);

	}

	public Element addChild(Element child) {
		return addChild(child, true);
	}
	public Element addChild(Element child, boolean normalise) {
		
		child.setNesting(getNesting() + 1);
		
		getAllChildren().add(child);

		child.setParent(this);
			
		if ( normalise )
			normaliseChild(child, getAllChildren().size());

		return this;

	}
	
	public void normalise() {
		
		//Element base = getRoot();
		Element base = this;
		
		base.setNamespace("1");
		
		int i = 1;
		for (Element child : base.getAllChildren() )
			base.normaliseChild(child, i++);
		
		
	}
	
	private void normaliseChild( Element child, int index ) {
		
		//if ( child.getNamespace() == null ) {
			String ns = getNamespace().equals("0") ? "1" : index + "." + getNamespace();		
			//String ns = index + "." + getNamespace();		
			child.setNamespace( ns );
			//child.setAttribute("TAYO", ns);
		//}

		int i = 1;
		for ( Element grandchild : child.getAllChildren() )
			child.normaliseChild(grandchild, i++);
			
		
	}

	public void replace(Element node1, Element node2) {

		deleteChild(node1);
		addChild(node2);				


	}


	public final int attributeCount() {
		return getMyAttributes(true) == null ? 0 : getMyAttributes(true).size();
	}



	public final int childCount() {
		return childCount(null, false);
	}

	public final int childCount(Element element, boolean deep) {
		return countChildren(element, deep);
	}

	public void clear() {

		getAllChildren().clear();
		getAllParents().clear();
		getMyAttributes().clear();
		setNesting(0);
		setName(null);
		//setHasChildren(false);
	}



	public final int countChildren() {
		return countChildren(null, false);
	}

	public final int countChildren(Element element, boolean deep) {

		int num = 0;

		try {

			num = getChildElements(element, deep).length;

		} catch (Exception e) {
			System.err.println(e);
			System.err.println("Error in countChildren: " + e);
		}

		return num;
	}


	public final boolean equals(Object object) {

		return isMatch((Element) object, true);

	}

	protected final List findMatches(Element element) {

		List matches = new java.util.ArrayList();

		List myChildren = this.getAllChildren();

		for (int i = 0; i < myChildren.size(); i++) {

			if (((Element) myChildren.get(i)).equals(element))
				matches.add(myChildren.get(i));

		}

		return matches;
	}

	public final List<Element> getAllChildren() {

		if (allChildren == null) {
			allChildren = new java.util.ArrayList();
		}
		return allChildren;
	}

	private final List getAllParents() {

		//if (this.allParents == null) {

		List<Element> parents = new java.util.ArrayList();

		Element parent = this.getParent();

		boolean more = parent != null;

		while (more) {

			parents.add(parent);

			parent = parent.getParent();

			more = parent != null;

		}

		setAllParents(parents);
		
		//}

		return parents;

	}

	public Attribute getAttribute(String name) {

		if (getMyAttributes() == null || name == null)
			return null;
		else
			return (Attribute) getMyAttributes().get(name.toUpperCase());

	}

	public Map<String, Attribute> getAttributes() {

		/*
		 * Object[] values = getMyAttributes().values().toArray();
		 * 
		 * Attribute[] attr = new Attribute[values.length];
		 * 
		 * for (int i = 0; i < values.length; i++) { attr[i] = (Attribute)
		 * values[i]; }
		 * 
		 * return attr;
		 */

		return getMyAttributes(); //PERF
		
		//return (Attribute[]) getMyAttributes().values().toArray(new Attribute[0]);

	}

	public String getAttributeValue(String name) {

		String retval = null;

		if (getAttribute(name) != null)
			retval = getAttribute(name).getValue();

		return retval;

	}

	public Element[] getChildElements(boolean deep) {
		return getChildElements((Element)null, deep);
	}


	public Element[] getChildElements() {
		return getChildElements((Element)null, false);
	}

	public Element[] getChildElements(Element element) {
		return getChildElements(element, false);
	}

	public Element[] getChildElements(Element element, boolean deep) {
		return getChildElements( element, deep, false );
	}

	public Element[] getChildElements(Element element, boolean deep, boolean baseScope) {

		List children = new java.util.ArrayList();

		Element[] retval = null;

		try {



			if ( baseScope ) {


				if ( element == null ) {

					children.add(this);


				} else {

					if ( this.equals(element) )
						children.add(this);


				}

			} 
			
			if ( !baseScope || deep )
				getChildElements(element, deep, children);

			retval = (Element[]) (children.toArray(Element.NULL_ELEMENT_ARRAY));




		} catch (Exception e) {
			retval = Element.NULL_ELEMENT_ARRAY;
		}
		children = null; //PERF
		return retval;

	}



	private void getChildElements(Element element, boolean deep, List elements) {

		Element e = this; // == getRoot() ? ( getChildElements() )[0] : this;

		try {
			List list = new java.util.ArrayList();

			if (element == null) {

				list = e.getAllChildren();

				/*
				 * List[] all = (List[])(getChildren().values().toArray(new
				 * List[0]));
				 * 
				 * for (int i = 0; i < all.length; i++) { list.addAll( all[i] ); }
				 */

			} else {

				// list = (List)
				// (e.getChildren().get(element.getName().toUpperCase()));

				list = e.getAllChildren();

				if (list != null) {

					List matches = new java.util.ArrayList(list.size());

					for (int i = 0; i < list.size(); i++) {

						if (((Element) list.get(i)).equals(element))
							matches.add(list.get(i));

					}

					list = matches;

				}

			}

			if (list != null && list.size() > 0) {
				elements.addAll(list);
			}
			
			list = null; //PERF
			
			if (deep) {
				
				/* PERF
				//
				Element[] children = (Element[]) e.getAllChildren().toArray(
						new Element[0]);

				// old: Element[] children = getChildElements();

				for (int i = 0; i < children.length; i++) {
					children[i].getChildElements(element, deep, elements);
				}
				
				children = null; //PERF
				*/
				
				for (Element c : e.getAllChildren() )					
					c.getChildElements(element, deep, elements);
									
			}

		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println(exception.toString());
		}

	}

	public Element[] getChildElements(Element element, int nestingLevel) {

		return getChildElements(element, nestingLevel, true);

	}

	public Element[] getChildElements(Element element, int nestingLevel,
			boolean absoluteLevel) {

		return getChildElements(element, nestingLevel, true, false);

	}

	public Element[] getChildElements(Element element, int nestingLevel,
			boolean absoluteLevel, boolean oneParentOnly) {

		Element[] children = getChildElements(element, true);

		String parent = element.getParent().getName();
		//String name = element.getName();

		List list = new java.util.ArrayList();

		int offset = absoluteLevel ? 0 : getNesting();

		for (int i = 0; i < children.length; i++) {

			if ((children[i].getNesting() - offset) == nestingLevel) {

				if (!oneParentOnly
						|| children[i].getParent().getName().equalsIgnoreCase(
								parent)) {

					list.add(children[i]);

				}

			}

		}

		// Object[] o = (Element[]) (list.toArray( new Element[0]));
		Object[] o = (list.toArray(Element.NULL_ELEMENT_ARRAY));
		children = o.length > 0 ? (Element[]) o : null;

		return children;

	}


	public Element getDocument() {

		/*
		 * Element[] e1 = getChildElements();
		 * 
		 * Element[] e2 = new Element[ e1.length + 1 ];
		 * 
		 * e2[0] = this;
		 * 
		 * for (int i = 0; i < e1.length; i++) e2[i+1] = e1[i];
		 * 
		 * e1 = null;
		 * 
		 * 
		 * return e2;
		 * 
		 */

		//return (getRoot().getChildElements())[0];
		
		Element retval = getRoot();
		
		List<Element> children;
		
		if ( retval.getName().equals("ROOT") && (children = retval.getAllChildren()).size() > 0 )
			retval = children.get(0);
		
		return retval;

	}
	/*
	public final int getIndex() {
		return index;
	}
*/
	private Map<String, Attribute> getMyAttributes() {
		return getMyAttributes(false);
	}

	private Map<String, Attribute> getMyAttributes(boolean readOnly) {

		if (myAttributes == null && !readOnly) {

			synchronized (this) {
				myAttributes = new java.util.HashMap<String, Attribute>();
			}

		}

		return myAttributes;
	}

	public String getName() {
		return name;
	}

	public int getNesting() {
		return nesting;
	}
/*
	private int getNextIndex() {
		return ++nextIndex;
	}

	private void setNextIndex(int index) {
		nextIndex = index;
	}
	*/
	public final int getOrdinal() {
		return ordinal;
	}

	public Element getParent() {
		return parent;
	}

	public Element[] getParentElements() {
		return getParentElements(null);
	}

	public Element[] getParentElements(Element element) {

		Element e = this;

		List elements = e.getAllParents();

		try {

			if (element != null) {

				List matches = new java.util.ArrayList(elements.size());

				for (int i = 0; i < elements.size(); i++) {

					if (((Element) elements.get(i)).equals(element))
						matches.add(elements.get(i));

				}

				elements = matches;

			}

		} catch (Exception exception) {
			exception.printStackTrace();
			System.out.println(exception.toString());
		}

		return (Element[]) elements.toArray(Element.NULL_ELEMENT_ARRAY);

	}

	public final Element getRoot(boolean isHardRoot) {

		if (isHardRoot)
			return this.root;
		else
			return getRoot();
	}


	public final Element getRoot() {

		Element parent = this.getParent();

		Element root = getRoot(true); // CHANGE 0909121456

		if (parent == null)
			return this;

		if (root == this) // CHANGE 0909121456
			return this;

		if ( parent == root )
			return parent;

		int hash2 = -2, hash1 = -1;

		//while (parent.hasParent() ) { // CHANGE 0909121456
		while (parent.hasParent()) {

			hash1 = parent.hashCode();

			//if ( hash1 == hash2 )
			//break;

			if ( parent == root )
				break;

			parent = parent.getParent();


			hash2 = parent.hashCode();

		}

		return parent;

		//return root;
	}

	public String getValue() {
		return value;
	}

	public boolean hasAttribute(String name) {
		return getAttribute(name) != null;
	}

	public boolean hasAttribute(String name, String value) {
		return getAttribute(name) != null
		&& ( getAttribute(name).getValue() != null || value == null) 
		&& getAttribute(name).getValue().equalsIgnoreCase(value);
	}

	public final boolean hasChildren() {
		return getAllChildren().size() > 0;
	}

	public boolean hasParent() {
		return getParent() != null;
	}

	public final boolean matches(Element e) {

		return isMatch(e, true);
	}

	private final boolean isMatch(Element e, boolean isMatch) {

		//String name = e.getName();

		Collection<Attribute> attributes = e.getAttributes().values(); //PERF
		//Attribute[] myAttributes = this.getAttributes();

		isMatch = ( e.getName() == null || e.getName().equals("*") ) 
		|| ( (e.isQbeEquality() &&  e.getName().equalsIgnoreCase(this.getName()))  || (!e.isQbeEquality() &&  !e.getName().equalsIgnoreCase(this.getName())) ) ;

		isMatch = isMatch
		&& (e.getValue() == null || (this.getValue() != null && 
				(e.isQbeEquality() && e.getValue().equalsIgnoreCase(this.getValue())) || (!e.isQbeEquality() && !e.getValue().equalsIgnoreCase(this.getValue()))  ));

		String attName = null, value = null, myValue = null;
		Attribute myAtt = null;
		boolean attMustMatch;

		for (Attribute att : attributes) {
			
			attName = att.getName();
			value = att.getValue();
			attMustMatch = att.isQbeMatch();

			myAtt = this.getAttribute(attName);

			if (myAtt != null)
				myValue = myAtt.getValue();

			// (value == null) 071207  
			isMatch = isMatch
			&& ( (value == null) || (myAtt != null
					&& value.equals("*") ) 
					|| (myValue != null && myValue
							.equalsIgnoreCase("*")) || (myValue != null && 
									( attMustMatch && myValue.equalsIgnoreCase(value) ) || ( !attMustMatch && !myValue.equalsIgnoreCase(value) )    ));

			
			if ( ! isMatch )
				break;
			
		}
		
		
		for (Element child : e.getAllChildren()) {

			boolean match = false;

			for (Element myChild : this.getAllChildren()) {

				match = match || myChild.isMatch(child, isMatch);
				
				if ( match )
					break;

			}

			isMatch = isMatch && match;

			if ( !isMatch )
				break;

		}
		return isMatch;

	}

	/*
	 * public int countChildren(String type) {
	 * 
	 * int num = 0;
	 * 
	 * try {
	 * 
	 * if (type != null) { num = ((List)(getChildren().get(type))).size(); }
	 * else {
	 * 
	 * List[] typeList = (List[])(getChildren().values().toArray(new List[0]));
	 * 
	 * for (int i = 0; i < typeList.length; i++) { num += typeList[i].size(); } } }
	 * catch(Exception e){}
	 * 
	 * return num; }
	 */



	public void deleteAttributes() {

		getMyAttributes().clear();

	}

	public void deleteAttribute(Attribute attribute) {

		deleteAttribute( attribute.getName() );

	}

	public void deleteAttribute(String attribute) {

		getMyAttributes().remove(attribute.toUpperCase());

	}

	public Element copy() {

		Element clone = new Element();

		clone.copy(this, true, true);

		return clone;

	}

	public Element copy( Element e ) {

		return copy(e, false, false);


	}

	public Element copy( Element e, boolean additive ) {

		return copy(e, additive, false);

	}

	public Element copy(Element e, boolean additive, boolean deep) {

		///setName( e.getName() );

		//////////////////////////////

		setName( e.getName());
		setFalseRoot( e.isFalseRoot());
		setNesting( e.getNesting());
		setRoot( e.getRoot() );
		setNesting( e.getNesting() );
		//setParent(e.getParent()); //CHANGE 0804101400
		setValue(e.getValue());
		setQbeEquality( e.isQbeEquality() );

		setNamespace( e.getNamespace() );

		/////////////////////////////

		if (!additive)
			deleteAttributes();

//		Attribute[] att = e.getAttributes();

		for (Attribute att : e.getAttributes().values())
			setAttribute( att.getName(), att.getValue() ) ;

		

		//setNesting( e.getNesting() );

		if (deep) {

			//setRoot( e.getRoot() );

			for (Element element : e.getAllChildren()) {

				Element child = new Element();

				child.copy( element, additive, true);

				addChild(child, false);

			}
			//TODO
		}

		normalise();

		return this;

	}




	public Element replicate() {

		return replicate(false);

	}

	public Element replicate(boolean deep) {

		Element copy = new Element();

		copy.setName(getName());
		copy.setFalseRoot(isFalseRoot());
		copy.setNesting(getNesting());
		copy.setRoot(copy);

		for (Attribute att : getAttributes().values())
			copy.setAttribute(att.getName(), att.getValue());

		if (deep) {
			//TODO
		}

		return copy;
	}

	private void setAllParents(List allParents) {
		this.allParents = allParents;
	}

	public void setAttribute(Attribute attribute) {

		String key = attribute.getName().toUpperCase();

		//if (getMyAttributes().get(key) != null)
		//getMyAttributes().remove(key);

		getMyAttributes().put(key, attribute);

	}

	public void setAttribute(String name, String value) {
		setAttribute(new Attribute(name, value));
	}

	/*
	public final void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	 */

	/*
	private void setIndex(int index) {
		this.index = index;
	}
*/
	public void setName(String name) {
		this.name = name;
	}

	public final void setNesting(int nesting) {
		this.nesting = nesting;
	}

	private final void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public void setParent(Element parent) {
		
		this.parent = null;

		this.parent = parent;

		if (parent != null) 
			setRoot(parent.getRoot());

	}


	public final void setRoot(Element root) {
		
		this.root = null;
		this.root = root;
	}

	public final void setRoot(Element root, boolean isHardRoot) {
		setRoot( root );
		if ( isHardRoot )
			setFalseRoot( false );
	}

	public void setValue(String value) {
		this.value = value == null ? null : value.trim();
	}

	private boolean isFalseRoot() {
		return falseRoot;
	}

	protected void setFalseRoot( boolean falseRoot ) {
		this.falseRoot = falseRoot;
	}


	public final boolean hasText() {

		return getValue() != null && ! getValue().trim().equals(""); 


	}



	/*
	public String toString(ElementFilter filter) {

		StringBuffer retval = new StringBuffer();
		StringBuffer attBuf = new StringBuffer();
		StringBuffer childBuf = new StringBuffer();

		Element e = isFalseRoot() ? (getChildElements())[0] : this;


		int numChildren = e.countChildren();


		retval.append("").append("<").append(e.getName());

		retval.append( (e.attributeCount() == 0 ? (e.hasChildren() || e.hasText() ?  ">" : "/>")  :  " ") );


		Element[] child = e.getChildElements();

		int childCount = 0;
		for (int i = 0; i < child.length; i++) {

			if ( filter != null && !filter.applyFilter(child[i]) )
				continue;

			childBuf.append( com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting()) ).append(
					 child[i].toString(filter) ) ;

			childCount++;
		}


		Attribute[] att = e.getAttributes();

		int attCount = 0;
		for (int i = 0; i < att.length; i++) {

			if ( filter == null || filter.applyFilter(e, att[i])) {			
				attBuf.append( att[i].toString() + (i < att.length - 1 ? " " : "") );
				attCount++;
			}
		}


		if (  attCount > 0 && !( childCount > 0 || e.hasText()) )
			retval.append( "/>" );
		else if (attCount > 0 && childCount > 0)
			retval.append( ">" );


		if (e.getValue() != null && !e.getValue().trim().equals("")) {
			retval.append( e.getValue() +  (e.hasChildren() ? "" : "</" + e.getName() + ">") );

		}		


		retval.append( System.getProperty("line.separator") );		


		if (numChildren > 0) {

			retval.append( com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting() - 1) )
					.append( "</" + e.getName() + ">" );

			retval.append( System.getProperty("line.separator") );

		}

		return retval.toString();
	}

	 */	




	public String toString() {

		StringBuffer retval = new StringBuffer();

		//		Element e =  isFalseRoot() ? (getChildElements())[0] : this;
		Element e =  this;
		int childCount = e.getAllChildren().size();
		int attributeCount = e.attributeCount();
		boolean hasChildren = childCount > 0;


		retval.append("").append("<").append(e.getName());

		retval.append( (attributeCount == 0 ? (hasChildren || e.hasText() ?  ">" : "/>")  :  " ") );

		Iterator<Attribute> i = e.getAttributes().values().iterator(); //PERF
		
		
		Attribute att;
		while (i.hasNext()) {

			att = i.next();
			
			retval.append( att.toString() + (i.hasNext() ? " " : "") );
			
		}



		if (  attributeCount > 0 && !( hasChildren || e.hasText()) )
			retval.append( "/>" );
		else if (attributeCount > 0 && (hasChildren || e.hasText()))
			retval.append( ">" );


		if (e.getValue() != null && !e.getValue().trim().equals("")) {
			retval.append( e.getValue() +  (hasChildren ? "" : "</" + e.getName() + ">") );

		}		


		retval.append( System.getProperty("line.separator") );		

		for (Element child : e.getAllChildren()) {
			retval.append( com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting()) ).append(
							child.toString() ) ;
		}

		if (hasChildren) {

			retval.append( com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting() - 1) )
					.append( "</" + e.getName() + ">" );

			retval.append( System.getProperty("line.separator") );

		}

		return retval.toString();
	}




	/*
	public String toString() {

		String retval;

		//Element e = this == getRoot() ? (getChildElements())[0] : this; // 071205		

		Element e = isFalseRoot() ? (getChildElements())[0] : this;

		int numChildren = e.countChildren();

		// retval = "" + e.getNesting() + ":" + "" + e.getIndex() + "<" +
		// e.getName() + (e.attributeCount() == 0 ? ">" : " ");

		retval = "" + "<" + e.getName();

		retval += (e.attributeCount() == 0 ? (e.hasChildren() || e.hasText() ?  ">" : "/>")  :  " ");

		Attribute[] att = e.getAttributes();

		for (int i = 0; i < att.length; i++) {
			retval += att[i].toString() + (i < att.length - 1 ? " " : "");
		}


		if (  e.attributeCount() > 0 && !( e.hasChildren() || e.hasText()) )
			retval += "/>";
		else if (e.attributeCount() > 0 && e.hasChildren())
			retval += ">";




		if (e.getValue() != null && !e.getValue().trim().equals("")) {
			retval += e.getValue() +  (e.hasChildren() ? "" : "</" + e.getName() + ">");

		}		


		retval += System.getProperty("line.separator");		

		Element[] child = e.getChildElements();

		for (int i = 0; i < child.length; i++) {
			retval += com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting())
					+ child[i].toString();
		}

		if (numChildren > 0) {

			retval += com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting() - 1)
					+ "</" + e.getName() + ">";

			retval += System.getProperty("line.separator");

		}

		return retval;
	}
	 */
	public boolean unsetAttribute(Attribute attribute) {

		String key = attribute.getName().toUpperCase();

		if (getMyAttributes().get(key) != null) {
			getMyAttributes().remove(key);
			return true;
		}

		return false;

	}

	public void unsetAttribute(String name) {
		unsetAttribute(new Attribute(name, "*"));
	}

	public final boolean isQbeEquality() {
		return qbeEquality;
	}

	public final void setQbeEquality(boolean qbeMatch) {
		this.qbeEquality = qbeMatch;
	}

	public final Element[] find(String path) {

		return find( path, false );

	}

	public final Element[] find(String path, boolean deep) {

		if ( path.equalsIgnoreCase("") )
			return new Element[] {this};

		path = path.toUpperCase();

		if ( ! path.endsWith(PATH_SEPARATOR) )
			path += PATH_SEPARATOR;


		boolean absolute = path.startsWith(PATH_SEPARATOR);

		List results = new java.util.ArrayList();

		Element start = absolute ? getRoot() : this;

		if ( absolute ) {

			deep = true;




			path = getPath() + path;

		}

		addPathMatches( path, start, results, deep);

		return (Element[]) results.toArray( Element.NULL_ELEMENT_ARRAY);

	}

	private final void addPathMatches(String path, Element e, List results, boolean deep) {

		String childPath = null;

		if ( getPath().equals( path ) ) 
			results.add( this );
		
		
		for (Element element : e.getAllChildren()) {

			childPath = element.getPath();

			if ( childPath.equals( path ) ) 
				results.add( element );


		}


		if (deep) {

			for (Element element : e.getAllChildren() ) {

				addPathMatches( path, element, results, true);

			}

		}



	}



	public final static Element pathToElement(String path) {

		Element pathElement = null;
		Element element, parent;
		int k = 0;


		String[] pathNodes = path.split(PATH_SEPARATOR, 0);

		while ( pathNodes[k].length() == 0)
			k++;

		pathElement = new Element( pathNodes[k++].toUpperCase() );
		parent = pathElement;


		for (int i = k; i <pathNodes.length; i++) {

			while ( pathNodes[i].length() == 0)
				i++;

			element = new Element( pathNodes[i].toUpperCase());

			parent.addChild( element  );

			parent = element; 

		}

		return pathElement;

	}

	public String getPath(String startFrom) {

	    	StringBuffer temp = new StringBuffer();
	    
		String path = null;

		if (startFrom == null)
			return null;

		//startFrom = PATH_SEPARATOR + startFrom + PATH_SEPARATOR;
		
		temp.append(PATH_SEPARATOR).append(startFrom).append(PATH_SEPARATOR);

		path = getPath();

		int pos = path.indexOf( temp.toString().toUpperCase() );
		
		//int pos = path.indexOf( startFrom.toUpperCase() );
		
		if ( pos < 0 )
			return null;


		//path = path.substring(pos + startFrom.length() );
		
		path = path.substring(pos + temp.length() );
		
		return path;


	}


	public String getPath() {

		StringBuffer path = new StringBuffer("");

		Element[] parents = getParentElements();

		path.append(PATH_SEPARATOR);

		for (int i = parents.length-1 ; i > -1; i--) {

			if ( !parents[i].isFalseRoot() ) {

				path.append( parents[i].getName().toUpperCase());			
				path.append(PATH_SEPARATOR);

			}

		}


		path.append( getName().toUpperCase() );
		path.append(PATH_SEPARATOR);


		return path.toString();



	}


	public Element[] xfind(String path) {

		Element base = null;
		boolean deep = false;

		if ( path.startsWith("../") ) {
			return getParent().xfind(path.substring(3));	
		}

		else if ( path.equalsIgnoreCase(".") ) {
			return new Element[]{ this };
		}

		else if ( path.startsWith("//") ) {

			if  (! isFalseRoot() ) {
				base = new Element("ROOT");
				base.addChild(getRoot());
			} else
				base = getRoot();

			path = path.substring(1);

			deep = true;

		} else if ( !path.startsWith("/") ) {

			base = this;

			deep = true;

			path = "/" + path;

		} else {

			base = this;

			deep = false;

		}

		String[] pathNames = path.split( PATH_SEPARATOR, 0 );

		List<Element> temp = new java.util.ArrayList<Element>(); 



		for (int i = 0; i < pathNames.length; i++) {

			if ( pathNames[i].equals("")) {

				if ( i == 0 )
					continue;
				else
					temp.add( null );

			}
			else			
				temp.add( buildElement( pathNames[i] ) );		


		}

		//Element[] search = (Element[]) temp.toArray( new Element[0] );

		List<Element> results = new java.util.ArrayList<Element>();

		query( base, temp, 0, results, deep );
		
		temp = null;
		
		return (Element[]) results.toArray( Element.NULL_ELEMENT_ARRAY );

	}




	public Element[] xpath(String path) {
		return xpath(this, path);
	}


	public static Element[] xpath(Element e, String path) {

		Element base = null;
		boolean deep = false;
		boolean baseScope = false;

		if ( path.startsWith("..") ) {
			
			baseScope=true;
			
			base = e. getParent();

			if ( base == null )
				base = e.getRoot();

			deep = false;

			path = path.substring(2);
		}

		else if ( path.startsWith("./") ) {

			base = e. getParent();

			if ( base == null )
				base = e.getRoot();

			deep = false;

			path = "/" + path;
		}
		else if ( path.startsWith("//") ) {

			baseScope = true;

			base = e;

			deep = true;

			path = path.substring(2);
		} 

		else if ( path.startsWith("/") ) {

			baseScope = true;

			base = e.getDocument();

			path = path.substring(1);

			deep = false;

		} 
		else if ( !path.startsWith("/") ) {

			base = e;

			deep = false;

			path = "/" + path;

		} else {

			base = e;

			deep = false;

		}

		String[] pathNames = path.split( PATH_SEPARATOR, 0 );

		List<Element> temp = new java.util.ArrayList<Element>(); 



		for (int i = 0; i < pathNames.length; i++) {

			if ( pathNames[i].equals("")) {

				if ( i == 0 )
					continue;
				else
					temp.add( null );

			}
			else			
				temp.add( buildElement( pathNames[i] ) );		


		}

		//Element[] search = (Element[]) temp.toArray( new Element[0] );

		List<Element> results = new java.util.ArrayList<Element>();

		Element.query( base, temp, 0, results, deep, baseScope );
		
		temp = null;
		
		return (Element[]) results.toArray( Element.NULL_ELEMENT_ARRAY );

	}




	/*
	public static Element[] xpath(Element e, String path) {

		Element base = null;
		boolean deep = false;

		if ( path.startsWith("../") ) {
			return xpath(e.getParent(), path.substring(3));	
		}

		else if ( path.startsWith("./") ) {

			base = e. getParent();

			if ( base == null )
				base = e.getRoot();

			   deep = false;

			   path = "/" + path;
		}
		else if ( path.startsWith("//") ) {

			base = e. getParent();

			if ( base == null )
				base = e.getRoot();

			   deep = true;

			   path = path.substring(1);
		 } 

		else if ( path.startsWith("/") ) {

		  base = e.getRoot();

		  if ( base == e && e.hasParent() )		  
			  base = e.getRoot().getParent();
		  else if ( base == e && ! e.hasParent() ) {

			  base = new Element("ROOT");

			  base.addChild(e);

		  }


		   path = path.substring(1);

		   deep = false;

		 } 
		else if ( !path.startsWith("/") ) {

		   base = e;

		   deep = false;

		   path = "/" + path;

		} else {

		   base = e;

		   deep = false;

		}

		String[] pathNames = path.split( PATH_SEPARATOR, 0 );

		List temp = new java.util.ArrayList(); 



		for (int i = 0; i < pathNames.length; i++) {

			if ( pathNames[i].equals("")) {

				if ( i == 0 )
					continue;
				else
					temp.add( null );

			}
			else			
				temp.add( buildElement( pathNames[i] ) );		


		}

		Element[] search = (Element[]) temp.toArray( new Element[0] );

		List results = new java.util.ArrayList();

		e.query( base, search, 0, results, deep );

		return (Element[]) results.toArray( new Element[0]);

		}

	 */	
	private static Element buildElement(String xpathName) {

		Element retval = null;

		int pos2, pos;

		pos = xpathName.indexOf("[");

		if ( pos < 0 )
			return new Element( xpathName );

		String name, attribute, value;

		name = xpathName.substring(0, pos);

		retval = new Element(name);

		xpathName = xpathName.substring(pos+1);

		while ( pos >= 0 ) {

			pos = xpathName.indexOf("@");			
			pos2 = xpathName.indexOf("=");

			attribute = xpathName.substring(pos+1, pos2).trim();

			pos = xpathName.indexOf("\"");			
			pos2 = xpathName.indexOf("\"", pos+1);

			value = xpathName.substring(pos+1, pos2).trim();

			retval.setAttribute(attribute, value);

			pos = xpathName.indexOf("[");

			if (pos >= 0)				
				xpathName = xpathName.substring(pos+1);


		}

		return retval;

	}
	private static void query( Element base, List<Element> search, int pos, List<Element> results, boolean deep ) {
		query(base, search, pos, results, deep, false );
	}

	private static void query( Element base, List<Element> search, int pos, List<Element> results, boolean deep, boolean baseScope ) {

		Element srch = search.get(pos);
		
		if ( srch == null ) {

			deep = true;
			pos++;

		}

		Element[] nodes = base.getChildElements(srch, deep, baseScope);


		deep = false;

		if ( pos == search.size() - 1  ) {

			for ( int i = 0; i < nodes.length; i++ )
				results.add( nodes[i]);
			
			nodes = null; //PERF
			
			return;

		}
		
		srch = null; //PERF
		base = null; //PERF
		
		pos++;

		for (int j = 0; j < nodes.length; j++) {

			query( nodes[j],  search, pos, results, deep );

		}

		nodes = null; //PERF
	}



	private static void addElement(Element parent, Node elementNode) {

		Element element = new Element(elementNode.getNodeName());

		Node n1 = elementNode;
		Node n2 = n1.getFirstChild();

		if (n2 != null) {

			//String n3 = n2.getNodeValue();

			element.setValue(elementNode.getFirstChild().getNodeValue());

		}

		NamedNodeMap attributes = elementNode.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {

			String name = attributes.item(i).getNodeName();
			String value = attributes.item(i).getNodeValue();

			element.setAttribute(name, value);

		}


		parent.addChild(element);

		NodeList nodes = elementNode.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {

			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				addElement(element, nodes.item(i));
			}
		}

		nodes = null;

	}

/*
	public static final Element parse(String xml) throws Exception {

		// Element retval = new Element("XML");

		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

				
		Element retval = new Element("ROOT");
		retval.setFalseRoot(true); // 071205

		Document doc = documentBuilder.parse(new InputSource(new StringReader(xml)));

		NodeList nodes = doc.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {

			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

				addElement(retval, nodes.item(i));

			}

		}

		doc = null;
		nodes = null;
		
		Element oldRoot = retval;

		retval = oldRoot.getAllChildren().get(0);

		retval.setRoot( retval, true );

		oldRoot = null;

		return retval;

	}



*/
	
	public static final Element parse(String xml) throws Exception {

		//get a new instance of parser
		Stack<Element> parents = new Stack<Element>();

		SAXParser saxParser = getSaxParserFactory().newSAXParser();

				
		Element retval = new Element("ROOT");
		retval.setFalseRoot(true); // 071205

		parents.push(retval);
		
		saxParser.parse(new InputSource(new StringReader(xml)), new ElementParser(parents));
		
		Element oldRoot = retval;

		retval = oldRoot.getAllChildren().get(0);

		retval.setRoot( retval, true );

		oldRoot = null;

		return retval;

	}
	
	public final boolean hasAttributes() {

		return getAttributes().values().size() > 0;

	}

	public final String getNamespace() {
		return namespace;
	}

	public final void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	
	public String toEscapedString() throws Exception{
		
		String retval;
	
		//Element e = this == getRoot() ? (getChildElements())[0] : this; // 071205		
		
		Element e1 = isFalseRoot() ? (getChildElements())[0] : this;
		
		Element e = new Element();
	
//		Element e =null;
		
//		e = e1.replicate(false);
		
//		e = (Element) DeepCopy.copy((Element) e1);
		
		e.copy(e1, true, true);
		
		int numChildren = e.countChildren();
	
		// retval = "" + e.getNesting() + ":" + "" + e.getIndex() + "<" +
		// e.getName() + (e.attributeCount() == 0 ? ">" : " ");
	
		retval = "" + "<" + e.getName();
		
		retval += (e.attributeCount() == 0 ? (e.hasChildren() || e.hasText() ?  ">" : "/>")  :  " ");
	
		//Attribute[] att = e.getAttributes();
		
		String attValue = null, attName = null, attrib = null;
		
		int size = e.getAttributes().size();
		
		Iterator<Attribute> i = e.getAttributes().values().iterator();
		Attribute att = null;
		while(i.hasNext()) {
			
//			attName = att[i].getName();
			att = i.next();
			attValue = xmlBeanEscape(att.getValue());
			
			att.setValue(attValue);
			
//			attrib = attName + " = " + attValue ;
			
			retval += att.toString() + (i.hasNext() ? " " : "");
//			retval += attrib + (i < att.length - 1 ? " " : "");
		}
		
		
		if (  e.attributeCount() > 0 && !( e.hasChildren() || e.hasText()) )
			retval += "/>";
		else if (e.attributeCount() > 0 && e.hasChildren())
			retval += ">";
			
		
		
		/*
		if (  e.attributeCount() > 0 && !( e.hasChildren() || e.hasText()) )
			retval += "/>";
		else if (  !( e.hasChildren() || e.hasText()) )
			retval += "/";		
		else if (e.attributeCount() > 0 && e.hasChildren())
			retval += ">";
		*/
		
		if (e.getValue() != null && !e.getValue().trim().equals("")) {
			retval += e.getValue() +  (e.hasChildren() ? "" : "</" + e.getName() + ">");
			
		}		
		
	
		retval += System.getProperty("line.separator");		
		
		Element[] child = e.getChildElements();
	
		for (int j = 0; j < child.length; j++) {
			retval += com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting())
					+ child[j].toEscapedString();
		}
	
		if (numChildren > 0) {
	
			retval += com.twc.eis.lib.string.StringHelper.replicate(" ", e
					.getNesting() - 1)
					+ "</" + e.getName() + ">";
	
			retval += System.getProperty("line.separator");
	
		}
	
		return retval;
	}
	
	private String xmlBeanEscape(String s) {
		
		if ( s == null )
			return null;

		s = s.trim();
//		System.out.println("XMLBEAN ESCAPE BEFORE********\n" + s );

		s = s.replaceAll("&", "&amp;");

		s = s.replaceAll("\'", "&apos;");
		
		s = s.replaceAll("\"", "&quot;");
		
		s = s.replaceAll("<", "&lt;");
		
		s = s.replaceAll(">", "&gt;");

		
		return s;
		
	}

	

	/**
	 * @param saxParserFactory the saxParserFactory to set
	 */
	public static void setSaxParserFactory(SAXParserFactory saxParserFactory)
	{
	    Element.saxParserFactory = saxParserFactory;
	}

	/**
	 * @return the saxParserFactory
	 */
	public static SAXParserFactory getSaxParserFactory()
	{
	    return saxParserFactory;
	}



	final static class ElementParser extends DefaultHandler {
	    
	    private Stack<Element> parents = null;
	    
	    
	    public ElementParser(Stack<Element> parents) {
		
		setParents(parents);
		
	    }
	    	
		public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) throws SAXException {
		
			Element element = new Element(tagName);
			

			String name, value;
			for (int i = 0; i < attributes.getLength(); i++) {

				name = attributes.getLocalName(i);
				value = attributes.getValue(i);

				element.setAttribute(name, value);

			}


			getParents().peek().addChild(element);

		    
			getParents().push(element);
		    
		}

		public void characters(char[] ch, int start, int length) {
		    
		    Element current = getParents().peek();
		    
		    current.setValue( new String(ch, start, length) );
						
			
		}

		
		public void endElement(String uri, String localName,  String qName) throws SAXException {
		    
		    getParents().pop();
		    
		}

		public void setParents(Stack<Element> parents)
		{
		    this.parents = parents;
		}

		public Stack<Element> getParents()
		{
		    return parents;
		}
	}
	
	

}
