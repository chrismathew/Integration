package org.cms.hios.common.util;

public enum OrganizationTypeEnum {
	
	/**
	 * LOOKUP_ID	LOOKUP_CAT_ID	LOOKUP_NAME	LOOKUP_DESCRIPTION	SORT_ORDER
212	38	Company	Company	1
213	38	Group	Group	2
214	38	Issuer	Issuer	3
247	38	Non Insurance Company 	Non Insurance Company 	5
	 */
	COMPANY("Company", "Company"),  
	NON_INSURANCE_COMPANY("Non Insurance Company", "Non Insurance Company"), 
	GROUP("GROUP","Company"), 
	NONFED("Non-Federal Governmental Plans", "Non-Federal Governmental Plans"),
	GRANTEE("Grantee","Grantee");
	
	private String orgType;
	private String displayType;
	 

	private OrganizationTypeEnum(String orgType, String displayType) {
		this.orgType = orgType;
		this.displayType= displayType;
	}
	public String getOrgType(){
		return this.orgType;
	}
	/**
	 * @return the displayType
	 */
	public String getDisplayType() {
		return displayType;
	}
	
	public static OrganizationTypeEnum getOrganizationTypeEnum(String orgType) {
		if(orgType==null) return null;
		for(OrganizationTypeEnum o : OrganizationTypeEnum.values()) {
			if(o.getOrgType().equalsIgnoreCase(orgType)) {
				return o;
			}
		}
		return null;
	}
}
