package gov.hhs.cms.base.common.util;


/**
 * 
 * @author aabbasi
 *
 */
public class SamlXMLStringStripUtil {
	
	protected static final String SECURITY_HEADER_DELIMITER = ":securityHeader>";

	/**
	 * This method strips the saml token out of Request/Response SIM Object XML. The Saml is stored in 
	 * security Header VO and is in form:
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
     * <ns3:getSystemUserByUserLoginResponse xmlns:ns2="http://vo.base.cms.hhs.gov" xmlns:ns3="http://sim.ffe.cms.hhs.gov"
     *  xmlns:ns4="http://vo.ffe.cms.hhs.gov">
	 * 
	 * <ns2:securityHeader>
	 * <ns2:ffeUserPrincipalBase64Encoded>99999999999  very large item with SAML TOKEN 9999999999</ns2:ffeUserPrincipalBase64Encoded>
	 * <ns2:ffeUserPrincipalJsonFormat>99999999999999  very large item with SAML TOKEN 9999999999</ns2:ffeUserPrincipalJsonFormat>
     * </ns2:securityHeader>
	 * 
	 * @param s
	 * @return
	 */
	public static String stripSamlFromXML(final String s)  {
		
		if (!UtilFunctions.isEmptyStr(s)) {
			StringBuilder xmlWithoutSaml = new StringBuilder();
			if (!s.contains(SECURITY_HEADER_DELIMITER)) {
				//no security header, return string intact
				return s;
				
			} else {
				String[] strArraySplitOnsecurityHeader = s.split(SECURITY_HEADER_DELIMITER);
				if (strArraySplitOnsecurityHeader != null) {
					for (int j=0; j< strArraySplitOnsecurityHeader.length; j++) {
						String splitPiece = strArraySplitOnsecurityHeader[j];
						if (splitPiece == null) {
							continue;
						}
						if (j == 0) {
							//take out: <ns2 (start of securityHeader tag)
							int endIdx = splitPiece.lastIndexOf("<");
							if (endIdx > -1) {
								int endOffset = splitPiece.lastIndexOf("\r\n");
								if (endOffset < 0) {
									splitPiece = splitPiece.substring(0, endIdx);
								} else {
									splitPiece = splitPiece.substring(0, endOffset);
								}
							}
						}
						//assumption: If we've securityHeader then we"ll always have the 'ffeUserPrincipalBase64Encoded' tag/element
						if (splitPiece.contains(":ffeUserPrincipalBase64Encoded>") ||
							splitPiece.contains(":ffeUserPrincipalJsonFormat>") )	{
							//encountered securityHeader, skip it
							continue;
						}
						//now write
						xmlWithoutSaml.append(splitPiece);
						 
					} //endfor
					if (xmlWithoutSaml.length() > 0) {
						return (xmlWithoutSaml.toString());
					}
				} //endIf (split found)
				
			} //endIf (securityHeader exists)
			
		} //endIf (s != null)
		return s;
		
	}	
	
}
