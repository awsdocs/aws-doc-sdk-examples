//snippet-sourcedescription:[DeleteApp.java demonstrates how to delete an application in the Pinpoint dashboard.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mobiletargeting]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-01-15]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.pinpoint;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.DeleteAppRequest;

public class DeleteApp {

	public static void main(String args[]) {
		final String USAGE = "\n" +
	            "DeleteApp - delete an application in the pinpoint dashboard\n\n" +
	            "Usage: DeleteApp <appID>\n\n" +
	            "Where:\n" +
	            "  appID - the application ID of the application to delete.\n\n";

	    if (args.length < 1) {
	        System.out.println(USAGE);
	        System.exit(1);
	    }

	    String appID = args[0];

	    System.out.println("Deleting application: " + appID);

		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

		DeleteAppRequest deleteRequest = new DeleteAppRequest()
				.withApplicationId(appID);

		pinpoint.deleteApp(deleteRequest);

	}
}
