package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

public class CreateBroker {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "CreateBrokerActiveMQ <engineType> <brokerName>\n\n" +
                "Where:\n" +
                "  engineType - Required. RABBITMQ or ACTIVEMQ for broker's engine type.\n" +
                "  brokerName - Optional. The name of the Amazon MQ for ActiveMQ broker.\n\n";
        
        int argsLength = args.length;
        String brokerName = "";
        String engineType = "";

        if (argsLength < 1 || argsLength > 2) {
            System.out.println(USAGE);
            System.exit(1);
        }  else {
            engineType = args[0];
            if (argsLength == 1) {
                brokerName = engineType + " - " + System.currentTimeMillis(); 
            } else {
                brokerName = args[1];
            }
        }

        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        createBrokerActiveMQ(mqClient, engineType, brokerName);
        mqClient.close();
    }
    public static void createBrokerActiveMQ(MqClient mqClient, String engineType, String brokerName) {
        
        try {

            User user = User.builder()
            .username("testAdminUser")
            .password("testAdminPassword")
            .build();

            String engineVersion = "";

            if (engineType.equals("ACTIVEMQ")) {
                engineVersion = "5.15.14";
            } else {
                engineVersion = "3.8.6";
            }
            // Creates an ActiveMQ broker and a new configuration with default values.
            mqClient.createBroker(CreateBrokerRequest.builder()
                .brokerName(brokerName)
                .engineType(engineType)
                .engineVersion(engineVersion)
                .deploymentMode("SINGLE_INSTANCE")
                .users(user)
                .publiclyAccessible(true)
                .autoMinorVersionUpgrade(true)
                .hostInstanceType("mq.t3.micro")
                .build());

        } catch (MqException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
        }
    }
}
