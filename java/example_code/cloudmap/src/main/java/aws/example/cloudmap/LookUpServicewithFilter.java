// Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of
// the License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

// snippet-sourcedescription:[LookUpServiceFilter.java helps to lookup a service with filter from AWS Cloud Map]
// snippet-service:[cloudmap]
// snippet-keyword:[java]
// snippet-keyword:[AWS Cloud Map]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-04-19]
// snippet-sourceauthor:[rajib76]
// snippet-start:[cloudmap.java.lookup_servicewithfilter_request.complete]

package aws.example.cloudmap;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClientBuilder;
import com.amazonaws.services.servicediscovery.model.DiscoverInstancesRequest;

public class LookUpServicewithFilter {
	public static void main(String args[]) throws Exception
	{
		AWSCredentials credentials = null;
		try
		{
			credentials = new ProfileCredentialsProvider().getCredentials();
		}catch(Exception e)
		{
			throw new AmazonClientException("Cannot Load credentials");
		}
		
		AWSServiceDiscovery client = AWSServiceDiscoveryClientBuilder
									.standard()
									.withCredentials(new AWSStaticCredentialsProvider(credentials))
									.withRegion("us-east-1")
									.build();
		
		DiscoverInstancesRequest direquest = new DiscoverInstancesRequest();
		direquest.setNamespaceName("my-apps");
		direquest.setServiceName("frontend");
		
		//Use a filter to retrieve the service based on environment and version
		Map<String,String>  filtermap = new HashMap<String,String>();
		filtermap.put("Stage", "Dev"); //Stage - key of the custom attribute, Dev - value of the custom attribute
		filtermap.put("Version", "01");//Version - key of the custom attribute, 01 - value of the custom attribute
		direquest.setQueryParameters(filtermap);
		System.out.println(client.discoverInstances(direquest));
	}

}
//snippet-end:[cloudmap.java.lookup_servicewithfilter_request.complete]