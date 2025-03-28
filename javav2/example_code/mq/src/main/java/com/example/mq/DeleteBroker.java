package com.example.mq;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mq.MqClient;
import software.amazon.awssdk.services.mq.model.DeleteBrokerRequest;

public class DeleteBroker {

    public static void main(String [] args) {
        final String usage = """

                Usage: <brokerId>

                Where:
                    brokerId - The id of the Amazon broker to delete.

                """;
        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String brokerId = args[0];
        Region region = Region.US_WEST_2;
        MqClient mqClient = MqClient.builder()
                .region(region)
                .build();

        deleteBroker(mqClient, brokerId);
    }

    public static void deleteBroker(MqClient mqClient, String brokerId) {
        DeleteBrokerRequest request = DeleteBrokerRequest.builder()
                .brokerId(brokerId)
                .build();

        mqClient.deleteBroker(request);
        System.out.println("Delete broker successfully");
    }
}
