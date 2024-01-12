// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.lex;

// snippet-start:[lex.java2.get_intent.main]
// snippet-start:[lex.java2.get_intent.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.services.lexmodelbuilding.model.LexModelBuildingException;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetIntentRequest;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetIntentResponse;
// snippet-end:[lex.java2.get_intent.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetIntent {
    public static void main(String[] args) {
        final String usage = """

                Usage:    <intentName> <intentVersion>

                Where:
                   intentName - The name of the intent (for example, BookHotel).

                   intentVersion - The version of the intent (for example, 1).
                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String intentName = args[0];
        String intentVersion = args[1];
        Region region = Region.US_WEST_2;
        LexModelBuildingClient lexClient = LexModelBuildingClient.builder()
                .region(region)
                .build();

        getSpecificIntent(lexClient, intentName, intentVersion);
        lexClient.close();
    }

    public static void getSpecificIntent(LexModelBuildingClient lexClient, String intentName, String intentVersion) {
        try {
            GetIntentRequest intentRequest = GetIntentRequest.builder()
                    .name(intentName)
                    .version(intentVersion)
                    .build();

            GetIntentResponse intentResponse = lexClient.getIntent(intentRequest);
            System.out.println("The description is " + intentResponse.description());

        } catch (LexModelBuildingException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[lex.java2.get_intent.main]