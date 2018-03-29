package org.cms.hios.common.util;

public enum ApplicationEnum {
	HIOS("HIOS"),
	RBIS("RBIS"),
	CCC("CCC"),
	URR("URR"),
	QHP("QHP"),
	QBSA("QBSA"),
	QRM("QRM"),
	STEV("STEV"),
	HIOS_PORTAL("HSPORT"),
	FINMGT("FINMGT"),
	QHPAGRM("QHPAGRM"), //QHP Issuer Agreement
	QHPPP("QHPPP"), //QHP Plan Preview
	CMS("CMS"), //CMS Certification
	PAYEE_MGT("FFEPYM"),//Payee Management Module
	ERE("ERE"),//External Review Election ; 
	NONFED("NONFED"),
	ASISTR("ASISTR"),
	ESM("ESM"),//EDGE Server Management
	DCMSDC("DCMSDC"),
	MQM("MQM");
	private String applicationName;

	private ApplicationEnum(String applicationName) {
		this.applicationName = applicationName;
	}
	public String getApplicationName(){
		return this.applicationName;
	}
	
}
