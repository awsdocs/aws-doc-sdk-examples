// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[mq.java2.describe_broker.complete]

package com.example.mq;

// snippet-start:[mq.java2.describe_broker.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.DescribeBrokerRequest;
import software.amazon.awssdk.services.mq.model.DescribeBrokerResponse;
import software.amazon.awssdk.services.mq.model.MqException;
// snippet-end:[mq.java2.describe_broker.import]

public class DescribeBroker {
    public static void main(String[] args) {
        final String USAGE = """

                Usage: DescribeBroker <brokerName>

                Where:
                  brokerName - The name of the broker.

                """;

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

    // snippet-start:[mq.java2.describe_broker.main]
    public static String describeBroker(MqClient mqClient, String brokerId) {
        try {
            while (true) {
                DescribeBrokerRequest request = DescribeBrokerRequest.builder()
                        .brokerId(brokerId)
                        .build();

                DescribeBrokerResponse response = mqClient.describeBroker(request);
                String currentState = response.brokerStateAsString();
                System.out.println("Current Broker State: " + currentState);

                if ("RUNNING".equalsIgnoreCase(currentState)) {
                    return response.brokerId();
                }

                // Sleep before polling again to avoid throttling
                Thread.sleep(5_000);
            }
        } catch (MqException e) {
            System.err.println("Error describing broker: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while waiting for broker to complete.", e);
        }
    }
    // snippet-end:[mq.java2.describe_broker.main]
}
// snippet-end:[mq.java2.describe_broker.complete]