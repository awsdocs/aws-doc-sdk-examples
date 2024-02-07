// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.apigateway.ApiGatewayClient
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.gateway.createAPI
import com.kotlin.gateway.deleteAPI
import com.kotlin.gateway.getAllDeployments
import com.kotlin.gateway.getAllStages
import kotlinx.coroutines.runBlocking
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
class APIGatewayTest {
    lateinit var apiGatewayClient: ApiGatewayClient
    private var restApiId = ""
    private var httpMethod = ""
    private var restApiName = ""
    private var stageName = ""
    private var newApiId = ""

    @BeforeAll
    fun setup() = runBlocking {
        apiGatewayClient = ApiGatewayClient { region = "us-east-1" }
        // Get the values from AWS Secrets Manager.
        val random = Random()
        val randomNum = random.nextInt(10000 - 1 + 1) + 1
        val gson = Gson()
        val json: String = getSecretValues()
        val values: SecretValues = gson.fromJson(json, SecretValues::class.java)
        restApiId = values.restApiId.toString()
        httpMethod = values.httpMethod.toString()
        restApiName = values.restApiName.toString() + randomNum
        stageName = values.stageName.toString()

       /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
        prop.load(input)

        // Populate the data members required for all tests
        restApiId = prop.getProperty("restApiId")
        resourceId = prop.getProperty("resourceId")
        httpMethod = prop.getProperty("httpMethod")
        restApiName = prop.getProperty("restApiName")
        stageName = prop.getProperty("stageName")
       */
    }

    @Test
    @Order(1)
    fun createRestApiTest() = runBlocking {
        newApiId = createAPI(restApiId).toString()
        println("Test 2 passed")
    }

    @Test
    @Order(2)
    fun getDeploymentsTest() = runBlocking {
        getAllDeployments(newApiId)
        println("Test 4 passed")
    }

    @Test
    @Order(3)
    fun getAllStagesTest() = runBlocking {
        getAllStages(newApiId)
        println("Test 5 passed")
    }

    @Test
    @Order(4)
    fun DeleteRestApi() = runBlocking {
        deleteAPI(newApiId)
        println("Test 6 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretName = "test/apigateway"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        SecretsManagerClient { region = "us-east-1"; credentialsProvider = EnvironmentCredentialsProvider() }.use { secretClient ->
            val valueResponse = secretClient.getSecretValue(valueRequest)
            return valueResponse.secretString.toString()
        }
    }

    @Nested
    @DisplayName("A class used to get test values from test/apigateway (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val restApiId: String? = null
        val restApiName: String? = null
        val httpMethod: String? = null
        val stageName: String? = null
    }
}
