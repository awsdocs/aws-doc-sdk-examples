/*
 * Author : Rajib Deb
 */
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

// snippet-sourcedescription:[ListInstances.java demonstrates how to ....]
// snippet-service:[cloudmap]
// snippet-keyword:[java]
// snippet-keyword:[AWS Cloud Map]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-04-18]
// snippet-sourceauthor:[rajib76]
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
		
		ListInstancesRequest lreq = new ListInstancesRequest();
		lreq.setServiceId("srv-l7gkxmjapm5givba");  //Replace with service id
		
		System.out.println(client.listInstances(lreq));
	}

}
//snippet-end:[cloudmap.java.list_instances.complete]