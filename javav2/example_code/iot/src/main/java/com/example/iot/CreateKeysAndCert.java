package com.example.iot;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iot.IotClient;
import software.amazon.awssdk.services.iot.model.CreateKeysAndCertificateResponse;
import software.amazon.awssdk.services.iot.model.IotException;

public class CreateKeysAndCert {

    public static void main(String[] args) {
        System.out.println("Creating a new AWS IoT");

        IotClient iotClient = IotClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            // Create keys and certificate
            CreateKeysAndCertificateResponse response = iotClient.createKeysAndCertificate();

            // Extract key, certificate, and certificate ARN
            String privateKey = response.keyPair().privateKey();
            String certificatePem = response.certificatePem();
            String certificateArn = response.certificateArn();

            // Print the details
            System.out.println("Private Key:");
            System.out.println(privateKey);
            System.out.println("\nCertificate:");
            System.out.println(certificatePem);
            System.out.println("\nCertificate ARN:");
            System.out.println(certificateArn);

        } catch (IotException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } finally {
            // Close the IoT client
            iotClient.close();
        }
    }
}
