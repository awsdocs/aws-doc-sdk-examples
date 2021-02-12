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
                "  configurationId - The ID of the configuration being associate with a broker.\n\n" +
                "Tip: You can use ListBrokers and ListConfigurations to display a list of your brokers and configurations.\n\n";

        int argsLength = args.length;
        String brokerId = "";
        String configurationId = "";

        if (argsLength != 2) {
            System.out.println(USAGE);
            System.exit(1);
        } else {
            brokerId = args[0];
            configurationId = args[1];
        }

        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        updateBrokerConfiguration(mqClient, brokerId, configurationId);
        mqClient.close();
    }
    public static void updateBrokerConfiguration(MqClient mqClient, String brokerId, String configurationId) {
        try {

            ConfigurationId configuration = ConfigurationId.builder()
                .id(configurationId)
                .build();

            UpdateBrokerRequest request = UpdateBrokerRequest.builder()
                .brokerId(brokerId)
                .configuration(configuration)
                .build();
            
            UpdateBrokerResponse response = mqClient.updateBroker(request);
            
            System.out.println(response);

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
