package gov.hhs.cms.base.common.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public abstract class XMLUtil {

	public static final int STRING = 1;
	public static final int XML = 2;


	private static DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			.newInstance();
	private static DocumentBuilder docBuilder = null;
	private static XPathFactory xpathFactory = XPathFactory.newInstance();
	private static XPath xpath = null;
	private static final TransformerFactory transformerFactory = TransformerFactory
			.newInstance();
	private static Transformer transformer = null;
	private static JaxbHelper jaxbHelper = new PooledJaxbHelper();

	private static final Logger LOG = Logger.getLogger(XMLUtil.class);

	static {
		try {
			docBuilderFactory.setNamespaceAware(true);
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			LOG.error(
					"Got ParserConfigurationException creating document builder: "
							+ ex.getMessage(), ex);
		}
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException ex) {
			LOG.error(
					"Got TransformerConfigurationException creating document builder: "
							+ ex.getMessage(), ex);
		}

		xpath = xpathFactory.newXPath();
	}

	/*@SuppressWarnings("rawtypes")
	private static String getJAXBCacheKey(Class classObject, String packageName) {
		String prefix = classObject.getClassLoader().toString();
		return prefix + ":" + packageName;
	}*/

	@SuppressWarnings("unused")
	public static <T> T getObject(String xmlRepresentation, Class<T> classObject)
			throws JAXBException {
		long start = System.currentTimeMillis();
		try {
			return doGetObject(xmlRepresentation, classObject);
		} finally {
			long time = System.currentTimeMillis() - start;

			// System.out.println("*** XML unmarshall took " + time +
			// " ms. Avg=" + (unmarshallTime / ++numUnmarshalls));
		}
	}

	@SuppressWarnings({ "rawtypes" })
	private static <T> T doGetObject(String xmlRepresentation, Class<T> classObject) throws JAXBException {
		if (xmlRepresentation == null || "".equals(xmlRepresentation)) {
			// TODO might want to handle this more gracefully; perhaps with an
			// XMLObjectNotFoundException
			// or something like that
			return null;
		}

		Object doc = null;
		try {
			if (!classObject.isAnnotationPresent(XmlRootElement.class)) {
				JAXBElement jaxbe = (JAXBElement) jaxbHelper.unmarshal(xmlRepresentation, classObject);
				doc = classObject.cast(jaxbe.getValue());
			} else {
				doc = jaxbHelper.unmarshal(xmlRepresentation, classObject);
				if (doc.getClass().equals(JAXBElement.class)) {
					doc = ((JAXBElement) doc).getValue();
				}
			}
		}catch(Exception e){
			throw new JAXBException(e.getMessage(),e.getCause());
		}
		return classObject.cast(doc);
	}

	public static String getXML(Object object, String namespace)
			throws JAXBException {
		return getXML(object,namespace,true);
	}
	
	@SuppressWarnings("unused")
	public static String getXML(Object object, String namespace, boolean includeDeclaration)
			throws JAXBException {
		long start = System.currentTimeMillis();
		try {
			return doGetXML(object, namespace, includeDeclaration);
		} finally {
			long time = System.currentTimeMillis() - start;
			// System.out.println("*** XML marshall took " + time + " ms. Avg="
			// + (marshallTime / ++numMarshalls));
		}
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static String doGetXML(Object object, String namespace, boolean includeDeclaration)
			throws JAXBException {
		// JAXB method
		Class classObject = object.getClass();
		String packageName = classObject.getPackage().getName();

		// for intelligent namespace determination
		if (packageName.equals("gov.hhs.cms.base.sim")) {
			namespace = FFEConfig.getProperty("ns.sim");
		} else if (packageName.equals("gov.hhs.cms.base.vo")) {
			namespace = FFEConfig.getProperty("ns.vo");
		} else if (packageName.equals("gov.hhs.cms.base.persistence")) {
			namespace = FFEConfig.getProperty("ns.persistence");
		} else if (packageName.equals("gov.hhs.cms.api.enrollmentgs.vo")) {
			namespace = FFEConfig.getProperty("ns.vo.enrollmentgs");
		} else if (packageName.equals("gov.hhs.cms.api.eligibilitygs.vo")) {
            namespace = FFEConfig.getProperty("ns.vo.eligibilitygs");
        }

			// get a key/class specific object to lock, so that
			// different classes can create their contexts in parallel
		
		String xmlRepresentation = null;
	

		
		//check on size of marshallerMap
		try{
			if (!classObject.isAnnotationPresent(XmlRootElement.class)) {
				if (namespace != null) {
					JAXBElement jaxbe = new JAXBElement(new QName(namespace, Character.toLowerCase(classObject.getSimpleName().charAt(0))+ classObject.getSimpleName().substring(1)),classObject, object);
					xmlRepresentation = jaxbHelper.marshal(jaxbe, includeDeclaration);
				} else {
					JAXBElement jaxbe = new JAXBElement(new QName(object.getClass().getSimpleName(), "local"), classObject, object);
					xmlRepresentation = jaxbHelper.marshal(jaxbe, includeDeclaration);
				}
			} else {
				xmlRepresentation = jaxbHelper.marshal(object, includeDeclaration);
			}
		} catch(Exception e){
			throw new JAXBException(e.getMessage(), e.getCause());
		}

		return xmlRepresentation;
	}

	public static String getXML(Object object) throws JAXBException {
		return getXML(object, null);
	}

	public static String evalXPath(String xml, String xpathStr) {
		return evalXPath(xml, xpathStr, STRING);
	}

	public static String evalXPath(String xml, String xpathStr, int returnType) {
		String[] xpathStrs = { xpathStr };
		int[] returnTypes = { returnType };
		String[] results = evalXPaths(xml, xpathStrs, returnTypes);
		return results[0];
	}

	public static String[] evalXPaths(String xml, String[] xpathStrs) {
		int[] returnTypes = new int[xpathStrs.length];
		for (int i = 0; i < returnTypes.length; i++) {
			returnTypes[i] = STRING;
		}
		return evalXPaths(xml, xpathStrs, returnTypes);
	}

	// TODO need to improve performance of this call if possible
	public static String[] evalXPaths(String xml, String[] xpathStrs,
			int[] returnTypes) {
		// long start = System.currentTimeMillis();

		String[] results = new String[xpathStrs.length];

		InputSource inputSource = new InputSource();

		try {
			inputSource.setCharacterStream(new StringReader(xml));
			Document doc = null;

			synchronized (docBuilder) {
				docBuilder.reset();
				doc = docBuilder.parse(new InputSource(new StringReader(xml)));
			}

			for (int i = 0; i < xpathStrs.length; i++) {
				if (returnTypes[i] == XML) {
					// return the xml located at this xpath location
					Node node = (Node) xpath.evaluate(xpathStrs[i], doc,
							XPathConstants.NODE);
					// System.out.println("eval. node=" + node.getNodeName() +
					// ", " + node.getNodeType() + ", " + node.getNodeValue());

					TransformerFactory transFac = TransformerFactory
							.newInstance();
					Transformer trans = transFac.newTransformer();
					StringWriter writer = new StringWriter();
					Result result = new StreamResult(writer);
					// System.out.println("eval. result=" + result);

					// get the first element child for this node and use it for
					// the result
					if (node != null && node.getChildNodes() != null) {
						NodeList list = node.getChildNodes();
						Node targetNode = null;
						// System.out.println("eval. children:");
						for (int j = 0; j < list.getLength(); j++) {
							Node child = list.item(j);
							// System.out.println("eval.   child=" +
							// child.getNodeName() + ", " + child.getNodeType()
							// + ", " + child.getNodeValue());
							if (child.getNodeType() == Node.ELEMENT_NODE) {
								// System.out.println("eval. keeping node " +
								// child.getNodeName());
								targetNode = child;
								break;
							}
						}

						// Get the namespaces attributes from the document root
						// and add to targetNode
						NamedNodeMap attrMap = doc.getDocumentElement()
								.getAttributes();
						for (int j = 0; j < attrMap.getLength(); j++) {
							Attr attrNode = (Attr) attrMap.item(j);
							if (attrNode.getName().startsWith("xmlns:")) {
								// System.out
								// .println("Adding namespace attribute to target node: "
								// + attrNode.getName()
								// + "="
								// + attrNode.getNodeValue());
								Attr newAttr = doc.createAttribute(attrNode
										.getName());
								newAttr.setNodeValue(attrNode.getValue());
								targetNode.getAttributes()
										.setNamedItem(newAttr);
							}
						}

						Source source = new DOMSource(targetNode);
						trans.transform(source, result);
						writer.close();

						// a hack to remove the <?xml version="1.0"
						// encoding="UTF-8"?> prefix
						String xmlSnippet = removeXMLPrefix(writer.toString());

						results[i] = xmlSnippet;
					}
				} else {
					// return the string located at this xpath location
					// INFO this step tends to take the longest
					results[i] = xpath.evaluate(xpathStrs[i], doc);

					// System.out.println("eval. results[" + i + "]=" +
					// results[i]);
				}
			}
		} catch (Exception ex) {
			LOG.error(ex.getClass().getName() + " occurred evaluating XPath: "
					+ ex.getMessage(), ex);
		}

		// System.out.println("*** evalXPaths took " +
		// (System.currentTimeMillis() - start) + " ms.");

		return results;
	}

	public static String evalXPath(DOMSource source, String xpathStr)
			throws XPathExpressionException {
		Node srcNode = source.getNode();
		Node srcNodeChild = srcNode.getFirstChild();
		Document doc = srcNodeChild.getOwnerDocument();
		String result = xpath.evaluate(xpathStr, doc);
		return result;
	}

	/**
	 * Returns the value of an element named tagName whose parent is named elt.
	 * 
	 * @param elt
	 * @param tagName
	 * @return
	 */
	public static String getTagValue(Element elt, String tagName) {
		Node child = elt.getElementsByTagName(tagName).item(0).getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return null;
	}

	/**
	 * Removes the XML prefix of an xml string.
	 * 
	 * @param xml
	 * @return
	 */
	public static final String removeXMLPrefix(String xml) {
		if (xml.indexOf("?>") != -1) {
			return xml.substring(xml.indexOf("?>") + 2);
		} else {
			return xml;
		}
	}

	@SuppressWarnings({ "rawtypes" })
	public static final String toXml(Object source, Class type) {
		String result;
		StringWriter writer = new StringWriter();
		try {
			JAXBContext context = JAXBContext.newInstance(type);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(source, writer);
			result = writer.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	/**
	 * Adds an element with a given namespace and value to an XML string.
	 * 
	 * @param xml
	 * @param parentNode
	 * @param parentNodeNS
	 * @param elementName
	 * @param elementNamespace
	 * @param elementTextValue
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String addElementToXML(String xml, String parentNode,
			String parentNodeNS, String elementName, String elementNamespace,
			String elementTextValue) throws IllegalArgumentException {
		String[] elementNames = { elementName };
		String[] elementNamespaces = { elementNamespace };
		String[] elementTextValues = { elementTextValue };

		return addElementsToXML(xml, parentNode, parentNodeNS, elementNames,
				elementNamespaces, elementTextValues);
	}

	/**
	 * Adds an element with a given namespace and value to an XML DOM object.
	 * 
	 * @param xml
	 * @param parentNode
	 * @param parentNodeNS
	 * @param elementName
	 * @param elementNamespace
	 * @param elementTextValue
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static void addElementToDOMSource(DOMSource source,
			String parentNode, String parentNodeNS, String elementName,
			String elementNamespace, String elementTextValue)
			throws IllegalArgumentException {
		String[] elementNames = { elementName };
		String[] elementNamespaces = { elementNamespace };
		String[] elementTextValues = { elementTextValue };

		addElementsToDOMSource(source, parentNode, parentNodeNS, elementNames,
				elementNamespaces, elementTextValues);
	}

	/**
	 * Adds elements with given namespaces and values to an XML string.
	 * 
	 * @param xml
	 * @param parentNode
	 * @param parentNodeNS
	 * @param elementName
	 * @param elementNamespace
	 * @param elementTextValue
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String addElementsToXML(String xml, String parentNode,
			String parentNodeNS, String[] elementNames,
			String[] elementNamespaces, String[] elementTextValues)
			throws IllegalArgumentException {
		// long start = System.currentTimeMillis();
		// convert xml to DOMSource
		DOMSource source = unmarshallXMLToDOMSource(xml);

		addElementsToDOMSource(source, parentNode, parentNodeNS, elementNames,
				elementNamespaces, elementTextValues);

		// marshall DOM to a string and return it
		String resultXML = marshallDOMSourceToXML(source);

		// System.out.println("*** addElementsToXML took " +
		// (System.currentTimeMillis() - start) + " ms.");

		return resultXML;
	}

	/**
	 * Adds one or more new elements to a given parent node in a DOMSource. The
	 * new elements will have a text node child with the provided text value.
	 * 
	 * @param source
	 * @param parentNodeName
	 * @param parentNodeNS
	 * @param elementNames
	 * @param elementTextValues
	 * @throws IllegalArgumentException
	 */
	public static void addElementsToDOMSource(DOMSource source,
			String parentNodeName, String parentNodeNS, String[] elementNames,
			String[] elementNamespaces, String[] elementTextValues)
			throws IllegalArgumentException {
		if (source == null) {
			throw new IllegalArgumentException("Input DOMSource is null.");
		}

		Node srcNode = source.getNode();
		Node srcNodeChild = srcNode.getFirstChild();
		Document doc = srcNodeChild.getOwnerDocument();
		NodeList nodeList = doc.getElementsByTagNameNS(parentNodeNS,
				parentNodeName);

		// assume that the first match is the one we care about
		Node parentNode = nodeList.item(0);
		NodeList childNodes = parentNode.getChildNodes();
		for (int newEltIdx = 0; newEltIdx < elementNames.length; newEltIdx++) {
			// check to see if this element/ns combination already exists; if
			// so, don't overwrite; if not, overwrite
			boolean alreadyHasElement = false;
			for (int childEltIdx = 0; childEltIdx < childNodes.getLength(); childEltIdx++) {
				Node child = childNodes.item(childEltIdx);
				if (elementNames[newEltIdx].equalsIgnoreCase(child
						.getLocalName())
						&& elementNamespaces[newEltIdx].equalsIgnoreCase(child
								.getNamespaceURI())) {
					LOG.warn("Input XML already has a "
							+ elementNames[newEltIdx]
							+ " element with namespace "
							+ elementNamespaces[newEltIdx]
							+ ". Keeping existing value ("
							+ child.getTextContent() + ").");
					alreadyHasElement = true;
					break;
				}
			}
			if (!alreadyHasElement) {
				// Ok; not already there; add it now
				Element elt = doc.createElementNS(elementNamespaces[newEltIdx],
						elementNames[newEltIdx]);
				elt.appendChild(doc
						.createTextNode(elementTextValues[newEltIdx]));
				parentNode.appendChild(elt);
			}
		}
	}

	public static String getElementValueFromDOMSource(DOMSource source,
			String node, String nodeNS) throws IllegalArgumentException {
		if (source == null) {
			throw new IllegalArgumentException("Input DOMSource is null.");
		}

		Node srcNode = source.getNode();
		Node srcNodeChild = srcNode.getFirstChild();
		Document doc = srcNodeChild.getOwnerDocument();
		NodeList nodeList = doc.getElementsByTagNameNS(nodeNS, node);

		if (nodeList == null || nodeList.getLength() < 1) {
			return null;
		}

		// assume that the first match is the one we care about
		Node retNode = nodeList.item(0);
		return retNode.getTextContent();
	}

	/**
	 * Converts an XML string to a DOMSource.
	 * 
	 * @param xml
	 * @return
	 */
	public static DOMSource unmarshallXMLToDOMSource(String xml) {
		// long start = System.currentTimeMillis();

		try {
			DOMSource domSource = null;

			synchronized (docBuilder) {
				docBuilder.reset();
				Document doc = docBuilder.parse(new InputSource(
						new StringReader(xml)));
				domSource = new DOMSource(doc);
			}

			// System.out.println("ResponseXML from dom source:\n" +
			// marshallDOMSourceToXML(domSource));

			// System.out.println("*** unmarshallXMLToDOMSource took " +
			// (System.currentTimeMillis() - start) + " ms.");

			return domSource;
		} catch (Exception ex) {
			LOG.error("Exception occurred unmarshalling string to DOMSource: "
					+ ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Retrieves the XML string from a DOMSource.
	 * 
	 * @param source
	 * @return
	 */
	public static String marshallDOMSourceToXML(DOMSource source) {
		// long start = System.currentTimeMillis();

		try {
			String str = null;
			StringWriter writer = new StringWriter();

			synchronized (transformerFactory) {
				transformer.reset();
				transformer.transform(source, new StreamResult(writer));
				writer.flush();
				str = writer.toString();
			}

			// System.out.println("*** marshallDOMSourceToXML took " +
			// (System.currentTimeMillis() - start) + " ms.");

			return str;
		} catch (Exception ex) {
			LOG.error("Exception occurred marshalling DOMSource to string: "
					+ ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Retrieves the XML string from a DOMSource.
	 * 
	 * @param source
	 * @return
	 */
	public static String marshallSourceToXML(Source source) {
		// long start = System.currentTimeMillis();

		try {
			String str = null;
			StringWriter writer = new StringWriter();

			synchronized (transformerFactory) {
				transformer.reset();
				transformer.transform(source, new StreamResult(writer));
				writer.flush();
				str = writer.toString();
			}

			// System.out.println("*** marshallDOMSourceToXML took " +
			// (System.currentTimeMillis() - start) + " ms.");

			return str;
		} catch (Exception ex) {
			// LOG.error("Exception occurred marshalling DOMSource to string: "
			// + ex.getMessage(), ex);
			return null;
		}
	}

}
