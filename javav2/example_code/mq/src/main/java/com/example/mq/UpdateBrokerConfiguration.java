//snippet-sourcedescription:[UpdateBrokerConfigurations.java demonstrates how to associate a new configuration with a specified broker.]
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

// snippet-start:[mq.java2.update_broker_configuration.complete]
package com.example.mq;

// snippet-start:[mq.java2.update_broker_configuration.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.ConfigurationId;
import software.amazon.awssdk.services.mq.model.UpdateBrokerRequest;
import software.amazon.awssdk.services.mq.model.UpdateBrokerResponse;
import software.amazon.awssdk.services.mq.model.MqException;

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
        
        // Applies only to Amazon MQ for ActiveMQ brokers.
        String result = updateBrokerConfiguration(mqClient, brokerId, configurationId);
        System.out.println(result);
        mqClient.close();
    }
    // snippet-start:[mq.java2.update_broker_configuration.main]
    public static String updateBrokerConfiguration(MqClient mqClient, String brokerId, String configurationId) {
        try {
            ConfigurationId configuration = ConfigurationId.builder()
                .id(configurationId)
                .build();

            UpdateBrokerRequest request = UpdateBrokerRequest.builder()
                .brokerId(brokerId)
                .configuration(configuration)
                .build();
            
            UpdateBrokerResponse response = mqClient.updateBroker(request);
            
            return response.brokerId();

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[mq.java2.update_broker_configuration.main]
}
// snippet-end:[mq.java2.update_broker_configuration.complete]
