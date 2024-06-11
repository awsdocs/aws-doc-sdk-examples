// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.example.autoscaling.createAutoScalingGroup
import com.example.autoscaling.deleteSpecificAutoScalingGroup
import com.example.autoscaling.describeAccountLimits
import com.example.autoscaling.describeAutoScalingGroups
import com.example.autoscaling.describeAutoScalingInstance
import com.example.autoscaling.describeScalingActivities
import com.example.autoscaling.disableMetricsCollection
import com.example.autoscaling.enableMetricsCollection
import com.example.autoscaling.getAutoScalingGroups
import com.example.autoscaling.getSpecificAutoScaling
import com.example.autoscaling.setDesiredCapacity
import com.example.autoscaling.terminateInstanceInAutoScalingGroup
import com.example.autoscaling.updateAutoScalingGroup
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.IOException
import java.util.Random
import kotlin.system.exitProcess

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class AutoScalingTest {
    private var groupName = ""
    private var groupNameSc = ""
    private var launchTemplateName = ""
    private var vpcZoneId = ""
    private var serviceLinkedRoleARN = ""

    @BeforeAll
    @Throws(IOException::class)
    fun setUp() =
        runBlocking {
            val random = Random()
            val randomNum = random.nextInt(10000 - 1 + 1) + 1
            // Get the values from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            groupName = values.groupName.toString() + randomNum
            launchTemplateName = values.launchTemplateName.toString()
            vpcZoneId = values.vpcZoneId.toString()
            serviceLinkedRoleARN = values.serviceLinkedRoleARN.toString()
            groupNameSc = values.groupNameSc.toString() + randomNum
            // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
        /*
        try {
            AutoScalingTest::class.java.getClassLoader().getResourceAsStream("config.properties").use { input ->
                val prop = Properties()
                if (input == null) {
                    println("Sorry, unable to find config.properties")
                    return
                }
                prop.load(input)
                groupName = prop.getProperty("groupName")
                launchTemplateName = prop.getProperty("launchTemplateName")
                subnetId = prop.getProperty("subnetId")
                vpcZoneId = "subnet-0ddc451b8a8a1aa44"  //prop.getProperty("vpcZoneId")
            }
        } catch (ex:IOException) {
            ex.printStackTrace()
        }
         */
        }

    @Test
    @Order(1)
    fun testScenario() =
        runBlocking {
            println("**** Create an Auto Scaling group named $groupName")
            createAutoScalingGroup(groupName, launchTemplateName, serviceLinkedRoleARN, vpcZoneId)

            println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned")
            delay(60000)

            val instanceId = getSpecificAutoScaling(groupName)
            if (instanceId.compareTo("") == 0) {
                println("Error - no instance Id value")
                exitProcess(1)
            } else {
                println("The instance Id value is $instanceId")
            }

            println("**** Describe Auto Scaling with the Id value $instanceId")
            describeAutoScalingInstance(instanceId)

            println("**** Enable metrics collection $instanceId")
            enableMetricsCollection(groupName)

            println("**** Update an Auto Scaling group to update max size to 3")
            updateAutoScalingGroup(groupName, launchTemplateName, serviceLinkedRoleARN)

            println("**** Describe all Auto Scaling groups to show the current state of the groups")
            describeAutoScalingGroups(groupName)

            println("**** Describe account details")
            describeAccountLimits()

            println("Wait 1 min for the resources, including the instance. Otherwise, an empty instance Id is returned")
            delay(60000)

            println("**** Set desired capacity to 2")
            setDesiredCapacity(groupName)

            println("**** Get the two instance Id values and state")
            getAutoScalingGroups(groupName)

            println("**** List the scaling activities that have occurred for the group")
            describeScalingActivities(groupName)

            println("**** Terminate an instance in the Auto Scaling group")
            terminateInstanceInAutoScalingGroup(instanceId)

            println("**** Stop the metrics collection")
            disableMetricsCollection(groupName)

            println("**** Delete the Auto Scaling group")
            deleteSpecificAutoScalingGroup(groupName)
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/autoscale"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
            credentialsProvider = EnvironmentCredentialsProvider()
        }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/autoscale (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val groupName: String? = null
        val groupNameSc: String? = null
        val launchTemplateName: String? = null
        val vpcZoneId: String? = null
        val serviceLinkedRoleARN: String? = null
    }
}
