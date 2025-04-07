// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[mq.java2.create_configuration.complete]
package com.example.mq;

// snippet-start:[mq.java2.create_configuration.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.CreateConfigurationRequest;
import software.amazon.awssdk.services.mq.model.CreateConfigurationResponse;
import software.amazon.awssdk.services.mq.model.MqException;
// snippet-end:[mq.java2.create_configuration.import]

public class CreateConfiguration {
    public static void main(String[] args) {

        final String USAGE = """

                Usage: CreateConfiguration <configurationName>

                Where:
                  configurationName - the name of the ActiveMQ configuration.

                """;

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

        String result = createNewConfigutation(mqClient, configurationName);
        System.out.println("Configuration ID: " + result);
        mqClient.close();
    }

    // snippet-start:[mq.java2.create_configuration.main]
    public static String createNewConfigutation(MqClient mqClient, String configurationName) {
        try {
            CreateConfigurationRequest configurationRequest = CreateConfigurationRequest.builder()
                    .name(configurationName)
                    .engineVersion("5.18")
                    .engineType("ACTIVEMQ")
                    .authenticationStrategy("SIMPLE")
                    .build();

            CreateConfigurationResponse response = mqClient.createConfiguration(configurationRequest);
            return response.id();

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[mq.java2.create_configuration.main]
}
// snippet-end:[mq.java2.create_configuration.complete]