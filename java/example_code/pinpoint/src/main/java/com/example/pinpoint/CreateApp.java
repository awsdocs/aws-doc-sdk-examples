package com.example.pinpoint;

import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.CreateAppRequest;
import com.amazonaws.services.pinpoint.model.CreateAppResult;
import com.amazonaws.services.pinpoint.model.CreateApplicationRequest;
import com.amazonaws.regions.Regions;

public class CreateApp {

	public static void main(String[] args) {
        final String USAGE = "\n" +
                "CreateApp - create an application in pinpoint dashboard\n\n" +
                "Usage: CreateApp <appName>\n\n" +
                "Where:\n" +
                "  appName - the name of the application to create.\n\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String appName = args[0];
        
        System.out.println("Creating an application with name: " + appName);
            
		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		
		CreateApplicationRequest appRequest = new CreateApplicationRequest()
				.withName("MyTestApp");
		
		CreateAppRequest request = new CreateAppRequest();
		request.withCreateApplicationRequest(appRequest);
		CreateAppResult result = pinpoint.createApp(request);
		
		String appID = result.getApplicationResponse().getId();
		System.out.println("Application " + appName + " has been created.");
		System.out.println("App ID is: " + appID);
	}
}
