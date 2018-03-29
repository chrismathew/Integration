package gov.hhs.cms.base.common.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XSLUtil {

	public static String transformXML(StreamSource xml, StreamSource source) throws TransformerException {
		StringWriter writer = new StringWriter();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(source);
		transformer.transform(xml, new StreamResult(writer));
		return writer.toString();
	}

	public static String transformXML(String xml, StreamSource source) throws TransformerException {
		StringWriter writer = new StringWriter();
		TransformerFactory tFactory = TransformerFactory.newInstance();
		StreamSource xmlSource = new StreamSource(new StringReader(xml));
		Transformer transformer = tFactory.newTransformer(source);
		transformer.transform(xmlSource, new StreamResult(writer));
		return writer.toString();
	}
}
