//snippet-sourcedescription:[CreateAlarm.kt demonstrates how to create an Amazon CloudWatch alarm.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon CloudWatch]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cloudwatch

// snippet-start:[cloudwatch.kotlin.create_alarm.import]
import aws.sdk.kotlin.services.cloudwatch.CloudWatchClient
import aws.sdk.kotlin.services.cloudwatch.model.Dimension
import aws.sdk.kotlin.services.cloudwatch.model.PutMetricAlarmRequest
import aws.sdk.kotlin.services.cloudwatch.model.ComparisonOperator
import aws.sdk.kotlin.services.cloudwatch.model.Statistic
import aws.sdk.kotlin.services.cloudwatch.model.StandardUnit
import aws.sdk.kotlin.services.cloudwatch.model.CloudWatchException
import kotlin.system.exitProcess
// snippet-end:[cloudwatch.kotlin.create_alarm.import]

suspend fun main(args:Array<String>) {

    val usage  = """

    Usage:
        <alarmName> <instanceId> 

    Where:
        alarmName - an alarm name to use.
        instanceId - an instance Id value .
    """

    if (args.size != 2) {
        println(usage)
        exitProcess(0)
    }

    val alarmName = args[0]
    val instanceId = args[1]
    val cwClient = CloudWatchClient{region="us-east-1"}
    putAlarm(cwClient, alarmName, instanceId)
    cwClient.close()
    }

    // snippet-start:[cloudwatch.kotlin.create_alarm.main]
    suspend fun putAlarm(cwClient: CloudWatchClient, alarmNameVal: String?, instanceIdVal: String?) {

        try {
            val dimension = Dimension {
                name  = "InstanceId"
                value = instanceIdVal
            }

            val request = PutMetricAlarmRequest {
                alarmName = alarmNameVal
                comparisonOperator = ComparisonOperator.GreaterThanThreshold
                evaluationPeriods = 1
                metricName = "CPUUtilization"
                namespace = "AWS/EC2"
                period = 60
                statistic = Statistic.fromValue("Average")
                threshold = 70.0
                actionsEnabled = false
                alarmDescription = "An Alarm created by the Kotlin SDK when server CPU utilization exceeds 70%"
                unit = StandardUnit.fromValue("Seconds")
                dimensions = listOf(dimension)
            }

            cwClient.putMetricAlarm(request)
            println("Successfully created an alarm with name $alarmNameVal")

        } catch (ex: CloudWatchException) {
            println(ex.message)
            cwClient.close()
            exitProcess(0)
        }
   }
// snippet-end:[cloudwatch.kotlin.create_alarm.main]