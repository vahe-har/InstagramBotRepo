package com.vahe.utils;

public enum Sites {

	WEBSTAGRAM("webstagram"),STATIGRAM("statigram");
	
	private Sites(String name){
		this.name = name;
	}
	private final String name;
	
	
	public String getName(){
		return name;
	}
	
}
