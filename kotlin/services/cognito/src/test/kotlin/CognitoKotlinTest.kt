/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.cognito.createIdPool
import com.kotlin.cognito.createNewUser
import com.kotlin.cognito.createPool
import com.kotlin.cognito.delPool
import com.kotlin.cognito.deleteIdPool
import com.kotlin.cognito.describePool
import com.kotlin.cognito.getAllPools
import com.kotlin.cognito.getPools
import com.kotlin.cognito.listAllUserPoolClients
import com.kotlin.cognito.listPoolIdentities
import com.kotlin.cognito.signUp
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
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class CognitoKotlinTest {
    private var userPoolName = ""
    private var identityId = ""
    private var userPoolId = "" // set in test 2
    private var identityPoolId = "" // set in test
    private var username = ""
    private var email = ""
    private var clientName = ""
    private var identityPoolName = ""
    private var appId = ""
    private var existingUserPoolId = ""
    private var existingIdentityPoolId = ""
    private var providerName = ""
    private var existingPoolName = ""
    private var clientId = ""
    private var secretkey = ""
    private var password = ""
    private var poolIdMVP = ""
    private var clientIdMVP = ""
    private var userNameMVP = ""
    private var passwordMVP = ""
    private var emailMVP = ""

    @BeforeAll
    fun setup() = runBlocking {
        val gson = Gson()
        val json = getSecretValues()
        val values = gson.fromJson(json, SecretValues::class.java)
        userPoolName = values.userPoolName.toString()
        username = values.username + "_" + UUID.randomUUID()
        email = values.email.toString()
        clientName = values.clientName.toString()
        identityPoolName = values.identityPoolName.toString()
        identityId = values.identityId.toString()
        appId = values.appId.toString()
        existingUserPoolId = values.existingUserPoolId.toString()
        existingIdentityPoolId = values.existingIdentityPoolId.toString()
        providerName = values.providerName.toString()
        existingPoolName = values.existingPoolName.toString()
        clientId = values.clientId.toString()
        secretkey = values.secretkey.toString()
        password = values.password.toString()
        poolIdMVP = values.poolIdMVP.toString()
        clientIdMVP = values.clientIdMVP.toString()
        userNameMVP = values.userNameMVP.toString()
        passwordMVP = values.passwordMVP.toString()
        emailMVP = values.emailMVP.toString()

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
        /*
        // load the properties file.
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        userPoolName = prop.getProperty("userPoolName")
        identityId = prop.getProperty("identityId")
        username = prop.getProperty("username")
        email = prop.getProperty("email")
        clientName = prop.getProperty("clientName")
        identityPoolName = prop.getProperty("identityPoolName")
        appId = prop.getProperty("appId")
        existingUserPoolId = prop.getProperty("existingUserPoolId")
        existingIdentityPoolId = prop.getProperty("existingIdentityPoolId")
        providerName = prop.getProperty("providerName")
        existingPoolName = prop.getProperty("existingPoolName")
        clientId = prop.getProperty("clientId")
        secretkey = prop.getProperty("secretkey")
        password = prop.getProperty("password")
        poolIdMVP = prop.getProperty("poolIdMVP")
        clientIdMVP = prop.getProperty("clientIdMVP")
        userNameMVP = prop.getProperty("userNameMVP")
        passwordMVP = prop.getProperty("passwordMVP")
        emailMVP = prop.getProperty("emailMVP")

         */
    }

    @Test
    @Order(1)
    fun createUserPoolTest() = runBlocking {
        userPoolId = createPool(userPoolName).toString()
        Assertions.assertTrue(!userPoolId.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createAdminUserTest() = runBlocking {
        createNewUser(userPoolId, username, email, password)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun signUpUserTest() = runBlocking {
        signUp(clientId, secretkey, username, password, email)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun listUserPoolsTest() = runBlocking {
        getAllPools()
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun listUserPoolClientsTest() = runBlocking {
        listAllUserPoolClients(existingUserPoolId)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun listUsersTest() = runBlocking {
        listAllUserPoolClients(existingUserPoolId)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun describeUserPoolTest() = runBlocking {
        describePool(existingUserPoolId)
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun deleteUserPool() = runBlocking {
        delPool(userPoolId)
        println("Test 8 passed")
    }

    @Test
    @Order(9)
    fun createIdentityPoolTest() = runBlocking {
        identityPoolId = createIdPool(identityPoolName).toString()
        Assertions.assertTrue(!identityPoolId.isEmpty())
        println("Test 9 passed")
    }

    @Test
    @Order(10)
    fun listIdentityProvidersTest() = runBlocking {
        getPools()
        println("Test 10 passed")
    }

    @Test
    @Order(11)
    fun listIdentitiesTest() = runBlocking {
        listPoolIdentities(identityPoolId)
        println("Test 11 passed")
    }

    @Test
    @Order(12)
    fun deleteIdentityPool() = runBlocking {
        deleteIdPool(identityPoolId)
        println("Test 12 passed")
    }

    private suspend fun getSecretValues(): String {
        val secretClient = SecretsManagerClient {
            region = "us-east-1"
            credentialsProvider = EnvironmentCredentialsProvider()
        }
        val secretName = "test/cognito"
        val valueRequest = GetSecretValueRequest {
            secretId = secretName
        }
        val valueResponse = secretClient.getSecretValue(valueRequest)
        return valueResponse.secretString.toString()
    }

    @Nested
    @DisplayName("A class used to get test values from test/cognito (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val username: String? = null
        val userPoolName: String? = null
        val identityId: String? = null
        val email: String? = null
        val clientName: String? = null
        val identityPoolName: String? = null
        val existingPoolName: String? = null
        val existingIdentityPoolId: String? = null
        val existingUserPoolId: String? = null
        val providerName: String? = null
        val clientId: String? = null
        val appId: String? = null
        val secretkey: String? = null
        val password: String? = null
        val poolIdMVP: String? = null
        val clientIdMVP: String? = null
        val userNameMVP: String? = null
        val passwordMVP: String? = null
        val emailMVP: String? = null
    }
}
