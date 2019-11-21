/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetApp.java demonstrates how to use the Pinpoint Java API (version 1) to get information about an application]
// snippet-service:[Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-11-21]
// snippet-sourceauthor:[AWS]

// snippet-start:[pinpoint.java.getapp.complete]
package com.example.pinpoint;

// snippet-start:[pinpoint.java.getapp.import]
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.GetAppRequest;
import com.amazonaws.services.pinpoint.model.AmazonPinpointException;
import java.io.IOException;
// snippet-end:[pinpoint.java.getapp.import]

public class GetApp {

    public static void main(String[] args) throws IOException {

        if (args.length < 1) {
            System.out.println("Please specify an application ID");
            System.exit(1);
        }

        // snippet-start:[pinpoint.java.getapp.main]
        String appId = args[0];
        String region = "us-west-2";
        try {
            AmazonPinpoint client = AmazonPinpointClientBuilder.standard()
                    .withRegion(region).build();

            //Create a GetAppRequest instance
            GetAppRequest appReq = new GetAppRequest();

            //Specify the ID of your application
            appReq.setApplicationId(appId);

            String appName = client.getApp(appReq).getApplicationResponse().getName();

            System.out.println("Name of the AWS Pinpoint app is "+appName);

        } catch (AmazonPinpointException e) {
            e.getStackTrace();
        }
        // snippet-end:[pinpoint.java.getapp.main]
    }
}
// snippet-end:[pinpoint.java.getapp.complete]
