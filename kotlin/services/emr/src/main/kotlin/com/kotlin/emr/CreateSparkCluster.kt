//snippet-sourcedescription:[CreateSparkCluster.kt demonstrates how to create and start running a new cluster (job flow).]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.emr

//snippet-start:[erm.kotlin.create_spark.import]
import aws.sdk.kotlin.services.emr.EmrClient
import aws.sdk.kotlin.services.emr.model.HadoopJarStepConfig
import aws.sdk.kotlin.services.emr.model.Application
import aws.sdk.kotlin.services.emr.model.StepConfig
import aws.sdk.kotlin.services.emr.model.ActionOnFailure
import aws.sdk.kotlin.services.emr.model.JobFlowInstancesConfig
import aws.sdk.kotlin.services.emr.model.RunJobFlowRequest
import kotlin.system.exitProcess
//snippet-end:[erm.kotlin.create_spark.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args:Array<String>) {

    val usage = """
        Usage:
            <jar> <myClass> <keys> <logUri> <name>

        Where:
            jar - a path to a JAR file run during the step. 
            myClass - the name of the main class in the specified Java file. 
            keys - The name of the EC2 key pair. 
            logUri - The Amazon S3 bucket where the logs are located (for example,  s3://<BucketName>/logs/). 
            name - The name of the job flow. 

        """

     if (args.size != 5) {
          println(usage)
          exitProcess(0)
      }

    val jar = args[0]
    val myClass = args[1]
    val keys = args[2]
    val logUri = args[3]
    val name = args[4]
    val jobFlowId: String = createSparkCluster(jar, myClass, keys, logUri, name).toString()
    println("The job flow id is $jobFlowId")
}

//snippet-start:[erm.kotlin.create_spark.main]
suspend fun createSparkCluster(
    jarVal: String?,
    myClass: String?,
    keysVal: String?,
    logUriVal: String?,
    nameVal: String?
): String? {

        val jarStepConfig = HadoopJarStepConfig {
            jar = jarVal
            mainClass = myClass
        }

        val app = Application {
            name = "Spark"
        }

        val enabledebugging = StepConfig {
            name = "Enable debugging"
            actionOnFailure = ActionOnFailure.fromValue("TERMINATE_JOB_FLOW")
            hadoopJarStep = jarStepConfig
        }

        val instancesConfig = JobFlowInstancesConfig {
            ec2SubnetId = "subnet-206a9c58"
            ec2KeyName = keysVal
            instanceCount = 3
            keepJobFlowAliveWhenNoSteps = true
            masterInstanceType = "m4.large"
            slaveInstanceType = "m4.large"
        }

        val request = RunJobFlowRequest {
            name = nameVal
            releaseLabel = "emr-5.20.0"
            steps = listOf(enabledebugging)
            applications = listOf(app)
            logUri = logUriVal
            serviceRole = "EMR_DefaultRole"
            jobFlowRole = "EMR_EC2_DefaultRole"
            instances = instancesConfig
        }

        EmrClient { region = "us-west-2" }.use { emrClient ->
            val response = emrClient.runJobFlow(request)
            return response.jobFlowId
        }
    }

//snippet-end:[erm.kotlin.create_spark.main]