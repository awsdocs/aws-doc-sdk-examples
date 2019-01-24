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

// snippet-sourcedescription:[pinpoint_associate_endpoints_users demonstrates how to associate users with endpoints in Amazon Pinpoint.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[UpdateEndpoint]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2018-08-07]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_associate_endpoints_users.complete]

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.EndpointRequest;
import com.amazonaws.services.pinpoint.model.EndpointUser;
import com.amazonaws.services.pinpoint.model.UpdateEndpointRequest;
import com.amazonaws.services.pinpoint.model.UpdateEndpointResult;

import java.util.Arrays;
import java.util.Collections;

public class AddExampleUser {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "AddExampleUser - Adds a user definition to the specified Amazon Pinpoint endpoint." +
                "Usage: AddExampleUser <endpointId> <applicationId>" +
                "Where:\n" +
                "  endpointId - The ID of the endpoint to add the user to." +
                "  applicationId - The ID of the Amazon Pinpoint application that contains the endpoint.";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String endpointId = args[0];
        String applicationId = args[1];

        // Creates a new user definition.
        EndpointUser wangXiulan = new EndpointUser().withUserId("example_user");

        // Assigns custom user attributes.
        wangXiulan.addUserAttributesEntry("name", Arrays.asList("Wang", "Xiulan"));
        wangXiulan.addUserAttributesEntry("gender", Collections.singletonList("female"));
        wangXiulan.addUserAttributesEntry("age", Collections.singletonList("39"));

        // Adds the user definition to the EndpointRequest that is passed to the Amazon Pinpoint client.
        EndpointRequest wangXiulansIphone = new EndpointRequest()
                .withUser(wangXiulan);

        // Initializes the Amazon Pinpoint client.
        AmazonPinpoint pinpointClient = AmazonPinpointClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();

        // Updates the specified endpoint with Amazon Pinpoint.
        UpdateEndpointResult result = pinpointClient.updateEndpoint(new UpdateEndpointRequest()
                .withEndpointRequest(wangXiulansIphone)
                .withApplicationId(applicationId)
                .withEndpointId(endpointId));

        System.out.format("Update endpoint result: %s\n", result.getMessageBody().getMessage());

    }
}
// snippet-end:[pinpoint.java.pinpoint_associate_endpoints_users.complete]
