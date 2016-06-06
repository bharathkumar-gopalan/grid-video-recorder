
 /*---------------------------------------------------------------------------------------------------------
 * Copyright 2016 Nirvagi project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * \*-------------------------------------------------------------------------------------------------------------------*/
 
package io.nirvagi.utils.node.helper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpGetHelper {
	private static final String ERR_MESSAGE = "The url %s passed is malformed";
	private static final String HTTP_GET_FAILED_MESSAGE = "The http get for the url %s failed, The client returned the code of %s with message %s";
	
	private final CloseableHttpClient client;
	private final HttpGet httpGet;
	
	private ResponseHandler<String> createHttpResponseHandler() {
		return new ResponseHandler<String>() {

			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					throw new ClientProtocolException(String.format(
							HTTP_GET_FAILED_MESSAGE, httpGet.getURI()
									.toString(), statusCode, response
									.getStatusLine().getReasonPhrase()));
				}
				return "SUCCESS";
			}
		};

	}
	
	public HttpGetHelper(final String url){
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format(ERR_MESSAGE, url));
		}
		client = HttpClients.createDefault();
		httpGet = new HttpGet(url);
	}
	
	
	
	
	public void execute(){
		try {
			client.execute(httpGet, createHttpResponseHandler());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}finally{
			try {
				this.client.close();
			} catch (IOException e) {
			}
		}
	}
	
	

}
