package com.twc.eis.lib.xml;

import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.InputSource;
import java.io.StringReader;

public class XMLElementParser {

	public static void main(String[] args) {

		try {
			
			XMLElementParser parser = new XMLElementParser();
			/*
			long t1, t2, t3, t4;
			
			String xs = com.twc.eis.lib.file.FileUtil
			.fileToString("/mnt/hp/fileshare/project/rr/novell/ent/design/IDM/tsdl/xml/ASB_CustomerData_Create_Request.xml");
			
			t1 = System.currentTimeMillis();
			
			org.apache.xmlbeans.XmlObject x = org.apache.xmlbeans.XmlObject.Factory.parse(xs);
			
			t2 = System.currentTimeMillis();
			
			System.out.println("\nBeans parse: " + (t2 - t1));
			

			t1 = System.currentTimeMillis();
			
			Element xx = parser.parse(xs); 
			
			t2 = System.currentTimeMillis();
			
			System.out.println("\nElement parse: " + (t2 - t1));
			

			
			 org.apache.xmlbeans.XmlCursor cursor = x.newCursor();
			 
			 //cursor.selectPath( "$this//CustomerData/Customer/Role/@id" );
			
			t1 = System.currentTimeMillis();
			 			 
				String t=null;;
					 

					 for (int i = 0; i < 1000; i++) {
						 
						 	cursor.push();
						 						 
							 cursor.selectPath( "$this//CustomerData/Customer/Role/@id" );
							 
							 cursor.toNextSelection();
					 
					    t = cursor.getTextValue();
					    
						 cursor.pop();
					    
					 	}

						t2 = System.currentTimeMillis();
					 	
				        System.out.println("\nBeans role: " + t + " " + (t2-t1));
			 
			Element e1 = new Element("CustomerData");
			Element e2 = new Element("Customer");
			Element e3 = new Element("Role");
			//e3.setAttribute("id", "TAYO");
			
			e2.addChild(e3);
			e1.addChild(e2);
						

			t1 = System.currentTimeMillis();
			
			String rr=null;
			
			for (int i = 0; i < 1000; i++) {
			 
			rr = (xx.getDocument().getChildElements(e3, true))[0].getAttribute("id").getValue();
			
			}

			t2 = System.currentTimeMillis();
			
			System.out.println("\nElement role: " + rr + " " + (t2 - t1));
			
						
			 	System.exit(0);
			
			
			
					 	
			 */
			


			String xml = null, searchStr = null;

			if (args.length < 1)
				//xml = com.twc.eis.lib.file.FileUtil
					//	.fileToString("/mnt/hp/fileshare/project/rr/novell/ent/design/IDM/tsdl/xml/ASB_CustomerData_Create_Request.xml");

			xml = com.twc.eis.lib.file.FileUtil
			.fileToString("/mnt/hp/fileshare/project/rr/novell/ent/design/IDM/tsdl/xml/ASB_CustomerData_Create_Request.xml");

			
			else
				xml = com.twc.eis.lib.file.FileUtil.fileToString(args[0]);

			if (args.length < 2)
				searchStr = "CustomerData";
			else
				searchStr = args[1];

			Element element = parser.parse(xml).getRoot();
			
			Element[] results = element.xfind("/AsbMessage/Header");
			
			
			
			for (int i = 0; i < results.length; i++) {
				System.out.println(results[i].toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());

		}
	}

	private DocumentBuilder documentBuilder;

	public XMLElementParser() {

		try {

			setDocumentBuilder(DocumentBuilderFactory.newInstance()
					.newDocumentBuilder());

		} catch (Exception e) {
		}

	}

	private void addElement(Element parent, Node elementNode) {

		Element element = new Element(elementNode.getNodeName());

		Node n1 = elementNode;
		Node n2 = n1.getFirstChild();

		if (n2 != null) {

			String n3 = n2.getNodeValue();

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

	private DocumentBuilder getDocumentBuilder() {
		return documentBuilder;
	}

	public Element parse(String xml) throws Exception {

		// Element retval = new Element("XML");

		Element retval = new Element("ROOT");
		retval.setFalseRoot(true); // 071205
		
		Element root = retval;
		

		Document doc = getDocumentBuilder().parse(toInputSource(xml));

		NodeList nodes = doc.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {

			if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {

				addElement(retval, nodes.item(i));

			}

		}

		doc = null;
		nodes = null;

//		Element[] document = retval.getChildElements();

		retval = retval.getAllChildren().get(0);

		retval.setRoot( root );
		
		return retval;

	}

	private void setDocumentBuilder(DocumentBuilder documentBuilder) {
		this.documentBuilder = documentBuilder;
	}

	private InputSource toInputSource(String xml) {

		return new InputSource(new StringReader(xml));

	}

	
}