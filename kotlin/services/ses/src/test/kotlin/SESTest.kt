// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.ses.listSESIdentities
import com.kotlin.ses.send
import kotlinx.coroutines.runBlocking
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class SESTest {
    private val logger: Logger = LoggerFactory.getLogger(SESTest::class.java)
    private var sender = ""
    private var recipient = ""
    private var subject = ""
    private var fileLocation = ""

    private val bodyHTML = """
    <html>
        <head></head>
        <body>
            <h1>Hello!</h1>
            <p>Please see the attached file for a list of customers to contact.</p>
        </body>
    </html>
    """.trimIndent()

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            sender = values.sender.toString()
            recipient = values.recipient.toString()
            subject = values.subject.toString()
            fileLocation = values.fileLocation.toString()
        }

    @Test
    @Order(1)
    fun sendMessageTest() =
        runBlocking {
            send(sender, recipient, subject, bodyHTML)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun listIdentitiesTest() =
        runBlocking {
            listSESIdentities()
            logger.info("Test 2 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/ses"
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
    @DisplayName("A class used to get test values from test/ses (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val sender: String? = null
        val recipient: String? = null
        val subject: String? = null
        val fileLocation: String? = null
    }
}
