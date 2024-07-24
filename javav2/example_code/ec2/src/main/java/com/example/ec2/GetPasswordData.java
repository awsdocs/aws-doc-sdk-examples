// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.ec2;

// snippet-start:[ec2.java2.get_password.main]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

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
        Region region = Region.US_EAST_1;
        Ec2Client ec2 = Ec2Client.builder()
            .region(region)
            .build();

        String instanceId = args[0];
        getPasswordData(ec2,instanceId);

    }
    /**
     * Retrieves the encrypted administrator password for a Windows-based Amazon EC2 instance.
     *
     * @param ec2 The Ec2Client instance used to interact with the Amazon EC2 service.
     * @param instanceId The ID of the Amazon EC2 instance from which to retrieve the encrypted password data.
     *
     * @throws Ec2Exception If an error occurs while retrieving the password data.
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
           System.err.println(e.awsErrorDetails().errorMessage());
           System.exit(1);
        }
    }
 }
// snippet-end:[ec2.java2.get_password.main]