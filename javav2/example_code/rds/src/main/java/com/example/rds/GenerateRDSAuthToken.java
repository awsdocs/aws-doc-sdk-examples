//snippet-sourcedescription:[CreateDBInstance.java demonstrates how to generate an authorization token for AWS Identity and Access Management (IAM) authentication to an Amazon Relational Database Service (Amazon RDS) database.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Relational Database Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.rds;

// snippet-start:[rds.java2.create_token.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.RdsUtilities;
import software.amazon.awssdk.services.rds.model.GenerateAuthenticationTokenRequest;
import software.amazon.awssdk.services.rds.model.RdsException;
// snippet-end:[rds.java2.create_token.import]

// snippet-start:[rds.java2.create_token.main]
public class GenerateRDSAuthToken {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <dbInstanceIdentifier> <masterUsername>\n\n" +
            "Where:\n" +
            "    dbInstanceIdentifier - The database instance identifier. \n" +
            "    masterUsername - The master user name. \n";

            if (args.length != 2) {
                System.out.println(usage);
                System.exit(1);
            }

            String dbInstanceIdentifier = args[0];
            String masterUsername = args[1];
            Region region = Region.US_WEST_2;
            RdsClient rdsClient = RdsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

            String token = getAuthToken(rdsClient, dbInstanceIdentifier, masterUsername);
            System.out.println("The token response is "+token);
    }

    public static String getAuthToken(RdsClient rdsClient, String dbInstanceIdentifier, String masterUsername ) {

        RdsUtilities utilities = rdsClient.utilities();
        try {
            GenerateAuthenticationTokenRequest tokenRequest = GenerateAuthenticationTokenRequest.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .username(masterUsername)
                .port(3306)
                .hostname(dbInstanceIdentifier)
                .build();

                return utilities.generateAuthenticationToken(tokenRequest);

        } catch (RdsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        return "";
   }
}
// snippet-end:[rds.java2.create_token.main]
