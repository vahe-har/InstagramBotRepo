package com.vahe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.exceptions.InstagramException;

import com.vahe.liker.CompositeLiker;
import com.vahe.liker.InstagramLiker;
import com.vahe.liker.StatigramLiker;
import com.vahe.liker.WebstagramLiker;
import com.vahe.utils.Sites;

public class InstagramClient {

	private static final int ONE_HOUR = 3_600_000;
	private static final Logger LOGGER = Logger.getLogger(InstagramClient.class);
	private static final Token EMPTY_TOKEN = null;

	private static final String clientId = "df5aa5a17b6949eeb76701c1253b9be3";
	private static final String clientSecret = "a2270db47c4b4bf1b936c6cf2a4a5852";
	private static final String callbackUrl = "http://rpnews.ru/";

	// instagram api object for only getting data
	private Instagram instagram;
//	private String maxTagId = "";
	private long pageNumber = 0;
//	private boolean isImagesOver = false;
	private long totalLikeCount = 0;
	private long likeInHour = 0;
	private Calendar startTime;
	private final InstagramLiker instagramLiker;
	
	private final LikeParmetes likeParmetes;

	public InstagramClient(LikeParmetes likeParmetes) {
		this.likeParmetes = likeParmetes;
		List<InstagramLiker> instagramLikers = new ArrayList<>();
//		instagramLikers.add(new StatigramLiker(likeParmetes.getUsername(), likeParmetes.getPassword()));
//		instagramLikers.add(new WebstagramLiker(likeParmetes.getUsername(), likeParmetes.getPassword()));
		if(this.likeParmetes.getSiteList().contains(Sites.WEBSTAGRAM)){
			instagramLikers.add(new WebstagramLiker(likeParmetes));
		}
		if(this.likeParmetes.getSiteList().contains(Sites.STATIGRAM)){
			instagramLikers.add(new StatigramLiker(likeParmetes));
		}
		this.instagramLiker = CompositeLiker.newInstance(instagramLikers); 
		initInstagram();
	}

	private void initInstagram() {
		InstagramService service = new InstagramAuthService()
									.apiKey(clientId)
									.apiSecret(clientSecret)
									.callback(callbackUrl)
									.scope("basic")
									.build();

		String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);

		// This is using
//		 username karine_min
//		 password karine12345
		String verifierCode = CodeScraper.getAPICode(authorizationUrl,likeParmetes.getUsername(), likeParmetes.getPassword());

		LOGGER.info("Verification code is  " + verifierCode);
		Verifier verifier = new Verifier(verifierCode);
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);

		instagram = new Instagram(accessToken);
//		instagram = new Instagram(clientId);

		UserInfo userInfo;
		try {
			userInfo = instagram.getCurrentUserInfo();
			LOGGER.info("***** User Info ******");
			LOGGER.info("Username : " + userInfo.getData().getUsername());

		} catch (InstagramException e) {
			LOGGER.error("Connot initialize Instagram Client  ", e);
		}

	}

	public void likeImagesInTag(final String tagName) {
		startTime = Calendar.getInstance();
		LOGGER.info("!!!!!!!!!   Start likeing   !!!!!!!!!!");
		
//		while (!isImagesOver) {
			while (true) {
				try {
					likeOnePage(tagName);
				} catch (Exception e) {
					LOGGER.error("General Exception Handler  ", e);
				}
			}
	}

	private void incrementLikeCount() {
		totalLikeCount++;
		likeInHour++;
	}

	private void likeOnePage(String tagName) {
		LOGGER.info("Page Number is  " + pageNumber);

		List<MediaFeedData> allElementInTag = getRecentElements(tagName);
		for (MediaFeedData mediaFeedData : allElementInTag) {
			int count = mediaFeedData.getLikes().getCount();
			if (count < likeParmetes.getLikeCount()) {
				String photoId = mediaFeedData.getId();
				try {
					checkForTime();
					instagramLiker.likeByPhotoId(photoId);
					incrementLikeCount();
					final String msg = "This image was liked     " + mediaFeedData.getLink() + "      " + photoId  + "\n Total(" + totalLikeCount + ")\n";
					
					
					mediaFeedData.getCreatedTime();
					LOGGER.info(msg);

					TimeUnit.SECONDS.sleep(likeParmetes.getDelay());
				} catch (InterruptedException e) {
					LOGGER.error("Exception in likeOnePage()   ", e);
				}
			}
		}
	}

	private void checkForTime() {
		Calendar currentTime = Calendar.getInstance();
		long delta = currentTime.getTimeInMillis() - startTime.getTimeInMillis();
		if (delta < ONE_HOUR && likeInHour >= likeParmetes.getMaxLikePerHour()) {
			try {
				long w = (ONE_HOUR - delta + 10_000) / (1000 * 60);
				LOGGER.info("Wainting  " + w + " min .............");
				TimeUnit.MILLISECONDS.sleep(ONE_HOUR - delta + 10_000);
			} catch (InterruptedException e) {
				LOGGER.error("Exception in checkForTime()   ", e);
			}
		}
		if (delta > ONE_HOUR) {
			startTime = Calendar.getInstance();
			likeInHour = 0;
		}

	}

	
	private List<MediaFeedData> getRecentElements(String tagName) {
		try {
			TimeUnit.MILLISECONDS.sleep(500);
			List<MediaFeedData> data = new ArrayList<>();
			try {
				LOGGER.info("Before getting recent media");
				TagMediaFeed recentMediaTags = instagram.getRecentMediaTags(tagName);
				LOGGER.info("After getting recent media");
				data = recentMediaTags.getData();
				pageNumber++;
			} catch (InstagramException e) {
				LOGGER.error("Exception in getAllElementInTag(tagName)  ", e);
					TimeUnit.SECONDS.sleep(3);
			}
			List<MediaFeedData> singleList = new ArrayList<>();
			if (!data.isEmpty()) {
				singleList.add(data.get(0));
			}
			return singleList;
		} catch (RuntimeException e) {
			LOGGER.error("Runtime Exception in getRecentElements", e);
			return new ArrayList<>();
		} catch (Exception e1) {
			LOGGER.error("Exception in getRecentElements ", e1);
			return new ArrayList<>();
		}
		
	}
	/*
	private List<MediaFeedData> getAllElementInTag(String tagName, String min, String max) {
		List<MediaFeedData> mediaFeeds = new ArrayList<>();
		if (isImagesOver) {
			return mediaFeeds;
		}
		try {
			TagMediaFeed mediaFeed;
			if (min == null && max == null) {
				mediaFeed = instagram.getRecentMediaTags(tagName);
			} else {
				mediaFeed = instagram.getRecentMediaTags(tagName, min, max);
			}
			Pagination pagination = mediaFeed.getPagination();
			String nextMaxTagId = pagination.getNextMaxTagId();

			if (nextMaxTagId == null || maxTagId.equals(nextMaxTagId)) {// there aren't new images
				isImagesOver = true;
			} else {
				maxTagId = nextMaxTagId;
				pageNumber++;
			}

			mediaFeeds = mediaFeed.getData();
		} catch (InstagramException e) {
			LOGGER.error("Exception in getAllElementInTag(tagName, min, max)  ", e);
		}
		return mediaFeeds;
	}
	*/
}