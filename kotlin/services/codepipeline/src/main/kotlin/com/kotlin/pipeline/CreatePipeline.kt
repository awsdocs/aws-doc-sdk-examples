// snippet-sourcedescription:[CreatePipeline.kt demonstrates how to create a pipeline.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS CodePipeline]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.pipeline

// snippet-start:[pipeline.kotlin.create_pipeline.import]
import aws.sdk.kotlin.services.codepipeline.CodePipelineClient
import aws.sdk.kotlin.services.codepipeline.model.ActionCategory
import aws.sdk.kotlin.services.codepipeline.model.ActionDeclaration
import aws.sdk.kotlin.services.codepipeline.model.ActionOwner
import aws.sdk.kotlin.services.codepipeline.model.ActionTypeId
import aws.sdk.kotlin.services.codepipeline.model.ArtifactStore
import aws.sdk.kotlin.services.codepipeline.model.ArtifactStoreType
import aws.sdk.kotlin.services.codepipeline.model.CreatePipelineRequest
import aws.sdk.kotlin.services.codepipeline.model.InputArtifact
import aws.sdk.kotlin.services.codepipeline.model.OutputArtifact
import aws.sdk.kotlin.services.codepipeline.model.PipelineDeclaration
import aws.sdk.kotlin.services.codepipeline.model.StageDeclaration
import kotlin.system.exitProcess
// snippet-end:[pipeline.kotlin.create_pipeline.import]

suspend fun main(args: Array<String>) {

    val usage = """
        Usage: 
            <name> <roleArn> <s3Bucket> <s3OuputBucket>

        Where:
            name - the name of the pipeline to create. 
            roleArn - the Amazon Resource Name (ARN) for AWS CodePipeline to use.  
            s3Bucket - the name of the Amazon S3 bucket where the code is located.  
            s3OuputBucket - the name of the Amazon S3 bucket where the code is deployed.  
        """

    if (args.size != 4) {
        println(usage)
        exitProcess(1)
    }

    val name = args[0]
    val roleArn = args[1]
    val s3Bucket = args[2]
    val s3OuputBucket = args[3]
    createNewPipeline(name, roleArn, s3Bucket, s3OuputBucket)
}

// snippet-start:[pipeline.kotlin.create_pipeline.main]
suspend fun createNewPipeline(
    nameVal: String,
    roleArnVal: String,
    s3Bucket: String,
    s3OuputBucket: String
) {

    val actionTypeSource = ActionTypeId {
        category = ActionCategory.fromValue("Source")
        owner = ActionOwner.fromValue("AWS")
        provider = "S3"
        version = "1"
    }

    // Set Config information.
    val mapConfig: MutableMap<String, String> = HashMap()
    mapConfig["PollForSourceChanges"] = "false"
    mapConfig["S3Bucket"] = s3Bucket
    mapConfig["S3ObjectKey"] = "SampleApp_Windows.zip"

    val outputArtifact = OutputArtifact {
        name = "SourceArtifact"
    }

    val actionDeclarationSource = ActionDeclaration {
        actionTypeId = actionTypeSource
        region = "us-east-1"
        configuration = mapConfig
        runOrder = 1
        outputArtifacts = listOf(outputArtifact)
        name = "Source"
    }

    // Set Config information.
    val mapConfig1: MutableMap<String, String> = HashMap()
    mapConfig1["BucketName"] = s3OuputBucket
    mapConfig1["ObjectKey"] = "SampleApp.zip"
    mapConfig1["Extract"] = "false"

    val actionTypeDeploy = ActionTypeId {
        category = ActionCategory.fromValue("Deploy")
        owner = ActionOwner.fromValue("AWS")
        provider = "S3"
        version = "1"
    }

    val inArtifact = InputArtifact {
        name = "SourceArtifact"
    }

    val actionDeclarationDeploy = ActionDeclaration {
        actionTypeId = actionTypeDeploy
        region = "us-east-1"
        configuration = mapConfig1
        inputArtifacts = listOf(inArtifact)
        runOrder = 1
        name = "Deploy"
    }

    val declaration = StageDeclaration {
        actions = listOf(actionDeclarationSource)
        name = "Stage"
    }

    val deploy = StageDeclaration {
        actions = listOf(actionDeclarationDeploy)
        name = "Deploy"
    }

    val stagesOb = mutableListOf<StageDeclaration>()
    stagesOb.add(declaration)
    stagesOb.add(deploy)

    val store = ArtifactStore {
        location = s3Bucket
        type = ArtifactStoreType.fromValue("S3")
    }

    val pipelineDeclaration = PipelineDeclaration {
        name = nameVal
        artifactStore = store
        roleArn = roleArnVal
        stages = stagesOb
    }

    val request = CreatePipelineRequest {
        pipeline = pipelineDeclaration
    }

    CodePipelineClient { region = "us-east-1" }.use { pipelineClient ->
        val response = pipelineClient.createPipeline(request)
        println("Pipeline ${response.pipeline?.name} was successfully created")
    }
}
// snippet-end:[pipeline.kotlin.create_pipeline.main]
