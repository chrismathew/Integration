package gov.hhs.cms.base.common.util;

/**
 * Custom interface, which simplifies JAXB API usage.
 */
public interface JaxbHelper {

	/**
	 * Marshal a given object into XML
	 * 
	 * @param instance object to marshal
	 * @param includeDeclaration for jaxb fragmentation
	 * @return xmlRepresentation
	 * @throws Exception
	 */
	public <T> String marshal(T instance, boolean includeDeclaration) throws Exception;

	/**
	 * Unmarshal an xml representation into an object
	 * 
	 * @param xml xml representation of the object
	 * @param clazz Class of the object
	 * @return object
	 * @throws Exception
	 */
    public <T> T unmarshal(String xml, Class<T> clazz) throws Exception;
}
