//snippet-sourcedescription:[DeleteBot.java demonstrates how to delete an Amazon Lex conversational bot.]
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

// snippet-start:[lex.java2.delete_bot.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.services.lexmodelbuilding.model.DeleteBotRequest;
import software.amazon.awssdk.services.lexmodelbuilding.model.LexModelBuildingException;
// snippet-end:[lex.java2.delete_bot.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteBot {
    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <botName>\n\n" +
                "Where:\n" +
                "   botName - The name of an existing bot to delete (for example, BookHotel).\n\n" ;

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

        deleteSpecificBot(lexClient, botName);
        lexClient.close();
    }

    // snippet-start:[lex.java2.delete_bot.main]
    public static void deleteSpecificBot( LexModelBuildingClient lexClient, String botName) {

        try {
            DeleteBotRequest botRequest = DeleteBotRequest.builder()
                    .name(botName)
                    .build();

            lexClient.deleteBot(botRequest);
            System.out.println(botName +" was deleted!");

        } catch (LexModelBuildingException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[lex.java2.delete_bot.main]
}