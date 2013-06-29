 package com.vahe;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.vahe.utils.HtmlUnitUtils;

public class CodeScraper {
	private static final String USERNAME = "karine_min";
	private static final String PASSWORD = "karine1234";

	private static final Logger LOGGER = Logger.getLogger(CodeScraper.class);
	private static final String REGEX_CODE = "code=(.+)";
	private static final String PASSWORD_INP = "password";
	private static final String USERNAME_INP = "username";
	private static final String LOG_IN = "Log in";

	public static String getAPICode(String instagramUrl,String username, String password) {
		final WebClient webClient = HtmlUnitUtils.getWebClient();

		try {
			HtmlPage page = webClient.getPage(instagramUrl);


			List<HtmlForm> forms = page.getForms();
			if (!forms.isEmpty()) {
				HtmlForm form = forms.get(0);
				HtmlInput submitButton = form.getInputByValue(LOG_IN);
				HtmlTextInput usernameInput = form.getInputByName(USERNAME_INP);
				List<HtmlInput> inputsByName = form.getInputsByName(PASSWORD_INP);
				HtmlInput passwordInput = inputsByName.get(0);

				usernameInput.setValueAttribute(username);
				passwordInput.setValueAttribute(password);

				HtmlPage newPage = submitButton.click();

				try {// maybe there is a second page, check for it
					List<HtmlForm> autForms = newPage.getForms();
					if (!autForms.isEmpty()) {
						HtmlForm autForm = autForms.get(0);
						HtmlInput inputList = autForm.getInputByValue("Authorize");
						newPage = inputList.click();
					}

				} catch (ElementNotFoundException e) {
					LOGGER.info("There is not second page in authorization process    ",e);
				}
				String apiUrl = newPage.getUrl().toString();
				LOGGER.info(" !!! API url is   " + apiUrl);

				Matcher matcher = Pattern.compile(REGEX_CODE).matcher(apiUrl);
				String code = "";
				if (matcher.find()) {
					code = matcher.group(1);
				} 
				return code;
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			LOGGER.error("Exception in getAPICode()   ", e);
		} finally {
			webClient.closeAllWindows();
		}

		webClient.closeAllWindows();
		return null;
	}
}
