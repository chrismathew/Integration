package org.cms.hios.common.util;

public enum EmailRequestEnum {
	REQUEST_TYPE_USER_ACCOUNT("USER_CREATION"),
	REQUEST_TYPE_USER_ROLE("ROLE_CREATION"),
	REQUEST_TYPE_ORG_ROLE("ORG_REQ_ROLE_CREATION");
	

	private String emailRequest;

	private EmailRequestEnum(String emailRequest) {
		this.emailRequest = emailRequest;
	}

	public String getEmailRequest() {
		return emailRequest;
	}

}
