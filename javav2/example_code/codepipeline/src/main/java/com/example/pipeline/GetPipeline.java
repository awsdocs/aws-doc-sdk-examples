//snippet-sourcedescription:[GetPipeline.java demonstrates how to retrieve a specific pipeline.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS CodePipeline]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/17/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pipeline;

// snippet-start:[pipeline.java2.get_pipeline.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codepipeline.CodePipelineClient;
import software.amazon.awssdk.services.codepipeline.model.GetPipelineResponse;
import software.amazon.awssdk.services.codepipeline.model.GetPipelineRequest;
import software.amazon.awssdk.services.codepipeline.model.StageDeclaration;
import software.amazon.awssdk.services.codepipeline.model.ActionDeclaration;
import software.amazon.awssdk.services.codepipeline.model.CodePipelineException;
import java.util.List;
// snippet-end:[pipeline.java2.get_pipeline.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetPipeline {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <name> \n\n" +
                "Where:\n" +
                "   name - The name of the pipeline to retrieve \n\n" ;


        if (args.length != 1) {
             System.out.println(usage);
             System.exit(1);
        }

        String name = args[0];
        Region region = Region.US_EAST_1;
        CodePipelineClient pipelineClient = CodePipelineClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getSpecificPipeline(pipelineClient, name);
        pipelineClient.close();
    }

    // snippet-start:[pipeline.java2.get_pipeline.main]
    public static void getSpecificPipeline(CodePipelineClient pipelineClient, String name) {

        try {
            GetPipelineRequest pipelineRequest = GetPipelineRequest.builder()
                .name(name)
                .version(1)
                .build();

            GetPipelineResponse response = pipelineClient.getPipeline(pipelineRequest);
            List<StageDeclaration> stages = response.pipeline().stages();
            for (StageDeclaration stage: stages) {
                System.out.println("Stage name is " + stage.name() + " and actions are:");

                //Get the stage actions.
                List<ActionDeclaration> actions = stage.actions();
                for (ActionDeclaration action : actions) {
                    System.out.println("Action name is " + action.name());
                    System.out.println("Action type id is " + action.actionTypeId());
                }
            }

        } catch (CodePipelineException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
     }
    // snippet-end:[pipeline.java2.get_pipeline.main]
    }
