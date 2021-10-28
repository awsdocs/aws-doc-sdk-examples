//snippet-sourcedescription:[DeletePipeline.java demonstrates how to delete a pipeline.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS CodePipeline]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/19/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pipeline;

// snippet-start:[pipeline.java2.delete_pipeline.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codepipeline.CodePipelineClient;
import software.amazon.awssdk.services.codepipeline.model.CodePipelineException;
import software.amazon.awssdk.services.codepipeline.model.DeletePipelineRequest;
// snippet-end:[pipeline.java2.delete_pipeline.import]


/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeletePipeline {


    public static void main(String[] args) {
    final String USAGE = "\n" +
            "Usage: " +
            "   <name>\n\n" +
            "Where:\n" +
            "   name - the name of the pipeline to delete \n\n" ;

    if (args.length != 1) {
         System.out.println(USAGE);
         System.exit(1);
    }

    String name = args[0];
    Region region = Region.US_EAST_1;
    CodePipelineClient pipelineClient = CodePipelineClient.builder()
            .region(region)
            .build();

   deleteSpecificPipeline(pipelineClient, name);
   pipelineClient.close();
}

 // snippet-start:[pipeline.java2.delete_pipeline.main]
 public static void deleteSpecificPipeline(CodePipelineClient pipelineClient, String name) {

        try {

            DeletePipelineRequest deletePipelineRequest = DeletePipelineRequest.builder()
                    .name(name)
                    .build() ;

            pipelineClient.deletePipeline(deletePipelineRequest);
            System.out.println(name +" was successfully deleted");

        } catch (CodePipelineException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[pipeline.java2.delete_pipeline.main]
}


