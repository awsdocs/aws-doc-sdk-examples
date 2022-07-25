//snippet-sourcedescription:[GetBots.java demonstrates how to return information about Amazon Lex chatbots.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Lex]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.lex;

// snippet-start:[lex.java2.get_bots.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.model.BotMetadata;
import software.amazon.awssdk.services.lexmodelbuilding.model.LexModelBuildingException;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetBotsResponse;
import java.util.List;
// snippet-end:[lex.java2.get_bots.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetBots {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        LexModelBuildingClient lexClient = LexModelBuildingClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        getAllBots(lexClient);
        lexClient.close();
    }

    // snippet-start:[lex.java2.get_bots.main]
    public static void getAllBots(LexModelBuildingClient lexClient) {
        try {
            GetBotsResponse response = lexClient.getBots();
            List<BotMetadata> bots = response.bots();
            for (BotMetadata bot: bots) {
                System.out.println("The bot name is : "+bot.name());
                System.out.println("The bot version is : "+bot.version());
            }

        } catch (LexModelBuildingException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[lex.java2.get_bots.main]
}
