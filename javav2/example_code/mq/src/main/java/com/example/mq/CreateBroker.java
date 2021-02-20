//snippet-sourcedescription:[CreateBroker.java demonstrates how to create an Amazon MQ broker.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon MQ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/18/2021]
//snippet-sourceauthor:[fararmin-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[mq.java2.create_broker.complete]
package com.example.mq;

// snippet-start:[mq.java2.create_broker.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.User;
import software.amazon.awssdk.services.mq.model.DescribeBrokerEngineTypesRequest;
import software.amazon.awssdk.services.mq.model.DescribeBrokerEngineTypesResponse;
import software.amazon.awssdk.services.mq.model.CreateBrokerRequest;
import software.amazon.awssdk.services.mq.model.CreateBrokerResponse;
import software.amazon.awssdk.services.mq.model.MqException;
// snippet-end:[mq.java2.create_broker.import]

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
                brokerName = engineType + "-" + System.currentTimeMillis(); 
            } else {
                brokerName = args[1];
            }
        }

        Region region = Region.US_WEST_2;
        
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();
        
        String brokerId = createBroker(mqClient, engineType, brokerName);
        System.out.println("The broker ID is: " + brokerId);
        mqClient.close();
    }
    // snippet-start:[mq.java2.create_broker.main]
    public static String createBroker(MqClient mqClient, String engineType, String brokerName) {
        
        try {

            // Create an Amazon MQ User object.
            User user = User.builder()
            .username("testAdminUser")
            .password("testAdminPassword")
            .consoleAccess(true)
            .build();

            // Check the latest minor version release for the given engine type,
            // and use the latest version to create the broker.
            DescribeBrokerEngineTypesRequest engineTypeRequest = DescribeBrokerEngineTypesRequest.builder()
                .engineType(engineType)
                .build();
            
            DescribeBrokerEngineTypesResponse enginesRequest = mqClient.describeBrokerEngineTypes(engineTypeRequest);
            String engineVersion = enginesRequest.brokerEngineTypes().get(0)
                .engineVersions().get(0)
                .name();

            // Creates a new Amazon MQ broker.
            // Creates a new broker configuration for ActiveMQ brokers.
            CreateBrokerResponse result = mqClient.createBroker(CreateBrokerRequest.builder()
                .brokerName(brokerName)
                .engineType(engineType)
                .engineVersion(engineVersion)
                .deploymentMode("SINGLE_INSTANCE")
                .users(user)
                .publiclyAccessible(true)
                .autoMinorVersionUpgrade(true)
                .hostInstanceType("mq.t3.micro")
                .build());


            return result.brokerId();

        } catch (MqException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
        }
        return "";
    }
}
// snippet-end:[mq.java2.create_broker.main]
// snippet-end:[mq.java2.create_broker.complete]