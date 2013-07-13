package com.vahe.utils;

public enum Site {

	WEBSTAGRAM("webstagram"),STATIGRAM("statigram");
	
	private Site(String name){
		this.name = name;
	}
	private final String name;
	
	
	public String getName(){
		return name;
	}
	
}
