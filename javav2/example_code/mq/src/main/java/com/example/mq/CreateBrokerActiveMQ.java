package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

public class CreateBrokerActiveMQ {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "CreateBrokerActiveMQ <brokerName>\n\n" +
                "Where:\n" +
                "  brokerName - the name of the Amazon MQ for ActiveMQ broker.\n\n" ;

        String brokerName = "";
        if (args.length > 1) {
            System.out.println(USAGE);
            System.exit(1);
        } else if (args.length == 1) {
            brokerName = args[0];
        } else {
            brokerName = "ActiveMQ-" + System.currentTimeMillis();
        }
        
        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        createBrokerActiveMQ(mqClient, brokerName);
        mqClient.close();
    }
    public static void createBrokerActiveMQ(MqClient mqClient, String brokerName) {
        
        try {

            User activeMQUser = User.builder()
            .username("testAdminUser")
            .password("testAdminPassword")
            .build();

            // Creates an ActiveMQ broker and a new configuration with default values.
            mqClient.createBroker(CreateBrokerRequest.builder()
                .brokerName(brokerName)
                .engineType("ACTIVEMQ")
                .engineVersion("5.15.14")
                .deploymentMode("SINGLE_INSTANCE")
                .users(activeMQUser)
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
