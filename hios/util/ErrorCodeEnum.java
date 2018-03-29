/**
 * 
 */
package org.cms.hios.common.util;

/**
 * @author chparekh
 *
 */
public enum ErrorCodeEnum {

	BAD_RQUEST(400,"Error", "Request is not valid."),
	DATA_NOT_FOUND(404, "Sucess", "Data Not Found."),
	INTERNAL_SERVER_ERROR(500,"Error", "Unable to process the request.Please try again later.");
	
	private int errorCode;
	private String description;
	private String status;
	
	private ErrorCodeEnum(int errorCode, String status, String description) {
		this.errorCode = errorCode;
		this.status = status;
		this.description = description;
	}

	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	
	
}
