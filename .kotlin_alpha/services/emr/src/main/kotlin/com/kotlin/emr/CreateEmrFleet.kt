//snippet-sourcedescription:[CreateEmrFleet.kt demonstrates how to create a cluster using instance fleet with spot instances.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EMR]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/19/2021]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.emr

//snippet-start:[emr.kotlin.create_fleet.import]
import aws.sdk.kotlin.services.emr.EmrClient
import aws.sdk.kotlin.services.emr.model.InstanceTypeConfig
import aws.sdk.kotlin.services.emr.model.InstanceFleetConfig
import aws.sdk.kotlin.services.emr.model.InstanceFleetType
import aws.sdk.kotlin.services.emr.model.JobFlowInstancesConfig
import aws.sdk.kotlin.services.emr.model.RunJobFlowRequest
import aws.sdk.kotlin.services.emr.model.EmrException
import aws.sdk.kotlin.services.emr.model.Application
import kotlin.system.exitProcess
//snippet-end:[emr.kotlin.create_fleet.import]

/*
* To run the CreateEmrFleet example, it is recommended that you go through the following document:
*
* https://docs.aws.amazon.com/emr/latest/ManagementGuide/emr-gs.html
*
*/

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val emrClient = EmrClient { region = "us-west-2" }
    createFleet(emrClient)
    emrClient.close()
}

//snippet-start:[emr.kotlin.create_fleet.main]
suspend fun createFleet(emr: EmrClient) {
    try {

        // Instance Types
        // M Family
        val m3xLarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType = "m3.xlarge"
            weightedCapacity = 1
        }


        val m4xLarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType ="m4.xlarge"
            weightedCapacity = 1
        }

        val m5xLarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType = "m5.xlarge"
            weightedCapacity = 1
        }

        // R Family
        val r5xlarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType = "r5.xlarge"
            weightedCapacity = 2
        }

        val r4xlarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType = "r4.xlarge"
            weightedCapacity = 2
        }

        val r3xlarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType = "r3.xlarge"
            weightedCapacity = 2
        }

        // C Family
        val c32xlarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType = "c3.2xlarge"
            weightedCapacity = 4
        }

        val c42xlarge = InstanceTypeConfig {
            bidPriceAsPercentageOfOnDemandPrice = 100.0
            instanceType = "c4.2xlarge"
            weightedCapacity = 4
        }

        // Master
        val masterFleet = InstanceFleetConfig {
            name = "master-fleet"
            instanceFleetType = InstanceFleetType.Master
            instanceTypeConfigs = listOf(m3xLarge, m4xLarge, m5xLarge)
            targetOnDemandCapacity = 1
        }

        // Core
        val coreFleet = InstanceFleetConfig {
            name = "core-fleet"
            instanceFleetType = InstanceFleetType.Core
            instanceTypeConfigs = listOf( m3xLarge, m4xLarge, r4xlarge, r3xlarge, c32xlarge)
            targetOnDemandCapacity = 20
            targetSpotCapacity = 10
        }

        // Task
        val taskFleet = InstanceFleetConfig {
            name = "task-fleet"
            instanceFleetType = InstanceFleetType.Task
            instanceTypeConfigs = listOf(m4xLarge, r5xlarge, r4xlarge, c32xlarge, c42xlarge)
            targetOnDemandCapacity = 8
            targetSpotCapacity = 40
        }

        val flowInstancesConfig = JobFlowInstancesConfig {
            ec2KeyName = "scottkeys"
            keepJobFlowAliveWhenNoSteps = true
            instanceFleets = listOf(masterFleet, coreFleet, taskFleet)
            ec2SubnetId = "subnet-cca64baa"
        }

        val flowRequest = RunJobFlowRequest {
            name = "emr-spot-example"
            instances = flowInstancesConfig
            serviceRole = "EMR_DefaultRole"
            jobFlowRole = "EMR_EC2_DefaultRole"
            visibleToAllUsers = true
            applications = listOf( Application{name = "Spark"})
            releaseLabel= "emr-5.29.0"
        }
        val response = emr.runJobFlow(flowRequest)
        println(response.toString())

    } catch (e: EmrException) {
        println(e.message)
        exitProcess(0)
    }
}
//snippet-end:[emr.kotlin.create_fleet.main]
