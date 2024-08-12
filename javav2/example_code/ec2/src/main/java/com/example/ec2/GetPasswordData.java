package com.example.ec2;

// snippet-start:[ec2.java2.get_password.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2AsyncClient;
import software.amazon.awssdk.services.ec2.model.*;

import java.util.concurrent.CompletableFuture;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetPasswordData {

    public static void main(String[] args) {
        final String usage = """

                Usage:
                   <instanceId> 

                Where:
                   instanceId - An instance id value that you can obtain from the AWS Management Console.\s
             """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }
        String instanceId = args[0];
        Ec2AsyncClient ec2AsyncClient = Ec2AsyncClient.builder()
            .region(Region.US_EAST_1)
            .build();

        try {
            CompletableFuture<Void> future = getPasswordDataAsync(ec2AsyncClient, instanceId);
            future.join(); // Wait for the async operation to complete.
            System.out.println("Password data retrieved successfully.");
        } catch (RuntimeException rte) {
            System.err.println("An exception occurred: " + (rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage()));
        }

    }
    /**
     * Fetches the password data for the specified EC2 instance asynchronously.
     *
     * @param ec2AsyncClient the EC2 asynchronous client to use for the request
     * @param instanceId the ID of the EC2 instance to fetch the password data for
     * @return a {@link CompletableFuture} that completes when the password data has been fetched
     * @throws RuntimeException if there was a failure in fetching the password data
     */
    public static CompletableFuture<Void> getPasswordDataAsync(Ec2AsyncClient ec2AsyncClient, String instanceId) {
        GetPasswordDataRequest getPasswordDataRequest = GetPasswordDataRequest.builder()
            .instanceId(instanceId)
            .build();

        // Fetch password data asynchronously.
        CompletableFuture<GetPasswordDataResponse> response = ec2AsyncClient.getPasswordData(getPasswordDataRequest);
        response.whenComplete((getPasswordDataResponse, ex) -> {
            if (getPasswordDataResponse != null) {
                String encryptedPasswordData = getPasswordDataResponse.passwordData();
                System.out.println("Encrypted Password Data: " + encryptedPasswordData);
            } else {
                throw new RuntimeException("Failed to get password data for instance: " + instanceId, ex);
            }
        });

        // Return CompletableFuture<Void> to signify the async operation's completion.
        return response.thenApply(resp -> null);
    }
}
// snippet-end:[ec2.java2.get_password.main]