package com.vahe.liker;

import java.util.List;

public class CompositeLiker implements InstagramLiker {

	
	public static CompositeLiker newInstance(List<InstagramLiker> instagramLikers){
		return new CompositeLiker(instagramLikers);
	}
	
	
	private final List<InstagramLiker> instagramLikers;
	private int count;
	
	private CompositeLiker(List<InstagramLiker> instagramLikers) {
		this.instagramLikers = instagramLikers;
	}

	@Override
	public void likeByPhotoId(String photoId) {
		int number = count % instagramLikers.size();
		InstagramLiker currentLiker = instagramLikers.get(number);
		currentLiker.likeByPhotoId(photoId);
		count++;
	}

}
