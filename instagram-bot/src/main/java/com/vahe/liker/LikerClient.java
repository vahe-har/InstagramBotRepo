package com.vahe.liker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.tags.TagMediaFeed;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.feed.MediaFeedData;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import com.vahe.InsClientFactoy;
import com.vahe.LikeParmetes;
import com.vahe.delayer.Delayer;
import com.vahe.delayer.InsagramAction;
import com.vahe.follower.FollowAction;
import com.vahe.follower.IskFollower;
import com.vahe.utils.Site;

public class LikerClient implements InsagramAction {

	private static final Logger LOGGER = Logger.getLogger(LikerClient.class);

	private Instagram instagramClient;
	private long pageNumber = 0;
	private long totalLikeCount = 0;
	private final InstagramLiker instagramLiker;

	private final LikeParmetes likeParmetes;
	private final IskFollower iskFollower;
	
	
	public LikerClient(LikeParmetes likeParmetes) {
		this.likeParmetes = likeParmetes;
		List<InstagramLiker> instagramLikers = new ArrayList<>();
		if (this.likeParmetes.getSiteList().contains(Site.WEBSTAGRAM)) {
			instagramLikers.add(new WebstagramLiker(likeParmetes));
		}
		if (this.likeParmetes.getSiteList().contains(Site.STATIGRAM)) {
			instagramLikers.add(new StatigramLiker(likeParmetes));
		}
		this.instagramLiker = CompositeLiker.newInstance(instagramLikers);
		this.iskFollower = new IskFollower(likeParmetes);
		this.instagramClient = InsClientFactoy.getClient("vahe_har2", "vahe12345");
	}

	public void likeImagesInTag() {
//		startTime = Calendar.getInstance();
		LOGGER.info("!!!!!!!!!   Start likeing   !!!!!!!!!!");
	
//		ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
//		newSingleThreadExecutor.execute(new Runnable() {
//			
//			@Override
//			public void run() {
//				getFollowRequests();
//			}
//		});
		Delayer delayer = new Delayer(this, likeParmetes.getMaxLikePerHour(),likeParmetes.getDelay());
		delayer.start();
//		while (true) {
//			try {
//				likeOnePhoto();
//			} catch (Exception e) {
//				LOGGER.error("General Exception Handler  ", e);
//			}
//		}
	}



	private void incrementLikeCount() {
		totalLikeCount++;
//		likeInHour++;
	}

	@Override
	public boolean action() {
		LOGGER.info("Page Number is  " + pageNumber);

		List<MediaFeedData> allElementInTag = getRecentElements(likeParmetes.getTagname());
		for (MediaFeedData mediaFeedData : allElementInTag) {
			int count = mediaFeedData.getLikes().getCount();
			if (count < likeParmetes.getLikeCount()) {
				String photoId = mediaFeedData.getId();
				try {
//					checkForTime();
					instagramLiker.likeByPhotoId(photoId);
					incrementLikeCount();
					final String msg = "This image was liked     "
							+ mediaFeedData.getLink() + "      " + photoId
							+ "\n Total(" + totalLikeCount + ")\n";

					LOGGER.info(msg);
				} catch (Exception e) {
					LOGGER.error("Exception in likeOnePage()   ", e);
				}
			}
		}
		return true;
	}

//	private void checkForTime() {
//		Calendar currentTime = Calendar.getInstance();
//		long delta = currentTime.getTimeInMillis()
//				- startTime.getTimeInMillis();
//		if (delta < ONE_HOUR && likeInHour >= likeParmetes.getMaxLikePerHour()) {
//			try {
//				long w = (ONE_HOUR - delta + 10_000) / (1000 * 60);
//				LOGGER.info("Wainting  " + w + " min .............");
//				TimeUnit.MILLISECONDS.sleep(ONE_HOUR - delta + 10_000);
//			} catch (InterruptedException e) {
//				LOGGER.error("Exception in checkForTime()   ", e);
//			}
//		}
//		if (delta > ONE_HOUR) {
//			startTime = Calendar.getInstance();
//			likeInHour = 0;
//		}
//
//	}

	private List<MediaFeedData> getRecentElements(String tagName) {
		try {
			TimeUnit.MILLISECONDS.sleep(500);
			List<MediaFeedData> data = new ArrayList<>();
			try {
				LOGGER.info("Before getting recent media");
				TagMediaFeed recentMediaTags = instagramClient
						.getRecentMediaTags(tagName);
				LOGGER.info("After getting recent media");
				data = recentMediaTags.getData();
				pageNumber++;
			} catch (InstagramException e) {
				LOGGER.error("Exception in getRecentElements(tagName)  ", e);
				TimeUnit.SECONDS.sleep(3);
//				LOGGER.info("Refreshing Acces token for instagram Client");
//				initInstagramCrient();
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

	public void getFollowRequests() {
		try {
			UserFeed userRequestedBy = instagramClient.getUserRequestedBy();
			List<UserFeedData> userList = userRequestedBy.getUserList();
			System.out.println(userList.size());
			for (UserFeedData userFeedData : userList) {
				long id = userFeedData.getId();
				int userMediaCount = getUserMediaCount(id);
				System.out.println("Count for  " +userFeedData.getUserName() + "    " + userMediaCount);
				if(userMediaCount < 5){
					iskFollower.follow(id, FollowAction.DENY);
				}else{
					iskFollower.follow(id, FollowAction.APPROVE);
				}
			}
		} catch (InstagramException e) {
			e.printStackTrace();
		}
	}
	
	public int getUserMediaCount(long id){
		try {
			UserInfo userInfo = instagramClient.getUserInfo(id);
			int mediaCount = userInfo.getData().getCounts().getMedia();
			System.out.println("Media counts is   "  + mediaCount);
			return mediaCount;
		} catch (InstagramException e) {
			LOGGER.error("exception in getUserDetails()  returning -1", e);
			return 1; 
		}
	}
}