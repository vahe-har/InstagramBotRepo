package com.vahe.follower;

public enum FollowAction {

	
	APPROVE("approve"),
	DENY("deny");

	private final String value;

	private FollowAction(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
