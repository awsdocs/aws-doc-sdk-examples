// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
	public static void main(String args[]) throws Exception {
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider().getCredentials();
		} catch (Exception e) {
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

		// Use a filter to retrieve the service based on environment and version
		Map<String, String> filtermap = new HashMap<String, String>();
		filtermap.put("Stage", "Dev"); // Stage - key of the custom attribute, Dev - value of the custom attribute
		filtermap.put("Version", "01");// Version - key of the custom attribute, 01 - value of the custom attribute
		direquest.setQueryParameters(filtermap);
		System.out.println(client.discoverInstances(direquest));
	}

}
// snippet-end:[cloudmap.java.lookup_servicewithfilter_request.complete]