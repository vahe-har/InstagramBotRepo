package com.vahe;

import java.util.List;

import com.vahe.utils.Site;


public class LikeParmetes {
	
	private final int likeCount;
	private final int delay;
	private final int maxLikePerHour;
	private final String tagname;
	private final String username;
	private final String password;
	private final List<Site> siteList;


	public LikeParmetes(int likeCount, int delay, int maxLikePerHour, String tagname, String username, String password,List<Site> siteList) {
		this.likeCount = likeCount;
		this.delay = delay;
		this.maxLikePerHour = maxLikePerHour;
		this.tagname = tagname;
		this.username = username;
		this.password = password;
		this.siteList = siteList;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public int getDelay() {
		return delay;
	}

	public int getMaxLikePerHour() {
		return maxLikePerHour;
	}

	public String getTagname() {
		return tagname;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	
	public List<Site> getSiteList() {
		return siteList;
	}
}
