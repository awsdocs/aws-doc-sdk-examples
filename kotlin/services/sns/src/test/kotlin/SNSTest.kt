// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.sns.addTopicTags
import com.kotlin.sns.createSNSTopic
import com.kotlin.sns.deleteSNSTopic
import com.kotlin.sns.listSNSSubscriptions
import com.kotlin.sns.listSNSTopics
import com.kotlin.sns.listTopicTags
import com.kotlin.sns.pubTopic
import com.kotlin.sns.removeTag
import com.kotlin.sns.setTopAttr
import com.kotlin.sns.subEmail
import com.kotlin.sns.subLambda
import com.kotlin.sns.subTextSNS
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class SNSTest {
    private var topicName = ""
    private var topicArn = "" // This value is dynamically set
    private var subArn = "" // This value is dynamically set
    private var attributeName = ""
    private var attributeValue = ""
    private var email = ""
    private var lambdaarn = ""
    private var phone = ""
    private var message = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val random = Random()
            val randomNum = random.nextInt(10000 - 1 + 1) + 1
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            topicName = values.topicName.toString() + randomNum
            attributeName = values.attributeName.toString()
            attributeValue = values.attributeValue.toString()
            email = values.email.toString()
            lambdaarn = values.lambdaarn.toString()
            phone = values.phone.toString()
            message = values.message.toString()

        /*
        // load the properties file.
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        topicName = prop.getProperty("topicName")
        attributeName = prop.getProperty("attributeName")
        attributeValue = prop.getProperty("attributeValue")
        email = prop.getProperty("email")
        lambdaarn = prop.getProperty("lambdaarn")
        phone = prop.getProperty("phone")
        message = prop.getProperty("message")
        existingsubarn = prop.getProperty("existingsubarn")
         */
        }

    @Test
    @Order(1)
    fun createTopicTest() =
        runBlocking {
            topicArn = createSNSTopic(topicName)
            Assertions.assertTrue(!topicArn.isEmpty())
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun listTopicsTest() =
        runBlocking {
            listSNSTopics()
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun setTopicAttributesTest() =
        runBlocking {
            setTopAttr(attributeName, topicArn, attributeValue)
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun subscribeEmailTest() =
        runBlocking {
            subEmail(topicArn, email)
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun subscribeLambdaTest() =
        runBlocking {
            subLambda(topicArn, lambdaarn)
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun addTagsTest() =
        runBlocking {
            addTopicTags(topicArn)
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun listTagsTest() =
        runBlocking {
            listTopicTags(topicArn)
            println("Test 7 passed")
        }

    @Test
    @Order(8)
    fun deleteTagTest() =
        runBlocking {
            removeTag(topicArn, "Team")
            println("Test 8 passed")
        }

    @Test
    @Order(10)
    fun subEmailTest() =
        runBlocking {
            subEmail(topicArn, email)
            println("Test 10 passed")
        }

    @Test
    @Order(11)
    fun pubTopicTest() =
        runBlocking {
            pubTopic(topicArn, message)
            println("Test 11 passed")
        }

    @Test
    @Order(12)
    fun listSubsTest() =
        runBlocking {
            listSNSSubscriptions()
            println("Test 12 passed")
        }

    @Test
    @Order(13)
    fun subscribeTextSMSTest() =
        runBlocking {
            subTextSNS(topicArn, phone)
            println("Test 14 passed")
        }

    @Test
    @Order(15)
    fun deleteTopicTest() =
        runBlocking {
            deleteSNSTopic(topicArn)
            println("Test 15 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/sns"
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
    @DisplayName("A class used to get test values from test/sns, an AWS Secrets Manager secret")
    internal class SecretValues {
        val topicName: String? = null
        val attributeName: String? = null
        val attributeValue: String? = null
        val lambdaarn: String? = null
        val phone: String? = null
        val message: String? = null
        val email: String? = null
    }
}
