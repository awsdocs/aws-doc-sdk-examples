//snippet-sourcedescription:[PutBot.java demonstrates how to creates an Amazon Lex conversational bot.]
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

// snippet-start:[lex.java2.create_bot.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.services.lexmodelbuilding.model.Intent;
import software.amazon.awssdk.services.lexmodelbuilding.model.Message;
import software.amazon.awssdk.services.lexmodelbuilding.model.ContentType;
import software.amazon.awssdk.services.lexmodelbuilding.model.Statement;
import software.amazon.awssdk.services.lexmodelbuilding.model.PutBotRequest;
import software.amazon.awssdk.services.lexmodelbuilding.model.LexModelBuildingException;
import java.util.ArrayList;
// snippet-end:[lex.java2.create_bot.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutBot {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <botName> <intentName> <intentVersion> \n\n" +
                "Where:\n" +
                "   botName - The name of bot (for example, BookHotel).\n\n" +
                "   intentName - The name of an existing intent (for example, BookHotel).\n\n" +
                "   intentVersion - The version of the intent (for example, 1).\n\n";

       if (args.length != 3) {
             System.out.println(usage);
             System.exit(1);
        }

        String botName = args[0];
        String intentName = args[1];
        String intentVersion = args[2];
        Region region = Region.US_WEST_2;
        LexModelBuildingClient lexClient = LexModelBuildingClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        createBot(lexClient, botName, intentName, intentVersion);
        lexClient.close();
    }

    // snippet-start:[lex.java2.create_bot.main]
    public static void createBot( LexModelBuildingClient lexClient,
                                  String botName,
                                  String intentName,
                                  String intentVersion) {

        try {
            // Create an Intent object for the bot.
            Intent weatherIntent = Intent.builder()
                    .intentName(intentName)
                    .intentVersion(intentVersion)
                    .build();

            ArrayList<Intent> intents = new ArrayList<>();
            intents.add(weatherIntent);

            // Create an abort statement.
            Message msg = Message.builder()
                    .content("I do not understand you!")
                    .contentType(ContentType.PLAIN_TEXT )
                    .build();

            ArrayList<Message> abortMsg = new ArrayList<>();
            abortMsg.add(msg);

            Statement statement = Statement.builder()
                    .messages(abortMsg)
                    .build();

            PutBotRequest botRequest = PutBotRequest.builder()
                    .abortStatement(statement)
                    .description("Created by using the Amazon Lex Java API")
                    .childDirected(true)
                    .locale("en-US")
                    .name(botName)
                    .intents(intents)
                    .build();

            lexClient.putBot(botRequest);
            System.out.println("The Amazon Lex bot was successfully created");

        } catch (LexModelBuildingException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[lex.java2.create_bot.main]
}