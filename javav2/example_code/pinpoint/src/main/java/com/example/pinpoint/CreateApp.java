//snippet-sourcedescription:[CreateApp.java demonstrates how to create an application in the Pinpoint dashboard.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-service:[pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-06-01]
//snippet-sourceauthor:[jschwarzwalder AWS]
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
//snippet-start:[pinpoint.java2.CreateApp.complete]
package com.example.pinpoint;

//snippet-start:[pinpoint.java2.CreateApp.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.CreateAppRequest;
import software.amazon.awssdk.services.pinpoint.model.CreateAppResponse;
import software.amazon.awssdk.services.pinpoint.model.CreateApplicationRequest;
//snippet-end:[pinpoint.java2.CreateApp.import]

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

        //snippet-start:[pinpoint.java2.CreateApp.main]
        String appName = args[0];

        System.out.println("Creating an application with name: " + appName);

        PinpointClient pinpoint = PinpointClient.builder().region(Region.US_EAST_1).build();

        CreateApplicationRequest appRequest = CreateApplicationRequest.builder()
                .name(appName)
                .build();

        CreateAppRequest request = CreateAppRequest.builder()
                .createApplicationRequest(appRequest)
                .build();

        CreateAppResponse result = pinpoint.createApp(request);

        String appID = result.applicationResponse().id();
        System.out.println("Application " + appName + " has been created.");
        System.out.println("App ID is: " + appID);
        //snippet-end:[pinpoint.java2.CreateApp.main]
    }
}
//snippet-end:[pinpoint.java2.CreateApp.complete]
