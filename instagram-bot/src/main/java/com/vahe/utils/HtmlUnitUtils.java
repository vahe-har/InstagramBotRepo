package com.vahe.utils;

import com.gargoylesoftware.htmlunit.WebClient;

public class HtmlUnitUtils {

	public static WebClient getWebClient() {
		final WebClient webClient = new WebClient();

		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		webClient.getOptions().setRedirectEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		
		return webClient;
	}

}
