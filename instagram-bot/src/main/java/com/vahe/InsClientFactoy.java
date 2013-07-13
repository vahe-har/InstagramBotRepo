package com.vahe;

import org.apache.log4j.Logger;
import org.jinstagram.Instagram;
import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.jinstagram.entity.users.basicinfo.UserInfo;
import org.jinstagram.exceptions.InstagramException;

public class InsClientFactoy {

	private static final String CLIENT_ID = "df5aa5a17b6949eeb76701c1253b9be3";
	private static final String CLIENT_SECRET = "a2270db47c4b4bf1b936c6cf2a4a5852";
	private static final String CALLBACK_URL = "http://rpnews.ru/";
	private static final Token EMPTY_TOKEN = null;
	
	private static final Logger LOGGER = Logger.getLogger(InsClientFactoy.class);
	
	public static Instagram getClient(String username, String password){
		InstagramService service = new InstagramAuthService().apiKey(CLIENT_ID)
				.apiSecret(CLIENT_SECRET).callback(CALLBACK_URL).scope("relationships")
				.build();

		String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);

		// This is using
		// username karine_min
		// password karine12345
//		String verifierCode = CodeScraper.getAPICode(authorizationUrl,
//				likeParmetes.getUsername(), likeParmetes.getPassword());
		String verifierCode = CodeScraper.getAPICode(authorizationUrl, username, password);

		LOGGER.info("Verification code is  " + verifierCode);
		Verifier verifier = new Verifier(verifierCode);
		Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);

		Instagram instagramClient = new Instagram(accessToken);
		// instagram = new Instagram(clientId);
		UserInfo userInfo;
		try {
			userInfo = instagramClient.getCurrentUserInfo();
			LOGGER.info("***** User Info ******");
			LOGGER.info("Username : " + userInfo.getData().getUsername());

		} catch (InstagramException e) {
			LOGGER.error("Connot initialize Instagram Client  ", e);
		}
    
		return instagramClient;
	}
}
