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
*/

// snippet-sourcedescription:[pinpoint_update_endpoints_batch demonstrates how to update several existing endpoints in a single call to the API.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[UpdateEndpointsBatch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-08-07]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_update_endpoints_batch.complete]

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.ChannelType;
import com.amazonaws.services.pinpoint.model.EndpointBatchItem;
import com.amazonaws.services.pinpoint.model.EndpointBatchRequest;
import com.amazonaws.services.pinpoint.model.EndpointUser;
import com.amazonaws.services.pinpoint.model.UpdateEndpointsBatchRequest;
import com.amazonaws.services.pinpoint.model.UpdateEndpointsBatchResult;

import java.util.Arrays;

public class AddExampleEndpoints {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "AddExampleEndpoints - Adds example endpoints to an Amazon Pinpoint application." +
                "Usage: AddExampleEndpoints <applicationId>" +
                "Where:\n" +
                "  applicationId - The ID of the Amazon Pinpoint application to add the example endpoints to.";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }


        String applicationId = args[0];

        // Initializes an endpoint definition with channel type, address, and ID.
        EndpointBatchItem richardRoesEmailEndpoint = new EndpointBatchItem()
                .withChannelType(ChannelType.EMAIL)
                .withAddress("richard_roe@example.com")
                .withId("example_endpoint_1");

        // Adds custom attributes to the endpoint.
        richardRoesEmailEndpoint.addAttributesEntry("interests", Arrays.asList(
                "music",
                "books"));

        // Adds custom metrics to the endpoint.
        richardRoesEmailEndpoint.addMetricsEntry("music_interest_level", 3.0);
        richardRoesEmailEndpoint.addMetricsEntry("books_interest_level", 7.0);

        // Initializes a user definition with a user ID.
        EndpointUser richardRoe = new EndpointUser().withUserId("example_user_1");

        // Adds custom user attributes.
        richardRoe.addUserAttributesEntry("name", Arrays.asList("Richard", "Roe"));

        // Adds the user definition to the endpoint.
        richardRoesEmailEndpoint.setUser(richardRoe);

        // Initializes an endpoint definition with channel type, address, and ID.
        EndpointBatchItem maryMajorsSmsEndpoint = new EndpointBatchItem()
                .withChannelType(ChannelType.SMS)
                .withAddress("+16145550100")
                .withId("example_endpoint_2");

        // Adds custom attributes to the endpoint.
        maryMajorsSmsEndpoint.addAttributesEntry("interests", Arrays.asList(
                "cooking",
                "politics",
                "finance"));

        // Adds custom metrics to the endpoint.
        maryMajorsSmsEndpoint.addMetricsEntry("cooking_interest_level", 5.0);
        maryMajorsSmsEndpoint.addMetricsEntry("politics_interest_level", 8.0);
        maryMajorsSmsEndpoint.addMetricsEntry("finance_interest_level", 4.0);

        // Initializes a user definition with a user ID.
        EndpointUser maryMajor = new EndpointUser().withUserId("example_user_2");

        // Adds custom user attributes.
        maryMajor.addUserAttributesEntry("name", Arrays.asList("Mary", "Major"));

        // Adds the user definition to the endpoint.
        maryMajorsSmsEndpoint.setUser(maryMajor);

        // Adds multiple endpoint definitions to a single request object.
        EndpointBatchRequest endpointList = new EndpointBatchRequest()
                .withItem(richardRoesEmailEndpoint)
                .withItem(maryMajorsSmsEndpoint);

        // Initializes the Amazon Pinpoint client.
        AmazonPinpoint pinpointClient = AmazonPinpointClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();

        // Updates or creates the endpoints with Amazon Pinpoint.
        UpdateEndpointsBatchResult result = pinpointClient.updateEndpointsBatch(
                new UpdateEndpointsBatchRequest()
                .withApplicationId(applicationId)
                .withEndpointBatchRequest(endpointList));

        System.out.format("Update endpoints batch result: %s\n",
                result.getMessageBody().getMessage());

    }

}

// snippet-end:[pinpoint.java.pinpoint_update_endpoints_batch.complete]


