package com.dp5.remote;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.util.HashMap;
import java.util.Map;

public class DP5RemoteTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DP5RemoteCore remote = new DP5RemoteCore();
		remote.setHttpClient(HttpClient.newBuilder().version(Version.HTTP_2) // this is the default
				.build());
		
		//Get DP5 version
		String version = remote.getVersion();
		System.out.println("version :"+ version);
		
		//Get DP5 status
		String dp5Status = remote.getDP5Status();
		System.out.println("status :"+ dp5Status);
				
		//Get DP5 Licence
		System.out.println(remote.getDP5Licence());
		
		//Get all DP5 containers 
		System.out.println(remote.getAllDP5Containers());
		
		//Delete a specific container in DP5
		remote.deleteSpecificContainer("cube96sbs2");
		
		//Scan a rack in DP5 for a given container uid
		Map<String,String> params = new HashMap<String, String>();
		params.put("container_uid", "cube96sbs2");
		params.put("raw_image","true");
		remote.scanRackUsingDP5(params);
		System.out.println(remote.scanRackUsingDP5(params));
		
		//Shutdown the dp5 headless
		remote.shutDown();
		
	}

}
