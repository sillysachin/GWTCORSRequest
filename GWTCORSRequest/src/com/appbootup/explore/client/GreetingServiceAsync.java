package com.appbootup.explore.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void invokedJSOUPWebrequestOnServer(String URL,
			AsyncCallback<String> callback);

	void invokedWebrequestGETOnServer(String URL, AsyncCallback<String> callback);

	void invokedWebrequestPOSTOnServer(String URL,
			AsyncCallback<String> callback);
}
