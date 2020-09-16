package com.dp5.remote;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

/**
 * This code details an overview for the Remote API client side calls. Please refer to the swagger document for DP5 remote API
 * to get the available urls and the correct object types
 * The swagger document is on  http://<host>:<port>/swagger-ui/index.html
 *
 */

public class DP5RemoteCore {

	private String host;
	private int port;
	private HttpClient httpClient;
	private int getParamTimeout = 10;
	private int postActionTimeout = 10;
	private int putActionTimeout = 10;
	private int deleteActionTimeout = 10;
	private String REMOTE_STUB = "/dp5/remote/v1";
	private HttpRequest request;


	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public HttpClient getHttpClient() {
		return this.httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void setGetParamTimeout(int getParamTimeout) {
		this.getParamTimeout = getParamTimeout;
	}

	public void setPostActionTimeout(int postActionTimeout) {
		this.postActionTimeout = postActionTimeout;
	}


	//Initialise the host and port which by default in DP5 is localhost and 8777
	public DP5RemoteCore() {
		// TODO Auto-generated constructor stub
		this.host = "localhost";
		this.port = 8777;
	}

	/**
	 * This is the public constructor, this does not connect and assumes that the server is on the localhost and listening on port 8777
	 * Note: this expects the DP5 service to be running if not then to run DP5 service excecute, C:\Program Files\Ziath\DP5\resources\dp5-server\dp5-headless.exe
	 * Note: this is assuming that you are on English language windows and using a default install location
	 * @param host
	 * @param port
	 */
	public DP5RemoteCore(String host, int port) {
		this.host = host;
		this.port = port;
	}

	private String urlStub()
	{
		return String.format("http://%s:%s"+REMOTE_STUB, host,port);
	}


	private String getParameterAppend(Map<String,String> parameters) {
		String appendQuery ="";

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			appendQuery = appendQuery + entry.getKey() +"=" + entry.getValue() +"&";
		}
		return appendQuery;
	}

	/**
	 * Sending a GET request
	 * @param action
	 * @param urlStub
	 * @param parameters
	 * @return
	 * @throws URISyntaxException
	 */
	private HttpRequest getGetParamRequest(String action, String urlStub, Map<String,String> parameters) throws URISyntaxException 
	{
		URI uri = URI.create(urlStub + action);

		if(parameters != null) {
			String queryParam = getParameterAppend(parameters);
			uri= new URI(uri.getScheme(), uri.getAuthority(),
					uri.getPath(), queryParam, uri.getFragment());

		}

		request = HttpRequest.newBuilder().uri(uri)
				.timeout(Duration.ofSeconds(getParamTimeout))
				.GET().build();		
		return request;
	}


	/**
	 * Sending a POST request
	 * @param action
	 * @param urlStub
	 * @param parameters
	 * @return
	 * @throws URISyntaxException
	 */
	private HttpRequest getPostParamRequest(String action, String urlStub, Map<String,String> parameters) throws URISyntaxException 
	{
		URI uri = URI.create(urlStub + action);
		if(parameters != null) {
			String queryParam = getParameterAppend(parameters);
			uri= new URI(uri.getScheme(), uri.getAuthority(),
					uri.getPath(), queryParam, uri.getFragment());
		}
		request = HttpRequest.newBuilder().uri(uri)
				.timeout(Duration.ofSeconds(postActionTimeout))
				.POST(BodyPublishers.ofString("")).build();		
		return request;
	}

    /**
     * Sending a PUT request
     * @param action
     * @param urlStub
     * @return
     * @throws URISyntaxException
     */
	private HttpRequest getPutParamRequest(String action, String urlStub) throws URISyntaxException 
	{
		URI uri = URI.create(urlStub + action);
		request = HttpRequest.newBuilder().uri(uri)
				.timeout(Duration.ofSeconds(putActionTimeout))
				.PUT(BodyPublishers.ofString("")).build();		
		return request;
	}

    /**
     * Sending a DELETE request
     * @param action
     * @param urlStub
     * @return
     * @throws URISyntaxException
     */
	private HttpRequest getDeletetParamRequest(String action, String urlStub) throws URISyntaxException 
	{
		URI uri = URI.create(urlStub + action);
		request = HttpRequest.newBuilder().uri(uri)
				.timeout(Duration.ofSeconds(postActionTimeout))
				.DELETE().build();		
		return request;
	}

	private String responseToString(String keyName, HttpResponse<String> response) {
		JsonObject rdr = Json.createReader(new StringReader(response.body())).readObject();
		return rdr.getString(keyName);
	}

	/**
	 * Get DP5 version number
	 * @return
	 */
	public String getVersion() 	{
		HttpResponse<String> response=null;
		try {
			response = this.getHttpClient().send(getGetParamRequest("/system/version", urlStub(),null),
					BodyHandlers.ofString());
		} catch (IOException | InterruptedException | URISyntaxException e) {
			//catch exception with some logs
		}
		if (response.statusCode() != 200) {
			//catch the error of incorrect output
		}
		return responseToString("version", response);
	}

	/**
	 * Get the DP5 status
	 * @return
	 */
	public String getDP5Status() 	{
		HttpResponse<String> response=null;
		try {
			response = this.getHttpClient().send(getGetParamRequest("/system/status", urlStub(),null),
					BodyHandlers.ofString());
		} catch (IOException | InterruptedException | URISyntaxException e) {
			//catch exception with some logs
		}
		if (response.statusCode() != 200) {
			//catch the error of incorrect output
		}
		return responseToString("status", response);
	}

	/**
	 * Returns the licence as a JSON object, it is recommended to create a class to represent the licence when used in production
	 * @return
	 */
	public JsonObject getDP5Licence() {
		HttpResponse<String> response=null;
		try {
			response = this.getHttpClient().send(getGetParamRequest("/licence", urlStub(),null),
					BodyHandlers.ofString());
		} catch (IOException | InterruptedException | URISyntaxException e) {
			//catch exception with some logs
		}
		if (response.statusCode() != 200) {
			//catch the error of incorrect output
		}
		return Json.createReader(new StringReader(response.body())).readObject();
	}

	/**
	 * Returns the scan result as a JSON object, it is recommended to create a class to represent the scan result when used in production
	 * The swagger documentation details out the ScanResult in the schemas section.
	 * @return
	 */
	public JsonObject scanRackUsingDP5(Map<String,String> parameters) {
		HttpResponse<String> response=null;
		try {
			response = this.getHttpClient().send(getPostParamRequest("/scan", urlStub(),parameters),
					BodyHandlers.ofString());
		} catch (IOException | InterruptedException | URISyntaxException e) {
			//catch exception with some logs
		}
		if (response.statusCode() != 200) {
			//catch the error of incorrect output
		}
		return Json.createReader(new StringReader(response.body())).readObject();
	}


	/**
	 * Returns the list of container as a JSON object, it is recommended to create a class to represent the container when used in production
	 * @return
	 */
	public JsonArray getAllDP5Containers() {
		HttpResponse<String> response=null;
		try {
			response = this.getHttpClient().send(getGetParamRequest("/containers", urlStub(),null),
					BodyHandlers.ofString());
		} catch (IOException | InterruptedException | URISyntaxException e) {
			//catch exception with some logs
		}
		if (response.statusCode() != 200) {
			//catch the error of incorrect output
		}
		return Json.createReader(new StringReader(response.body())).readArray();
	}
	
	public void deleteSpecificContainer(String parameter) {
		HttpResponse<String> response=null;
		try {
			response = this.getHttpClient().send(getDeletetParamRequest("/containers/"+parameter, urlStub()),
					BodyHandlers.ofString());
		} catch (IOException | InterruptedException | URISyntaxException e) {
			//catch exception with some logs
		}
		if (response.statusCode() != 200) {
			//catch the error of incorrect output
		}
	}

	/**
	 * Shutdown the DP5 headless 
	 */
	public void shutDown() {
		HttpResponse<String> response=null;
		try {
			response = this.getHttpClient().send(getPutParamRequest("/system/shutdown", urlStub()),
					BodyHandlers.ofString());
		} catch (IOException | InterruptedException | URISyntaxException e) {
			//catch exception with some logs
		}
		if (response.statusCode() != 200) {
			//catch the error of incorrect output
		}
	}
}