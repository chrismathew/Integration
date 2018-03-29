package org.cms.hios.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLUtils {
	public static String marshallForXMLRootElement(Object o) throws JAXBException, PropertyException {
		JAXBContext context = JAXBContext.newInstance(o.getClass());
		Marshaller marshaller = context.createMarshaller();
		StreamResult rs = new StreamResult(new StringWriter());
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(o, rs);
		return rs.getWriter().toString();
	}

	public static Object unmarshallForXMLRootElement(String xml, String clazz) throws ClassNotFoundException, JAXBException {
		JAXBContext context = JAXBContext.newInstance(Class.forName(clazz));
		ByteArrayInputStream input = new ByteArrayInputStream (xml.getBytes());	    
		Unmarshaller unmarshaller = context.createUnmarshaller();	    
		return unmarshaller.unmarshal(input);
	}
	
	public static String marshall(Object o) throws JAXBException, PropertyException, XMLStreamException, SOAPException, ParserConfigurationException, IOException
	    {
		

	        JAXBContext context = JAXBContext.newInstance(o.getClass().getPackage().getName());
	        Marshaller marshaller = context.createMarshaller();
	        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
	        
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        Document doc = dbf.newDocumentBuilder().newDocument();
	        marshaller.marshal(o, doc);
	        
	        MessageFactory factory = MessageFactory.newInstance();
	        SOAPMessage message = factory.createMessage();
	        message.getSOAPBody().addDocument(doc);	        
	        
	        message.saveChanges();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        message.writeTo(out);
	        String strMsg = new String(out.toByteArray());

	        return strMsg;
	    }
	
	public static <T> Object unmarshall(String xml, String clazz, String simpleClassName) throws ClassNotFoundException, JAXBException, XMLStreamException {
		
		//JAXBElement<ValidIssuersProducts> obj = (JAXBElement<ValidIssuersProducts>) unmarshaller.unmarshal(new StreamSource(input), ValidIssuersProducts.class);
		ByteArrayInputStream input = new ByteArrayInputStream (xml.getBytes());	 
		
		XMLInputFactory xif = XMLInputFactory.newFactory();
		StreamSource xmlString = new StreamSource(input);
		XMLStreamReader xsr = xif.createXMLStreamReader(xmlString);
		xsr.nextTag();
		while(!xsr.getLocalName().equals(simpleClassName)) {
	            xsr.nextTag();
	    }
		
		JAXBContext context = JAXBContext.newInstance(Class.forName(clazz));
		Class classType = Class.forName(clazz);
		
		Unmarshaller unmarshaller = context.createUnmarshaller();	
		JAXBElement<T> jb = unmarshaller.unmarshal(xsr, classType);
		xsr.close();
		Object object = jb.getValue();
		return  object;
		
	}
	
	static Map<String, Schema> cachedSchemas = new HashMap<String, Schema>();

	/**
	 * It will validate the request xml with XSD.
	 * 
	 * @param xmlString
	 * @param xsdUrl
	 * @return
	 * @throws Exception
	 */
	public static boolean isValidXml(String xmlAsString, String xsdFileName)
			throws SAXException, IOException {
		
		Schema schema = cachedSchemas.get(xsdFileName);
		if (schema == null) {
			URL xsdURL = XMLUtils.class.getClassLoader()
					.getResource(xsdFileName);
			String schemaLang = "http://www.w3.org/2001/XMLSchema";
			SchemaFactory factory = SchemaFactory.newInstance(schemaLang);
			schema = factory.newSchema(xsdURL);			
			cachedSchemas.put(xsdFileName, schema);
		}

		Validator validator = schema.newValidator();
		Source src = new StreamSource(new java.io.StringReader(xmlAsString));
		validator.validate(src);
		return true;
	}

}
