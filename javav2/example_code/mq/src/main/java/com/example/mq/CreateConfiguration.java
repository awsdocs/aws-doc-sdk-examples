package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

public class CreateConfiguration {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "CreateConfiguration <configurationName>\n\n" +
                "Where:\n" +
                "  configurationName - the name of the ActiveMQ configuration.\n\n" ;

        String configurationName = "";
        if (args.length > 1) {
            System.out.println(USAGE);
            System.exit(1);
        } else if (args.length == 1) {
            configurationName = args[0];
        } else {
            configurationName = "Configuration-" + System.currentTimeMillis();
        }
        
        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        createNewConfigutation(mqClient, configurationName);
        mqClient.close();
    }
    public static String createNewConfigutation(MqClient mqClient, String configurationName) {
        try {
            CreateConfigurationRequest configurationRequest = CreateConfigurationRequest.builder()
                .name(configurationName)
                .engineVersion("5.15.14")
                .engineType("ACTIVEMQ")
                .authenticationStrategy("SIMPLE")
                .build();

            CreateConfigurationResponse response = mqClient.createConfiguration(configurationRequest);
            System.out.println(response.id());
            return response.id();

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
}
