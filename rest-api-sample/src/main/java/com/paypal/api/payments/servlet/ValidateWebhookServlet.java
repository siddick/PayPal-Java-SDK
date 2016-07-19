// #CreateCreditCard Sample
// Using the 'vault' API, you can store a 
// Credit Card securely on PayPal. You can
// use a saved Credit Card to process
// a payment in the future.
// The following code demonstrates how 
// can save a Credit Card on PayPal using 
// the Vault API.
// API used: POST /v1/vault/credit-card
package com.paypal.api.payments.servlet;

import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.Event;
import com.paypal.api.payments.util.ResultPrinter;
import com.paypal.base.Constants;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.paypal.api.payments.util.SampleConstants.*;

public class ValidateWebhookServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger
			.getLogger(ValidateWebhookServlet.class);

	public static final String WebhookId = "4JH86294D6297924G";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	// ##Create
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try{
			// ### Api Context
			// Pass in a `ApiContext` object to authenticate
			// the call and to send a unique request id
			// (that ensures idempotency). The SDK generates
			// a request id if you do not pass one explicitly.
			APIContext apiContext = new APIContext(clientID, clientSecret, mode);
			apiContext.addConfiguration(Constants.PAYPAL_WEBHOOK_ID, WebhookId);

			Boolean result = Event.validateReceivedEvent(apiContext, getHeadersInfo(req), getBody(req));
			
			System.out.println("Result is " + result);
			LOGGER.info("Webhook Validated:  "
					+ result);
			ResultPrinter.addResult(req, resp, "Webhook Validated:  ", CreditCard.getLastRequest(), CreditCard.getLastResponse(), null);
		} catch (PayPalRESTException e) {
			LOGGER.error(e.getMessage());
			ResultPrinter.addResult(req, resp, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
		} catch (InvalidKeyException e) {
			LOGGER.error(e.getMessage());
			ResultPrinter.addResult(req, resp, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e.getMessage());
			ResultPrinter.addResult(req, resp, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
		} catch (SignatureException e) {
			LOGGER.error(e.getMessage());
			ResultPrinter.addResult(req, resp, "Webhook Validated:  ", CreditCard.getLastRequest(), null, e.getMessage());
		}
	}

	/**
	 * Simple helper method to help you extract the headers from HttpServletRequest object.
	 *
	 * @param request {@link HttpServletRequest} request object
	 * @return @{@link Map} of headers.
     */
	private static Map<String, String> getHeadersInfo(HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		@SuppressWarnings("rawtypes")
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}

		return map;
	}

	/**
	 * Simple helper method to fetch request data as a string from @{@link HttpServletRequest} object.
	 *
	 * @param request {@link HttpServletRequest} request object
	 * @return String containing the webhook data
	 * @throws IOException
     */
	private static String getBody(HttpServletRequest request) throws IOException {

	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	}

}
