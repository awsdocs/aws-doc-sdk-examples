//snippet-sourcedescription:[DescribeBroker.java demonstrates how to describe an Amazon MQ broker using the Java SDK.]
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
    // snippet-start:[mq.java2.describe_broker.main]
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
    // snippet-end:[mq.java2.describe_broker.main]
}
// snippet-end:[mq.java2.describe_broker.complete]