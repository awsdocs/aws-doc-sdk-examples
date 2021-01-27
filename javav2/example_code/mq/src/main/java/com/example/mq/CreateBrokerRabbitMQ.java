package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

public class CreateBrokerRabbitMQ {
    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage: " +
                "CreateBrokerRabbitMQ <brokerName>\n\n" +
                "Where:\n" +
                "  brokerName - the name of the Amazon MQ for RabbitMQ broker.\n\n" ;

        String brokerName = "";
        if (args.length > 1) {
            System.out.println(USAGE);
            System.exit(1);
        } else if (args.length == 1) {
            brokerName = args[0];
        } else {
            brokerName = "RabbitMQ-" + System.currentTimeMillis();
        }
        
        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        createBrokerRabbitMQ(mqClient, brokerName);
        mqClient.close();
    }
    
    public static void createBrokerRabbitMQ(MqClient mqClient, String brokerName) {
        
        try {

            User rabbitMQUser = User.builder()
            .username("testAdminUser")
            .password("testAdminPassword")
            .build();

            mqClient.createBroker(CreateBrokerRequest.builder()
                .brokerName(brokerName)
                .engineType("RABBITMQ")
                .engineVersion("3.8.6")
                .deploymentMode("SINGLE_INSTANCE")
                .users(rabbitMQUser)
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
