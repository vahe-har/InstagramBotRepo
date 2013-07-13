package com.vahe.follower;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.entity.users.feed.UserFeed;
import org.jinstagram.entity.users.feed.UserFeedData;
import org.jinstagram.exceptions.InstagramException;

import com.vahe.InsClientFactoy;
import com.vahe.LikeParmetes;
import com.vahe.delayer.Delayer;
import com.vahe.delayer.InsagramAction;

public class FollowerClient implements InsagramAction {

	private static final Logger LOGGER = Logger.getLogger(IskFollower.class);

	private final Instagram instagramClient;

	private final IskFollower iskFollower;
	private final LikeParmetes likeParmetes;

	private int totalApproveCount = 0;
	private int totalDenyCount = 0;

	private List<UserFeedData> followingUsers;
	private volatile int index = 0;

	public FollowerClient(LikeParmetes likeParmetes) {
		this.iskFollower = new IskFollower(likeParmetes);
		this.likeParmetes = likeParmetes;
		this.instagramClient = InsClientFactoy.getClient(likeParmetes.getUsername(), likeParmetes.getPassword());
		refreshFollowingUsers();
	}

	public void startFollow() {
		Delayer delayer = new Delayer(this, 50, likeParmetes.getDelay());
		delayer.start();
	}

	@Override
	public boolean action() {
		if (index >= followingUsers.size()) {
			refreshFollowingUsers();
			index = 0;
			if(followingUsers.size() == 0 ){
				try {
					TimeUnit.MINUTES.sleep(20);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}
		UserFeedData userFeedData = followingUsers.get(index);

		long id = userFeedData.getId();
		int userMediaCount = getUserMediaCount(id);
		System.out.println("Count for  " + userFeedData.getUserName() + "    " + userMediaCount);
		if (userMediaCount < 5) {
			iskFollower.follow(id, FollowAction.DENY);
			totalDenyCount++;
			LOGGER.info("Total Deny count is " + totalDenyCount);
		} else {
			iskFollower.follow(id, FollowAction.APPROVE);
			totalApproveCount++;
			LOGGER.info("Total Approve count is " + totalApproveCount);
		}

		index++;
		return true;
	}

	public void refreshFollowingUsers() {
		try {
			UserFeed userRequestedBy = instagramClient.getUserRequestedBy();
			List<UserFeedData> userList = userRequestedBy.getUserList();
			System.out.println(userList.size());
			followingUsers = userList;
		} catch (InstagramException e) {
			LOGGER.error("Exception in refreshFollowingUsers()", e);
			followingUsers = new ArrayList<>();
		}
	}

	public int getUserMediaCount(long id) {
		try {
			UserInfo userInfo = instagramClient.getUserInfo(id);
			int mediaCount = userInfo.getData().getCounts().getMedia();
			System.out.println("Media counts is   " + mediaCount);
			return mediaCount;
		} catch (InstagramException e) {
			LOGGER.error("exception in getUserDetails()  returning -1", e);
			return 1;
		}
	}

}
