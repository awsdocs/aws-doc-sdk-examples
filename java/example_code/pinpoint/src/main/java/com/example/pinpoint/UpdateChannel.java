package com.example.pinpoint;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.APNSChannelRequest;
import com.amazonaws.services.pinpoint.model.APNSChannelResponse;
import com.amazonaws.services.pinpoint.model.GetApnsChannelRequest;
import com.amazonaws.services.pinpoint.model.GetApnsChannelResult;
import com.amazonaws.services.pinpoint.model.UpdateApnsChannelRequest;
import com.amazonaws.services.pinpoint.model.UpdateApnsChannelResult;

public class UpdateChannel {
	
	public static void main(String[] args) {
		final String USAGE = "\n" +
                "CreateChannel - create an application in pinpoint dashboard\n\n" +
                "Usage: CreateChannel <appId>\n\n" +
                "Where:\n" +
                "  appId - the name of the application to create.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }
        String appId = args[0];
        
		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		
		APNSChannelResponse getResponse = getApnsChannel(pinpoint, appId);
		toggleApnsChannel(pinpoint, appId, getResponse);
		getApnsChannel(pinpoint, appId);
	}
	
	private static APNSChannelResponse getApnsChannel(AmazonPinpoint client, String appId) {
		GetApnsChannelRequest request = new GetApnsChannelRequest()
				.withApplicationId(appId);
		
		GetApnsChannelResult result = client.getApnsChannel(request);
		APNSChannelResponse response = result.getAPNSChannelResponse();
		System.out.println("Channel state: " + response.getEnabled());
		return response;
	}
	
	private static void toggleApnsChannel(AmazonPinpoint client, String appId, APNSChannelResponse getResponse) {
		Boolean enabled = true; 
		
		if (getResponse.getEnabled()) {
			enabled = false;
		}
		
		APNSChannelRequest request = new APNSChannelRequest()
				.withEnabled(enabled);
		
		UpdateApnsChannelRequest updateRequest = new UpdateApnsChannelRequest()
				.withAPNSChannelRequest(request)
				.withApplicationId(appId);
		UpdateApnsChannelResult result = client.updateApnsChannel(updateRequest);
		System.out.println("Channel state: " + result.getAPNSChannelResponse().getEnabled());
	}
}
