/**
 * 
 */
package org.cms.hios.common.util;

/**
 * @author mpnguyen
 *
 */
public enum ApprovalRequestEnum {

	PENDING(176,"Pending"),
	APPROVED(177, "Approved"),
	DENIED(237,"Denied"),
	AUTO_APPROVED(320,"Auto Approved"),
	CONFIRMED(321,"Confirmed"),
	DISABLED(322,"Disabled");
	
	private int lookupId;
	private String description;	
	
	private ApprovalRequestEnum(int lookupId, String description) {
		this.lookupId = lookupId;		
		this.description = description;
	}



	public int getLookupId() {
		return lookupId;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	
	
	
}
