//snippet-sourcedescription:[CreatePipeline.java demonstrates how to create a pipeline.]
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

// snippet-start:[pipeline.java2.create_pipeline.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codepipeline.CodePipelineClient;
import software.amazon.awssdk.services.codepipeline.model.CreatePipelineRequest;
import software.amazon.awssdk.services.codepipeline.model.ActionTypeId;
import software.amazon.awssdk.services.codepipeline.model.OutputArtifact;
import software.amazon.awssdk.services.codepipeline.model.InputArtifact;
import software.amazon.awssdk.services.codepipeline.model.ActionDeclaration;
import software.amazon.awssdk.services.codepipeline.model.StageDeclaration;
import software.amazon.awssdk.services.codepipeline.model.ArtifactStore;
import software.amazon.awssdk.services.codepipeline.model.PipelineDeclaration;
import software.amazon.awssdk.services.codepipeline.model.CreatePipelineResponse;
import software.amazon.awssdk.services.codepipeline.model.CodePipelineException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// snippet-end:[pipeline.java2.create_pipeline.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreatePipeline {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <name> <roleArn> <s3Bucket> <s3OuputBucket>\n\n" +
                "Where:\n" +
                "   name - The name of the pipeline to create. \n\n" +
                "   roleArn - The Amazon Resource Name (ARN) for AWS CodePipeline to use.  \n\n"+
                "   s3Bucket - The name of the Amazon S3 bucket where the code is located.  \n\n"+
                "   s3OuputBucket - The name of the Amazon S3 bucket where the code is deployed.  \n\n";

         if (args.length != 4) {
             System.out.println(usage);
             System.exit(1);
        }

        String name = args[0] ;
        String roleArn = args[1];
        String s3Bucket = args[2];
        String s3OuputBucket = args[3] ;
        Region region = Region.US_EAST_1;
        CodePipelineClient pipelineClient = CodePipelineClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        createNewPipeline(pipelineClient, name, roleArn, s3Bucket, s3OuputBucket);
        pipelineClient.close();
    }

    // snippet-start:[pipeline.java2.create_pipeline.main]
    public static void createNewPipeline(CodePipelineClient pipelineClient, String name, String roleArn, String s3Bucket, String s3OuputBucket) {

        try {
            ActionTypeId actionTypeSource = ActionTypeId.builder()
                    .category("Source")
                    .owner("AWS")
                    .provider("S3")
                    .version("1")
                    .build();

            // Set Config information
            Map<String,String> mapConfig = new HashMap<>();
            mapConfig.put("PollForSourceChanges","false");
            mapConfig.put("S3Bucket",s3Bucket);
            mapConfig.put("S3ObjectKey","SampleApp_Windows.zip");

            OutputArtifact outputArtifact = OutputArtifact.builder()
                    .name("SourceArtifact")
                    .build();

            ActionDeclaration actionDeclarationSource = ActionDeclaration.builder()
                    .actionTypeId(actionTypeSource)
                    .region("us-east-1")
                    .configuration(mapConfig)
                    .runOrder(1)
                    .outputArtifacts(outputArtifact)
                    .name("Source")
                    .build();

            // Set Config information.
            Map<String,String> mapConfig1 = new HashMap<>();
            mapConfig1.put("BucketName",s3OuputBucket);
            mapConfig1.put("ObjectKey","SampleApp.zip");
            mapConfig1.put("Extract","false");

            ActionTypeId actionTypeDeploy = ActionTypeId.builder()
                    .category("Deploy")
                    .owner("AWS")
                    .provider("S3")
                    .version("1")
                    .build();

            InputArtifact inArtifact = InputArtifact.builder()
                    .name("SourceArtifact")
                    .build();

            ActionDeclaration actionDeclarationDeploy = ActionDeclaration.builder()
                    .actionTypeId(actionTypeDeploy)
                    .region("us-east-1")
                    .configuration(mapConfig1)
                    .inputArtifacts(inArtifact)
                    .runOrder(1)
                    .name("Deploy")
                    .build();

            StageDeclaration declaration = StageDeclaration.builder()
                    .actions(actionDeclarationSource)
                    .name("Stage")
                    .build();

            StageDeclaration deploy = StageDeclaration.builder()
                    .actions(actionDeclarationDeploy)
                    .name("Deploy")
                    .build();

            List<StageDeclaration> stages = new ArrayList<>();
            stages.add(declaration);
            stages.add(deploy);

            ArtifactStore store = ArtifactStore.builder()
                    .location(s3Bucket)
                    .type("S3")
                    .build();

            PipelineDeclaration pipelineDeclaration = PipelineDeclaration.builder()
                    .name(name)
                    .artifactStore(store)
                    .roleArn(roleArn)
                    .stages(stages)
                    .build();

            CreatePipelineRequest pipelineRequest = CreatePipelineRequest.builder()
                    .pipeline(pipelineDeclaration)
                    .build();

            CreatePipelineResponse response = pipelineClient.createPipeline(pipelineRequest);
            System.out.println("Pipeline "+response.pipeline().name() +" was successfully created");

        } catch (CodePipelineException e) {
            System.err.println(e.getMessage());
            System.exit(1);
       }
  }
    // snippet-end:[pipeline.java2.create_pipeline.main]
}
