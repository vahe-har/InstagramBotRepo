package com.vahe.follower;

import static com.vahe.utils.Const.COOKIE;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.vahe.LikeParmetes;
import com.vahe.liker.StatigramLiker;
import com.vahe.utils.Const;
import com.vahe.utils.HtmlUnitUtils;

public class IskFollower {

	private static final Logger LOGGER = Logger.getLogger(IskFollower.class);
	
	private  Set<Cookie> cookiesSet;
	private  String sess;
	
	private final LikeParmetes likeParmetes;
	
	
	public IskFollower(LikeParmetes likeParmetes){
		this.likeParmetes = likeParmetes;
		cookiesSet = new SessionScraper(likeParmetes.getUsername(), likeParmetes.getPassword()).getCookes();
		String session = "";
		for (Cookie cookie : cookiesSet) {
			session += cookie.getName() + "=" + cookie.getValue() + "; ";
		}
		sess  = session;
	}
	
	
	public void follow(long userId, FollowAction action){
		
		
		String response = null;
		try {
			response = Request.Post("http://data.ink361.com/v1/users/ig-" + userId + "/relationship")
			        .version(HttpVersion.HTTP_1_1)
			        .useExpectContinue()
			        .addHeader(COOKIE, sess)
			        .bodyForm(Form.form().add("count", "50").add("action", action.getValue()).build())
			        .execute().returnContent().asString();
		} catch (IOException e) {
			LOGGER.error("Exception in likeByPhotoId ", e);
			refreshSession();
		}
		
		LOGGER.info("!!!! Statigram  Respone is    " + response);

	}
	
	
	
	
	
	
	private void refreshSession() {
		cookiesSet = new SessionScraper(likeParmetes.getUsername(), likeParmetes.getPassword()).getCookes();
		String session = "";
		for (Cookie cookie : cookiesSet) {
			session += cookie.getName() + "=" + cookie.getValue() + "; ";
		}
		sess  = session;
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			LOGGER.info(e);
		}
	}






	private static class SessionScraper {

		private static final Logger LOGGER = Logger.getLogger(SessionScraper.class);

//		private static final String SESSIONID = "session_id";
//		private static final String ID = "id";
//		private static final String HMAC_DIGEST = "hmac_digest";
//		private static final String INKCONFIG = "inkconfig";
		
		
		private static final String PAGE = "http://ink361.com/app/#!/photo/ig-490267556234670854_114363";
		private static final String URL = "https://instagram.com/accounts/login/?next=/oauth/authorize/%3Fclient_id%3D2e7067c2b22a4cbdb6b2e0e89cbd6537%26response_type%3Dcode%26redirect_uri%3Dhttp%3A//data.ink361.com/v1/auth%3Finstagram%3D1%26scope%3Dbasic%2Bcomments%2Brelationships%2Blikes#";

	
		private final String username;
		private final String password;

		public SessionScraper(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public Set<Cookie> getCookes() {
			final WebClient webClient = HtmlUnitUtils.getWebClient();
					
			Set<Cookie> cookiesSet = new HashSet<>();
			HtmlPage page = null;
			try {
				page = webClient.getPage(URL);

				List<HtmlForm> forms = page.getForms();

				if (!forms.isEmpty()) {
					HtmlForm form = forms.get(0);
					HtmlInput submitButton = form.getInputByValue(Const.LOG_IN);
					HtmlTextInput usernameInput = form.getInputByName(Const.USERNAME_INP);
					List<HtmlInput> inputsByName = form.getInputsByName(Const.PASSWORD_INP);
					HtmlInput passwordInput = inputsByName.get(0);

					usernameInput.setValueAttribute(username);
					passwordInput.setValueAttribute(password);

					HtmlPage newPage = submitButton.click();
					Page tagPage = webClient.getPage(PAGE);
					 cookiesSet = webClient.getCookieManager().getCookies();
					 cookiesSet = removeCookies(cookiesSet);
					return cookiesSet;
						 
				}
			} catch (FailingHttpStatusCodeException | IOException e) {
				LOGGER.error("Exception in getSeesion() ", e);
			} finally {
				webClient.closeAllWindows();
			}
			
			LOGGER.warn("Could not find session  for Statigram..., Trying agin");
			
			return cookiesSet;
		}
		
		private Set<Cookie> removeCookies(Set<Cookie> cookiesSet) {
			Set<Cookie> newcookiesSet = new HashSet<>();
			for (Cookie cookie : cookiesSet) {
				if(!StringUtils.startsWith(cookie.getName(), "_"))
				{
					newcookiesSet.add(cookie);
				}
					
			}
			return newcookiesSet;
		}
	}
	
	
}
