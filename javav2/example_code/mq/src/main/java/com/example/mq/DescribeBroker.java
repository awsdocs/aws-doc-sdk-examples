package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

public class DescribeBroker {
    public static void main(String[] args) {
        final String USAGE = "\n" +
            "Usage: " +
            "DescribeBroker <brokerName>\n\n" +
            "Where:\n" +
            "  brokerName - The name of the broker.\n\n";
        
        String brokerName = "";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        } else {
            brokerName = args[0];
        }

        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        String result = describeBroker(mqClient, brokerName);
        System.out.print("Broker ID: " + result);
        mqClient.close();
    }
    public static String describeBroker(MqClient mqClient, String brokerName) {
        try {
            DescribeBrokerRequest request = DescribeBrokerRequest.builder()
                .brokerId(brokerName)
                .build();
            
            DescribeBrokerResponse response = mqClient.describeBroker(request);
            System.out.print(response + "\n\n");
            return response.brokerId();
            

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
