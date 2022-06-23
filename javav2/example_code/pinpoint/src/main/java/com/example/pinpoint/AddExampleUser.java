//snippet-sourcedescription:[AddExampleUser.java demonstrates how to update an existing endpoint.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pinpoint;

//snippet-start:[pinpoint.java2.update_endpoint.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.EndpointRequest;
import software.amazon.awssdk.services.pinpoint.model.EndpointUser;
import software.amazon.awssdk.services.pinpoint.model.ChannelType;
import software.amazon.awssdk.services.pinpoint.model.UpdateEndpointRequest;
import software.amazon.awssdk.services.pinpoint.model.UpdateEndpointResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//snippet-end:[pinpoint.java2.update_endpoint.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AddExampleUser {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <appId>\n\n" +
                "Where:\n" +
                "   appId - The ID of the application to delete.\n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
       }

        String applicationId = args[0];
        String endPointId = args[1];
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)

                .build();

        updatePinpointEndpoint(pinpoint, applicationId, endPointId);
        pinpoint.close();
    }

    //snippet-start:[pinpoint.java2.update_endpoint.main]
    public static void updatePinpointEndpoint(PinpointClient pinpoint,String applicationId, String endPointId) {

        try{
            List<String> wangXiList = new ArrayList<>();
            wangXiList.add("cooking");
            wangXiList.add("running");
            wangXiList.add("swimming");

            Map myMapWang = new HashMap<>();
            myMapWang.put("interests", wangXiList);

            List<String> myNameWang = new ArrayList<>();
            myNameWang.add("Wang ");
            myNameWang.add("Xiulan");

            Map wangName = new HashMap<>();
            wangName.put("name",myNameWang );

            EndpointUser wangMajor = EndpointUser.builder()
                .userId("example_user_10")
                .userAttributes(wangName)
                .build();

            // Create an EndpointBatchItem object for Mary Major.
            EndpointRequest wangXiulanEndpoint = EndpointRequest.builder()
                .channelType(ChannelType.EMAIL)
                .address("wang_xiulan@example.com")
                .attributes(myMapWang)
                .user(wangMajor)
                .build();

            // Adds multiple endpoint definitions to a single request object.
            UpdateEndpointRequest endpointList = UpdateEndpointRequest.builder()
                .applicationId(applicationId)
                .endpointRequest(wangXiulanEndpoint)
                .endpointId(endPointId)
                .build();

            UpdateEndpointResponse result = pinpoint.updateEndpoint(endpointList);
            System.out.format("Update endpoint result: %s\n", result.messageBody().message());

    } catch (PinpointException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
 }
    //snippet-end:[pinpoint.java2.update_endpoint.main]
}
