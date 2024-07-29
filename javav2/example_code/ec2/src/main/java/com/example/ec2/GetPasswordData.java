// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


package com.example.ec2;

// snippet-start:[ec2.java2.get_password.main]
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.GetPasswordDataRequest;
import software.amazon.awssdk.services.ec2.model.GetPasswordDataResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;

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
                   instanceId - An instance id from which the password is obtained.\s
             """;

        if (args.length != 1) {
            System.out.println(usage);
            return;
        }

        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
            .region(region)
            .build();

        String instanceId = args[0];
        getPasswordData(ec2,instanceId);
    }

    /**
     * Retrieves and prints the encrypted administrator password data for a specified EC2 instance.
     *
     * <p>The password data is encrypted using the key pair that was specified when the instance was launched.
     * To decrypt the password data, you can use the private key of the key pair.</p>
     *
     * @param ec2       The {@link Ec2Client} to use for making the request.
     * @param instanceId The ID of the instance for which to get the encrypted password data.
     */
     public static void getPasswordData(Ec2Client ec2,String instanceId) {
        GetPasswordDataRequest getPasswordDataRequest = GetPasswordDataRequest.builder()
            .instanceId(instanceId)
            .build();

        try {
            GetPasswordDataResponse getPasswordDataResponse = ec2.getPasswordData(getPasswordDataRequest);
            String encryptedPasswordData = getPasswordDataResponse.passwordData();
            System.out.println("Encrypted Password Data: " + encryptedPasswordData);

        } catch (Ec2Exception e) {
        String errorCode  = e.awsErrorDetails().errorCode();
        if (errorCode.matches("InvalidInstanceID.NotFound")) {
            System.err.println("Instance ID not found, unable to retrieve password data.");
        } else {
            System.err.println("There was a problem retrieving password data. Details:");
            e.printStackTrace();
        }
    }
    }
 }
// snippet-end:[ec2.java2.get_password.main]