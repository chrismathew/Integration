package com.twc.eis.lib.xml;

public class Attribute {

	private String name;	
	private String value;
	
	private boolean qbeMatch = true;

	public Attribute() {
	}

	public Attribute(String name, String value) {

		setName(name);
		setValue(value);
		
	}

	public final void setName(String name) {
		this.name = name;
	}
	
	
	public final void setValue(String value) {
		this.value = value;
	}

	public final String getName() {
		return name;		
	}

	public final String getValue() {
		return value;
	}

	public String toString() {
		
		return getName() + "=\"" + getValue() + "\"";
	}

	public final boolean isQbeMatch() {
		return qbeMatch;
	}

	public final void setQbeMatch(boolean qbeMatch) {
		this.qbeMatch = qbeMatch;
	}

}