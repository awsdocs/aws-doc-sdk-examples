//snippet-sourcedescription:[AddExampleEndpoints.java demonstrates how to update several existing endpoints in a single call to the API.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-service:[pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[03/02/2020]
//snippet-sourceauthor:[scmacdon-aws]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

//snippet-start:[pinpoint.java2.update_batch.import]
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

public class AddExampleEndpoints {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "AddExampleEndpoints -  demonstrates  how to update several existing endpoints in a single call\n\n" +
                "Usage: AddExampleEndpoints <appId>\n\n" +
                "Where:\n" +
                "  appId - the ID of the application to delete.\n\n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String applicationId = args[0];

        // Create a PinpointClient object
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        updateEndpointsViaBatch(pinpoint, applicationId);
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

            // Create an EndpointBatchItem object for Richard Roe
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

            // Create an EndpointBatchItem object for Mary Major
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

            // Create the UpdateEndpointsBatchRequest
            UpdateEndpointsBatchRequest batchRequest = UpdateEndpointsBatchRequest.builder()
                .applicationId(applicationId)
                .endpointBatchRequest(endpointList)
                .build();

            //  Updates the endpoints with Amazon Pinpoint
            UpdateEndpointsBatchResponse result = pinpoint.updateEndpointsBatch(batchRequest);
            System.out.format("Update endpoints batch result: %s\n",
                result.messageBody().message());

     } catch (PinpointException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
        //snippet-end:[pinpoint.java2.update_batch.main]
  }
}
