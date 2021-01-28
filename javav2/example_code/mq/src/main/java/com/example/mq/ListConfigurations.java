package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.*;

import java.util.List;

public class ListConfigurations {
    public static void main(String[] args) {
        try {
            Region region = Region.US_WEST_2;
        
            MqClient mqClient = MqClient.builder()
                    .region(region)
                    .build();
            

            ListConfigurationsResponse response = mqClient.listConfigurations();
            List<Configuration> result = response.configurations();

            for (Configuration configuration : result) {
                System.out.println("Name: " + configuration.name() + " | " +
                    "ID: " + configuration.id() + "\n");
            }

            mqClient.close();

        } catch (MqException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
