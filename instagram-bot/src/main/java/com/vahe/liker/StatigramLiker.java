package com.vahe.liker;

import static com.vahe.utils.Const.COOKIE;

import java.io.IOException;
import java.util.List;

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
import com.vahe.utils.Const;
import com.vahe.utils.HtmlUnitUtils;

public class StatigramLiker implements InstagramLiker {

	
	private static final String PHOTO_ID = "photo_id";

	private static final String POST_LIKE = "postLike";

	private static final String ACTION = "action";

	private static final String URL = "http://statigr.am/controller_ajax.php/";

	private static final String PARAMSTRING = "action=postLike&photo_id=";

	private static final Logger LOGGER = Logger.getLogger(StatigramLiker.class);

	private  String sessionId;

	private final LikeParmetes likeParmetes;
	
	public StatigramLiker(LikeParmetes likeParmetes) {
		this.likeParmetes = likeParmetes;
		SessionScraper sessionId = new SessionScraper(likeParmetes.getUsername(), likeParmetes.getPassword());
		this.sessionId = sessionId.getSession();
	}
	private void refreshSession(){
		LOGGER.info("REFERSHING  SESSION   !!!!!!!!!!");
		this.sessionId = new SessionScraper(likeParmetes.getUsername(), likeParmetes.getPassword()).getSession();
	}

	@Override
	public void likeByPhotoId(String photoId) {
		String response = null;
		try {
			response = Request.Post(URL)
			        .version(HttpVersion.HTTP_1_1)
			        .useExpectContinue()
			        .addHeader(COOKIE, "STATISESSID=" + sessionId)
			        .bodyForm(Form.form().add(ACTION, POST_LIKE).add(PHOTO_ID, photoId).build())
			        .execute().returnContent().asString();
		} catch (IOException e) {
			LOGGER.error("Exception in likeByPhotoId ", e);
			refreshSession();
		}
		
		LOGGER.info("!!!! Statigram  Respone is    " + response);
		if(StringUtils.isNotBlank(response)){
			LOGGER.info("!!!! Statigram  Respone is    not blunk, require refreshing");
			refreshSession();
		}

	}
	
	
	private static class SessionScraper {

		private static final Logger LOGGER = Logger.getLogger(SessionScraper.class);

		private static final String STATISESSID = "STATISESSID";
		private static final String TAG_PAGE = "http://statigr.am/viewer.php#/tag/dilijan/";
		private static final String URL = "https://instagram.com/oauth/authorize?client_id=d9494686198d4dfeb954979a3e270e5e&redirect_uri=http%3A%2F%2Fstatigr.am&response_type=code&scope=likes+comments+relationships";

	
		private final String username;
		private final String password;

		public SessionScraper(String username, String password) {
			this.username = username;
			this.password = password;
		}

		public String getSession() {
			final WebClient webClient = HtmlUnitUtils.getWebClient();
					

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
					Page tagPage = webClient.getPage(TAG_PAGE);
					Cookie cookie = webClient.getCookieManager().getCookie(STATISESSID);
					
					if (cookie != null) {
						LOGGER.info("Statigram !!!!! " + cookie.getName() + "   " + cookie.getValue() + " !!!!!!!");
						return cookie.getValue();
					}
					 
				}
			} catch (FailingHttpStatusCodeException | IOException e) {
				LOGGER.error("Exception in getSeesion() ", e);
			} finally {
				webClient.closeAllWindows();
			}
			
			LOGGER.warn("Could not find session  for Statigram..., Trying agin");
			
			return getSession();
		}
	}
	
	
}
