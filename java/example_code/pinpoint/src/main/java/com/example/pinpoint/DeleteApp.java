package com.example.pinpoint;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.DeleteAppRequest;

public class DeleteApp {

	public static void main(String args[]) {
		final String USAGE = "\n" +
	            "DeleteApp - delete an application in the pinpoint dashboard\n\n" +
	            "Usage: DeleteApp <appID>\n\n" +
	            "Where:\n" +
	            "  appID - the applicatino ID of the application to delete.\n\n";

	    if (args.length < 1) {
	        System.out.println(USAGE);
	        System.exit(1);
	    }

	    String appID = args[0];
	    
	    System.out.println("Deleting application: " + appID);
	    
		AmazonPinpoint pinpoint = AmazonPinpointClientBuilder.standard().withRegion(Regions.US_EAST_1).build();

		DeleteAppRequest deleteRequest = new DeleteAppRequest()
				.withApplicationId(appID);
		
		pinpoint.deleteApp(deleteRequest);
	
	}
}
