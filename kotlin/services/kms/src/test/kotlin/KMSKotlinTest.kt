// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.kms.createCustomAlias
import com.kotlin.kms.createKey
import com.kotlin.kms.createNewGrant
import com.kotlin.kms.decryptData
import com.kotlin.kms.deleteSpecificAlias
import com.kotlin.kms.describeSpecifcKey
import com.kotlin.kms.disableKey
import com.kotlin.kms.displayGrantIds
import com.kotlin.kms.enableKey
import com.kotlin.kms.encryptData
import com.kotlin.kms.listAllAliases
import com.kotlin.kms.listAllKeys
import com.kotlin.kms.revokeKeyGrant
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class KMSKotlinTest {
    private var keyId = "" // gets set in test 2
    private var keyDesc = ""
    private var granteePrincipal = ""
    private var operation = ""
    private var grantId = ""
    private var aliasName = ""
    private var path = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            keyDesc = values.keyDesc.toString()
            operation = values.operation.toString()
            aliasName = values.aliasName.toString()
            granteePrincipal = values.granteePrincipal.toString()
            path = values.path.toString()

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        keyDesc = prop.getProperty("keyDesc")
        granteePrincipal = prop.getProperty("granteePrincipal")
        operation = prop.getProperty("operation")
        aliasName = prop.getProperty("aliasName")
        path = prop.getProperty("path")
         */
        }

    @Test
    @Order(2)
    fun createCustomerKeyTest() =
        runBlocking {
            keyId = createKey(keyDesc).toString()
            Assertions.assertTrue(!keyId.isEmpty())
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun encryptDataKeyTest() =
        runBlocking {
            val encryptData = encryptData(keyId)
            decryptData(encryptData, keyId, path)
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun disableCustomerKeyTest() =
        runBlocking {
            disableKey(keyId)
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun enableCustomerKeyTest() =
        runBlocking {
            enableKey(keyId)
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun createGrantTest() =
        runBlocking {
            grantId = createNewGrant(keyId, granteePrincipal, operation).toString()
            Assertions.assertTrue(!grantId.isEmpty())
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun listGrantsTest() =
        runBlocking {
            displayGrantIds(keyId)
            println("Test 7 passed")
        }

    @Test
    @Order(8)
    fun revokeGrantsTest() =
        runBlocking {
            revokeKeyGrant(keyId, grantId)
            println("Test 8 passed")
        }

    @Test
    @Order(9)
    fun describeKeyTest() =
        runBlocking {
            describeSpecifcKey(keyId)
            println("Test 9 passed")
        }

    @Test
    @Order(10)
    fun createAliasTest() =
        runBlocking {
            createCustomAlias(keyId, aliasName)
            println("Test 10 passed")
        }

    @Test
    @Order(11)
    fun listAliasesTest() =
        runBlocking {
            listAllAliases()
            println("Test 11 passed")
        }

    @Test
    @Order(12)
    fun deleteAliasTest() =
        runBlocking {
            deleteSpecificAlias(aliasName)
            println("Test 12 passed")
        }

    @Test
    @Order(13)
    fun listKeysTest() =
        runBlocking {
            listAllKeys()
            println("Test 13 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/kms"
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
    @DisplayName("A class used to get test values from test/kms (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val granteePrincipal: String? = null
        val keyDesc: String? = null
        val operation: String? = null
        val aliasName: String? = null
        val path: String? = null
    }
}
