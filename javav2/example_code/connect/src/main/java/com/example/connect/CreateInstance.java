//snippet-sourcedescription:[CreateInstance.java demonstrates how to create an Amazon Connect instance.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.CreateInstanceRequest ;
import software.amazon.awssdk.services.connect.model.CreateInstanceResponse;
import software.amazon.awssdk.services.connect.model.DirectoryType;
import software.amazon.awssdk.services.connect.model.ConnectException;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateInstance {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceAlias>\n\n" +
            "Where:\n" +
            "   instanceAlias - The name for your instance.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceAlias = args[0] ;
        Region region = Region.US_WEST_2;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        createConnectInstance(connectClient, instanceAlias);
    }

    // snippet-start:[connect.java2.create.main]
    public static String createConnectInstance(ConnectClient connectClient, String instanceAlias) {
        try {
            CreateInstanceRequest instanceRequest = CreateInstanceRequest.builder()
                .identityManagementType(DirectoryType.CONNECT_MANAGED)
                .instanceAlias(instanceAlias)
                .inboundCallsEnabled(true)
                .outboundCallsEnabled(true)
                .build();

            CreateInstanceResponse response = connectClient.createInstance(instanceRequest);
            System.out.println("The instance ARN is "+ response.arn());
            return response.id();

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        return "" ;
    }
    // snippet-end:[connect.java2.create.main]
}
