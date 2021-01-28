package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

public class UpdateBrokerConfiguration {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "CreateConfiguration <brokerId> <configurationId>\n\n" +
                "Where:\n" +
                "  brokerId - The ID of the broker being updated\n" +
                "  configurationId - The ID of the configuration being associate with a broker.\n\n" ;

        if (args.length > 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        // UpdateBrokerRequest request = UpdateBrokerRequest.builder()
        //     .configuration(configuration)
        //     .brokerId(broker)
        //     .build();
        
        // mqClient.updateBroker(request);
        mqClient.close();
    }
}
