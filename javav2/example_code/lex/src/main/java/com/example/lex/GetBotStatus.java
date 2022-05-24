//snippet-sourcedescription:[GetBotStatus.java demonstrates how to get the status of an Amazon Lex bot.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Lex]
//snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.lex;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetBotRequest;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetBotResponse;
import software.amazon.awssdk.services.lexmodelbuilding.model.LexModelBuildingException;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetBotStatus {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <botName> \n\n" +
                "Where:\n" +
                "   botName - The name of an existing bot (for example, BookHotel).\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String botName = args[0];
        Region region = Region.US_WEST_2;
        LexModelBuildingClient lexClient = LexModelBuildingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getStatus(lexClient, botName );
        lexClient.close();
    }

    public static void getStatus(LexModelBuildingClient lexClient, String botName ) {

        GetBotRequest botRequest = GetBotRequest.builder()
                .name(botName)
                .versionOrAlias("$LATEST")
                .build();
        try {

           String status = "";

           // Loop until the bot is in a ready status
           do {

                // Wait 5 secs
                Thread.sleep(5000);
                GetBotResponse response = lexClient.getBot(botRequest);
                status = response.statusAsString();
            } while (status.compareTo("READY") != 0);

        } catch (LexModelBuildingException | InterruptedException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
