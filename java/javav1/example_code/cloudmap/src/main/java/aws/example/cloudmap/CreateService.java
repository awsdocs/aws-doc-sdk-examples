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

// snippet-sourcedescription:[CreateService.java creates a service in the given namespace in AWS Cloud Map]
// snippet-service:[cloudmap]
// snippet-keyword:[java]
// snippet-keyword:[AWS Cloud Map]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-04-19]
// snippet-sourceauthor:[rajib76]
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
		
		CreateServiceRequest crequest = new CreateServiceRequest();
		crequest.setName("example-service-01");
		crequest.setDescription("This is an example service request");
		crequest.setNamespaceId("ns-ldmexc5fqajjnhco");//Replace with the namespaceID		
		System.out.println(client.createService(crequest));
	}

}
//snippet-end:[cloudmap.java.create_service_request.complete]