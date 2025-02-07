// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.ec2.createEC2Instance
import com.kotlin.ec2.createEC2KeyPair
import com.kotlin.ec2.createEC2SecurityGroup
import com.kotlin.ec2.deleteEC2SecGroup
import com.kotlin.ec2.deleteKeys
import com.kotlin.ec2.describeEC2Account
import com.kotlin.ec2.describeEC2Address
import com.kotlin.ec2.describeEC2Instances
import com.kotlin.ec2.describeEC2Keys
import com.kotlin.ec2.describeEC2RegionsAndZones
import com.kotlin.ec2.describeEC2SecurityGroups
import com.kotlin.ec2.describeEC2Vpcs
import com.kotlin.ec2.findRunningEC2Instances
import com.kotlin.ec2.terminateEC2
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class EC2Test {
    private val logger: Logger = LoggerFactory.getLogger(EC2Test::class.java)
    private var instanceId = "" // Gets set in test 2.
    private var ami = ""
    private var instanceName = ""
    private var keyName = ""
    private var groupName = ""
    private var groupDesc = ""
    private var groupId = ""
    private var vpcId = ""

    private var keyNameSc = ""
    private var fileNameSc = ""
    private var groupNameSc = ""
    private var groupDescSc = ""
    private var vpcIdSc = ""
    private var myIpAddressSc = ""

    @BeforeAll
    @Throws(IOException::class)
    fun setUp() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val random = Random()
            val randomNum: Int = random.nextInt(10000 - 1 + 1) + 1
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            ami = values.ami.toString()
            instanceName = values.instanceName.toString()
            keyName = values.keyNameSc.toString()
            groupName = values.groupName.toString() + randomNum
            groupDesc = values.groupDesc.toString()
            vpcId = values.vpcId.toString()
            keyNameSc = values.keyNameSc.toString() + randomNum
            fileNameSc = values.fileNameSc.toString()
            groupDescSc = values.groupDescSc.toString()
            groupNameSc = values.groupNameSc.toString() + randomNum
            vpcIdSc = values.vpcIdSc.toString()
            myIpAddressSc = values.myIpAddressSc.toString()
        }

    @Test
    @Order(1)
    fun createInstanceTest() =
        runBlocking {
            instanceId = createEC2Instance(instanceName, ami).toString()
            assertTrue(instanceId.isNotEmpty())
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createKeyPairTest() =
        runBlocking {
            createEC2KeyPair(keyName)
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun describeKeyPairTest() =
        runBlocking {
            describeEC2Keys()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun deleteKeyPairTest() =
        runBlocking {
            deleteKeys(keyName)
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun createSecurityGroupTest() =
        runBlocking {
            groupId = createEC2SecurityGroup(groupName, groupDesc, vpcId).toString()
            assertTrue(groupId.isNotEmpty())
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun describeSecurityGroupTest() =
        runBlocking {
            describeEC2SecurityGroups(groupId)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteSecurityGroupTest() =
        runBlocking {
            deleteEC2SecGroup(groupId)
            logger.info("Test 7 passed")
        }

    @Test
    @Order(8)
    fun describeAccountTest() =
        runBlocking {
            describeEC2Account()
            logger.info("Test 8 passed")
        }

    @Test
    @Order(9)
    fun describeInstancesTest() =
        runBlocking {
            describeEC2Instances()
            logger.info("Test 9 passed")
        }

    @Test
    @Order(10)
    fun describeRegionsAndZonesTest() =
        runBlocking {
            describeEC2RegionsAndZones()
            logger.info("Test 10 passed")
        }

    @Test
    @Order(11)
    fun describeVPCsTest() =
        runBlocking {
            describeEC2Vpcs(vpcId)
            logger.info("Test 11 passed")
        }

    @Test
    @Order(12)
    fun findRunningInstancesTest() =
        runBlocking {
            findRunningEC2Instances()
            logger.info("Test 12 passed")
        }

    @Test
    @Order(13)
    fun describeAddressesTest() =
        runBlocking {
            describeEC2Address()
            logger.info("Test 13 passed")
        }

    @Test
    @Order(14)
    fun terminateInstanceTest() =
        runBlocking {
            terminateEC2(instanceId)
            logger.info("Test 14 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/ec2"
        val valueRequest =
            GetSecretValueRequest {
                secretId = secretName
            }
        SecretsManagerClient {
            region = "us-east-1"
        }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/ec2 (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val ami: String? = null
        val instanceName: String? = null
        val keyPair: String? = null
        val groupName: String? = null
        val groupDesc: String? = null
        val vpcId: String? = null
        val keyNameSc: String? = null
        val fileNameSc: String? = null
        val groupNameSc: String? = null
        val groupDescSc: String? = null
        val vpcIdSc: String? = null
        val myIpAddressSc: String? = null
    }
}
