// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 * Author : Rajib Deb
 */
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudmap.java.list_instances.complete]

package aws.example.cloudmap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClientBuilder;
import com.amazonaws.services.servicediscovery.model.ListInstancesRequest;

public class ListInstances {
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

		ListInstancesRequest lreq = new ListInstancesRequest();
		lreq.setServiceId("srv-l7gkxmjapm5givba"); // Replace with service id

		System.out.println(client.listInstances(lreq));
	}

}
// snippet-end:[cloudmap.java.list_instances.complete]