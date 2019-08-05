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

// snippet-sourcedescription:[pinpoint_create_endpoint demonstrates how to create a new endpoint that includes standard and custom attributes and metrics.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[CreateEndpoint]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2018-08-07]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_create_endpoint.complete]

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.*;
import java.util.Arrays;

public class AddExampleEndpoint {

	public static void main(String[] args) {
	
		final String USAGE = "\n" +
			"AddExampleEndpoint - Adds an example endpoint to an Amazon Pinpoint application." +
			"Usage: AddExampleEndpoint <applicationId>" +
			"Where:\n" +
			"  applicationId - The ID of the Amazon Pinpoint application to add the example " +
			"endpoint to.";
		
		if (args.length < 1) {
		    System.out.println(USAGE);
		    System.exit(1);
		}
		
		String applicationId = args[0];
		
		// The device token assigned to the user's device by Apple Push Notification service (APNs).
		String deviceToken = "1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0t1u2v3w4x5y6z7a8b9c0d1e2f";
		
		// Initializes an endpoint definition with channel type and address.
		EndpointRequest wangXiulansIphoneEndpoint = new EndpointRequest()
			.withChannelType(ChannelType.APNS)
			.withAddress(deviceToken);
		
		// Adds custom attributes to the endpoint.
		wangXiulansIphoneEndpoint.addAttributesEntry("interests", Arrays.asList(
			"technology",
			"music",
			"travel"));
		
		// Adds custom metrics to the endpoint.
		wangXiulansIphoneEndpoint.addMetricsEntry("technology_interest_level", 9.0);
		wangXiulansIphoneEndpoint.addMetricsEntry("music_interest_level", 6.0);
		wangXiulansIphoneEndpoint.addMetricsEntry("travel_interest_level", 4.0);
		
		// Adds standard demographic attributes.
		wangXiulansIphoneEndpoint.setDemographic(new EndpointDemographic()
			.withAppVersion("1.0")
			.withMake("apple")
			.withModel("iPhone")
			.withModelVersion("8")
			.withPlatform("ios")
			.withPlatformVersion("11.3.1")
			.withTimezone("America/Los_Angeles"));
		
		// Adds standard location attributes.
		wangXiulansIphoneEndpoint.setLocation(new EndpointLocation()
			.withCountry("US")
			.withCity("Seattle")
			.withPostalCode("98121")
			.withLatitude(47.61)
			.withLongitude(-122.33));
		
		// Initializes the Amazon Pinpoint client.
		AmazonPinpoint pinpointClient = AmazonPinpointClientBuilder.standard()
			.withRegion(Regions.US_EAST_1).build();
		
		// Updates or creates the endpoint with Amazon Pinpoint.
		UpdateEndpointResult result = pinpointClient.updateEndpoint(new UpdateEndpointRequest()
			.withApplicationId(applicationId)
			.withEndpointId("example_endpoint")
			.withEndpointRequest(wangXiulansIphoneEndpoint));
		
		System.out.format("Update endpoint result: %s\n", result.getMessageBody().getMessage());
	
	}
}
// snippet-end:[pinpoint.java.pinpoint_create_endpoint.complete]
