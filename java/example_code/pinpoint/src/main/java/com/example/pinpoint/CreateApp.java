 
//snippet-sourcedescription:[CreateApp.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[mobiletargeting]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.CreateAppRequest;
import com.amazonaws.services.pinpoint.model.CreateAppResult;
import com.amazonaws.services.pinpoint.model.CreateApplicationRequest;
import com.amazonaws.regions.Regions;

public class CreateApp {

	public static void main(String[] args) {
        final String USAGE = "\n" +
                "CreateApp - create an application in pinpoint dashboard\n\n" +
                "Usage: CreateApp <appName>\n\n" +
                "Where:\n" +
                "  appName - the name of the application to create.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String appName = args[0];
        
        System.out.println("Creating an application with name: " + appName);
            
		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		
		CreateApplicationRequest appRequest = new CreateApplicationRequest()
				.withName(appName);
		
		CreateAppRequest request = new CreateAppRequest();
		request.withCreateApplicationRequest(appRequest);
		CreateAppResult result = pinpoint.createApp(request);
		
		String appID = result.getApplicationResponse().getId();
		System.out.println("Application " + appName + " has been created.");
		System.out.println("App ID is: " + appID);
	}
}
