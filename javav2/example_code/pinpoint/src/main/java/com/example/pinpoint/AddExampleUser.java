//snippet-sourcedescription:[AddExampleEndpoints.java demonstrates how to associate users with endpoints in Amazon Pinpoint.]
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

public class AddExampleUser {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "AddExampleUser -  demonstrates  how to update several existing endpoints in a single call\n\n" +
                "Usage: AddExampleEndpoints <appId>\n\n" +
                "Where:\n" +
                "  appId - the ID of the application to delete.\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
       }

        String applicationId = args[0];
        String endPointId = args[1];

        // Create a PinpointClient object
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .build();

        updatePinpointEndpoint(pinpoint, applicationId, endPointId);
    }

    //snippet-start:[pinpoint.java2.update_endpoint.main]
    public static void updatePinpointEndpoint(PinpointClient pinpoint,String applicationId, String endPointId) {


        try{
            List<String> wangXiList = new ArrayList<String>();
            wangXiList.add("cooking");
            wangXiList.add("running");
            wangXiList.add("swimming");

            Map myMapWang = new HashMap<String, List>();
            myMapWang.put("interests", wangXiList);

            List<String> myNameWang = new ArrayList<String>();
            myNameWang.add("Wang ");
            myNameWang.add("Xiulan");

            Map wangName = new HashMap<String, List>();
            wangName.put("name",myNameWang );

            EndpointUser wangMajor = EndpointUser.builder()
                .userId("example_user_10")
                .userAttributes(wangName)
                .build();

            // Create an EndpointBatchItem object for Mary Major
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
    //snippet-end:[pinpoint.java2.update_endpoint.main]
  }
}
