package com.appbootup.explore.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.appbootup.explore.client.GreetingService;
import com.appbootup.explore.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private static final Logger LOGGER = Logger
			.getLogger(GreetingServiceImpl.class.getName());
	private final String USER_AGENT = "Mozilla/5.0";

	public String greetServer(String input) throws Exception {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	public String sendWebRequestByJSOUP() {
		Document doc = null;
		String targetURL = "https://www.codeschool.com/";
		LOGGER.info("\nSending JSOUP request to URL : " + targetURL);
		String message = "";
		try {
			doc = Jsoup.connect(targetURL).get();
			String htmlContent = doc.body().html();
			message = "Success in WebRequestByJSOUP - " + doc.title();
			LOGGER.info(message);
			// LOGGER.info(htmlContent);
		} catch (IOException e) {
			message = "Error in WebRequestByJSOUP";
			LOGGER.info(message);
		}
		return message;
	}

	// HTTP GET request
	private String sendWebRequestByGET() throws Exception {
		try {
			String url = "https://www.codeschool.com/";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			int responseCode = con.getResponseCode();
			LOGGER.info("\nSending 'GET' request to URL : " + url);
			LOGGER.info("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
		} catch (Exception caught) {
			LOGGER.log(Level.SEVERE, "Failed WebRequestByGET", caught);
			return "Failed WebRequestByGET";
		}
		return "Completed WebRequestByGET";
	}

	// HTTP POST request
	private String sendWebRequestByPOST() {
		displayHttpsCert();
		try {

			String url = "https://www.codeschool.com/";
			URL obj;

			obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add request header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();
			LOGGER.info("\nSending 'POST' request to URL : " + url);
			LOGGER.info("Post parameters : " + urlParameters);
			LOGGER.info("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// print result
			LOGGER.info(response.toString());
		} catch (Exception caught) {
			LOGGER.log(Level.SEVERE, "Failed WebRequestByPOST", caught);
			return "Failed WebRequestByPOST";
		}
		return "Completed WebRequestByPOST";
	}

	private void displayHttpsCert() {
		try {
			String url = "https://www.codeschool.com/";
			URL obj;

			obj = new URL(url);

			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			LOGGER.info("Response Code : " + con.getResponseCode());
			LOGGER.info("Cipher Suite : " + con.getCipherSuite());
			LOGGER.info("\n");

			Certificate[] certs = con.getServerCertificates();
			for (Certificate cert : certs) {
				LOGGER.info("Cert Type : " + cert.getType());
				LOGGER.info("Cert Hash Code : " + cert.hashCode());
				LOGGER.info("Cert Public Key Algorithm : "
						+ cert.getPublicKey().getAlgorithm());
				LOGGER.info("Cert Public Key Format : "
						+ cert.getPublicKey().getFormat());
				LOGGER.info("\n");
			}
		} catch (Exception caught) {
			LOGGER.log(Level.SEVERE, "Failed WebRequestByPOST", caught);
		}
	}

	public String invokedJSOUPWebrequestOnServer(String URL) throws Exception {
		return sendWebRequestByJSOUP();
	}

	public String invokedWebrequestGETOnServer(String URL) throws Exception {
		return sendWebRequestByGET();
	}

	public String invokedWebrequestPOSTOnServer(String URL) throws Exception {
		return sendWebRequestByPOST();
	}
}