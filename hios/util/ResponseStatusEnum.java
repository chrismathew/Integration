package org.cms.hios.common.util;

public enum ResponseStatusEnum {
	SUCCESS("SUCCESS"),
	FAILURE("FAILURE"),
	SUCCESS_WITH_ERROR("SUCCESS_WITH_ERROR");

	private String status;

	private ResponseStatusEnum(String status) {
		this.status = status;
	}
	public String getStatus(){
		return this.status;
	}
	
}
