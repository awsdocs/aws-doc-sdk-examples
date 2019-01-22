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

// snippet-sourcedescription:[pinpoint_delete_endpoint demonstrates how to delete an existing endpoint in Amazon Pinpoint.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[DeleteEndpoint]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-08-07]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_delete_endpoint.complete]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.DeleteEndpointRequest;
import com.amazonaws.services.pinpoint.model.DeleteEndpointResult;

import java.util.Arrays;

public class DeleteEndpoints {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "DeleteEndpoints - Removes one or more endpoints from an " +
                "Amazon Pinpoint application.\n\n" +

                "Usage: DeleteEndpoints <applicationId> <endpointId1> [endpointId2 ...]\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String applicationId = args[0];
        String[] endpointIds = Arrays.copyOfRange(args, 1, args.length);

        // Initializes the Amazon Pinpoint client.
        AmazonPinpoint pinpointClient = AmazonPinpointClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();

        try {
            // Deletes each of the specified endpoints with the Amazon Pinpoint client.
            for (String endpointId: endpointIds) {
                DeleteEndpointResult result =
                        pinpointClient.deleteEndpoint(new DeleteEndpointRequest()
                        .withEndpointId(endpointId)
                        .withApplicationId(applicationId));
                System.out.format("Deleted endpoint %s.\n", result.getEndpointResponse().getId());
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[pinpoint.java.pinpoint_delete_endpoint.complete]
