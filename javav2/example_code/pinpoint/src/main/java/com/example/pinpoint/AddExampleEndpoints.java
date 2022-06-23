//snippet-sourcedescription:[AddExampleEndpoints.java demonstrates how to update several existing endpoints in a single call to the API.]
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

//snippet-start:[pinpoint.java2.update_batch.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.UpdateEndpointsBatchResponse;
import software.amazon.awssdk.services.pinpoint.model.EndpointUser;
import software.amazon.awssdk.services.pinpoint.model.EndpointBatchItem;
import software.amazon.awssdk.services.pinpoint.model.ChannelType;
import software.amazon.awssdk.services.pinpoint.model.EndpointBatchRequest;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import software.amazon.awssdk.services.pinpoint.model.UpdateEndpointsBatchRequest;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
//snippet-end:[pinpoint.java2.update_batch.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AddExampleEndpoints {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <appId>\n\n" +
                "Where:\n" +
                "   appId - The ID of the application.\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String applicationId = args[0];
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        updateEndpointsViaBatch(pinpoint, applicationId);
        pinpoint.close();
    }

    //snippet-start:[pinpoint.java2.update_batch.main]
    public static void updateEndpointsViaBatch( PinpointClient pinpoint, String applicationId) {

        try {
            List<String> myList = new ArrayList<String>();
            myList.add("music");
            myList.add("books");

            Map myMap = new HashMap<String, List>();
            myMap.put("attributes", myList);

            List<String> myNames = new ArrayList<String>();
            myList.add("Richard");
            myList.add("Roe");

            Map myMap2 = new HashMap<String, List>();
            myMap2.put("name",myNames );

            EndpointUser richardRoe = EndpointUser.builder()
                .userId("example_user_1")
                .userAttributes(myMap2)
                .build();

            // Create an EndpointBatchItem object for Richard Roe.
            EndpointBatchItem richardRoesEmailEndpoint = EndpointBatchItem.builder()
                .channelType(ChannelType.EMAIL)
                .address("richard_roe@example.com")
                .id("example_endpoint_1")
                .attributes(myMap)
                .user(richardRoe)
                .build();

            List<String> myListMary = new ArrayList<String>();
            myListMary.add("cooking");
            myListMary.add("politics");
            myListMary.add("finance");

            Map myMapMary = new HashMap<String, List>();
            myMapMary.put("interests", myListMary);

            List<String> myNameMary = new ArrayList<String>();
            myNameMary.add("Mary ");
            myNameMary.add("Major");

            Map maryName = new HashMap<String, List>();
            myMapMary.put("name",myNameMary );

            EndpointUser maryMajor = EndpointUser.builder()
                .userId("example_user_2")
                .userAttributes(maryName)
                .build();

            // Create an EndpointBatchItem object for Mary Major.
            EndpointBatchItem maryMajorsSmsEndpoint = EndpointBatchItem.builder()
                .channelType(ChannelType.SMS)
                .address("+16145550100")
                .id("example_endpoint_2")
                .attributes(myMapMary)
                .user(maryMajor)
                .build();

            // Adds multiple endpoint definitions to a single request object.
            EndpointBatchRequest endpointList = EndpointBatchRequest.builder()
                .item( richardRoesEmailEndpoint)
                .item( maryMajorsSmsEndpoint)
                .build();

            // Create the UpdateEndpointsBatchRequest.
            UpdateEndpointsBatchRequest batchRequest = UpdateEndpointsBatchRequest.builder()
                .applicationId(applicationId)
                .endpointBatchRequest(endpointList)
                .build();

            //  Updates the endpoints with Amazon Pinpoint.
            UpdateEndpointsBatchResponse result = pinpoint.updateEndpointsBatch(batchRequest);
            System.out.format("Update endpoints batch result: %s\n",
                result.messageBody().message());

     } catch (PinpointException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
  }
    //snippet-end:[pinpoint.java2.update_batch.main]
}
