package com.vahe.liker;

import static com.vahe.utils.Const.*;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.DefaultHttpClient;
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

public class WebstagramLiker implements InstagramLiker {

	private static final String PHPSESSID = "PHPSESSID=";

	private static final String T = "t";

	private static final String PK = "pk";

	private static final Logger LOGGER = Logger.getLogger(WebstagramLiker.class);

	private static final String URL = "http://web.stagram.com/do_like/";

	private static final DefaultHttpClient DEFAULT_HTTP_CLIENT = new DefaultHttpClient();
	
	private  String sessionId;

	private final LikeParmetes likeParmetes;
	
	public WebstagramLiker(LikeParmetes likeParmetes) {
		this.likeParmetes = likeParmetes;
		this.sessionId = new SessionScraper(likeParmetes.getUsername(), likeParmetes.getPassword()).getSession();
	}
	
	private void refreshSession(){
		LOGGER.info("REFERSHING  SESSION   !!!!!!!!!!");
		this.sessionId = new SessionScraper(likeParmetes.getUsername(), likeParmetes.getPassword()).getSession();
	}

	@Override
	public void likeByPhotoId(String photoId) {
 
		String response = "";
		try {
			String genNumber = String.valueOf(randomInt(1000, 9999));

			Request request = Request.Post(URL)
					.version(HttpVersion.HTTP_1_1)
					.addHeader(COOKIE, PHPSESSID + sessionId)
					.addHeader(USER_AGENT, MOZILLA)
					.bodyForm(Form.form().add(PK, photoId).add(T, genNumber)
					.build());

			Executor executor = Executor.newInstance(DEFAULT_HTTP_CLIENT);
			DEFAULT_HTTP_CLIENT.getCookieStore().clear();
			response = executor.execute(request).returnContent().asString();
			

		} catch (IOException e) {
			LOGGER.error("Exception in likeByPhotoId ", e);
			refreshSession();
		}
		LOGGER.info("!!!! Webstagram  Respone is    " + response);
		if(response.contains("log in")){
			refreshSession();
		}
	}
	

	private static int randomInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	private static class SessionScraper {

		private static final Logger LOGGER = Logger.getLogger(SessionScraper.class);

		private static final String TAG_PAGE = "http://web.stagram.com/tag/love/";
		private static final String SESSIONID = "PHPSESSID";

		private final String username;
		private final String password;

		private static final String URL = "https://instagram.com/accounts/login/?next=/oauth/authorize/%3Fclient_id%3D9d836570317f4c18bca0db6d2ac38e29%26redirect_uri%3Dhttp%3A//web.stagram.com/%26response_type%3Dcode%26scope%3Dlikes%2Bcomments%2Brelationships";
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

					Page page2 = submitButton.click();
					Page tagPage = webClient.getPage(TAG_PAGE);
					Cookie cookie = webClient.getCookieManager().getCookie(SESSIONID);
					
					if (cookie != null) {
						LOGGER.info("Webstagram !!!!! " + cookie.getName() + "   " + cookie.getValue() + " !!!!!!!");
						return cookie.getValue();
					}
				}
			} catch (FailingHttpStatusCodeException | IOException e) {
				LOGGER.error("Exception in getSeesion() ", e);
			} finally {
				webClient.closeAllWindows();
			}
			LOGGER.warn("Could not find session  for webstagram..., Trying agin");
			
			return getSession();

		}

	}
}
