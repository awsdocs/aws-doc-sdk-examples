package com.example.pinpoint;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.GetJourneyRequest;
import software.amazon.awssdk.services.pinpoint.model.GetJourneyResponse;
import software.amazon.awssdk.services.pinpoint.model.JourneyResponse;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;

public class GetJourney {


        public static void main(String[] args) {
            final String USAGE = "\n" +
                    "CreateApp - create an application in the Amazon Pinpoint dashboard\n\n" +
                    "Usage: CreateApp <appName>\n\n" +
                    "Where:\n" +
                    "  appName - the name of the application to create.\n\n";

         //   if (args.length != 1) {
         //       System.out.println(USAGE);
         //       System.exit(1);
         //   }

            String existingApplicationId = "2fdc4442c6a2483f85eaf7a943054815";
            String journeyId = "9e051f6ab2b549e9a70842cef2a96a4d";

            PinpointClient pinpoint = PinpointClient.builder()
                    .region(Region.US_EAST_1)
                    .build();

            try {
                GetJourneyRequest request = GetJourneyRequest.builder()
                    .journeyId(journeyId)
                    .applicationId(existingApplicationId)
                    .build();

                GetJourneyResponse response = pinpoint.getJourney(request);
                JourneyResponse journeyResponse =  response.journeyResponse();
                System.out.println("The name of the journey is "+journeyResponse.name());

        } catch (PinpointException e) {
        System.err.println(e.awsErrorDetails().errorMessage());
        System.exit(1);
    }
   }
}
