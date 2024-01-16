// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[cloudmap.java.create_service_request.complete]

package aws.example.cloudmap;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.servicediscovery.AWSServiceDiscovery;
import com.amazonaws.services.servicediscovery.AWSServiceDiscoveryClientBuilder;
import com.amazonaws.services.servicediscovery.model.CreateServiceRequest;

public class CreateService {
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

		CreateServiceRequest crequest = new CreateServiceRequest();
		crequest.setName("example-service-01");
		crequest.setDescription("This is an example service request");
		crequest.setNamespaceId("ns-ldmexc5fqajjnhco");// Replace with the namespaceID
		System.out.println(client.createService(crequest));
	}

}
// snippet-end:[cloudmap.java.create_service_request.complete]