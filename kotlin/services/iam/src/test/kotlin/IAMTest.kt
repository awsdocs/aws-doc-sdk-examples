// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.iam.attachIAMRolePolicy
import com.kotlin.iam.createIAMAccessKey
import com.kotlin.iam.createIAMAccountAlias
import com.kotlin.iam.createIAMPolicy
import com.kotlin.iam.createIAMUser
import com.kotlin.iam.deleteIAMAccountAlias
import com.kotlin.iam.deleteIAMPolicy
import com.kotlin.iam.deleteIAMUser
import com.kotlin.iam.deleteKey
import com.kotlin.iam.detachPolicy
import com.kotlin.iam.getIAMPolicy
import com.kotlin.iam.listAllUsers
import com.kotlin.iam.listKeys
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class IAMTest {
    private var userName = ""
    private var policyName = ""
    private var roleName = ""
    private var policyARN = ""
    private var keyId = ""
    private var accountAlias = ""
    private var usernameSc = ""
    private var policyNameSc = ""
    private var roleNameSc = ""
    private var roleSessionName = ""
    private var fileLocationSc = ""
    private var bucketNameSc = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            userName = values.userName.toString()
            policyName = values.policyName.toString()
            roleName = values.roleName.toString()
            accountAlias = values.accountAlias.toString()
            usernameSc = values.usernameSc.toString()
            policyNameSc = values.policyNameSc.toString()
            roleNameSc = values.roleNameSc1.toString()
            roleSessionName = values.roleName.toString()
            fileLocationSc = values.fileLocationSc.toString()
            bucketNameSc = values.bucketNameSc.toString()

        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        userName = prop.getProperty("userName")
        policyName = prop.getProperty("policyName")
        roleName = prop.getProperty("roleName")
        accountAlias = prop.getProperty("accountAlias")
        policyNameSc = prop.getProperty("policyNameSc")
        usernameSc = prop.getProperty("usernameSc")
        roleNameSc = prop.getProperty("roleNameSc")
        roleSessionName = prop.getProperty("roleSessionName")
        fileLocationSc = prop.getProperty("fileLocationSc")
        bucketNameSc = prop.getProperty("bucketNameSc")
         */
        }

    @Test
    @Order(1)
    fun createUserTest() =
        runBlocking {
            val result = createIAMUser(userName)
            if (result != null) {
                Assertions.assertTrue(!result.isEmpty())
            }
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createPolicyTest() =
        runBlocking {
            policyARN = createIAMPolicy(policyName)
            Assertions.assertTrue(!policyARN.isEmpty())
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun createAccessKeyTest() =
        runBlocking {
            keyId = createIAMAccessKey(userName)
            Assertions.assertTrue(!keyId.isEmpty())
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun attachRolePolicyTest() =
        runBlocking {
            attachIAMRolePolicy(roleName, policyARN)
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun detachRolePolicyTest() =
        runBlocking {
            detachPolicy(roleName, policyARN)
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun getPolicyTest() =
        runBlocking {
            getIAMPolicy(policyARN)
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun listAccessKeysTest() =
        runBlocking {
            listKeys(userName)
            println("Test 7 passed")
        }

    @Test
    @Order(8)
    fun listUsersTest() =
        runBlocking {
            listAllUsers()
            println("Test 8 passed")
        }

    @Test
    @Order(9)
    fun createAccountAliasTest() =
        runBlocking {
            createIAMAccountAlias(accountAlias)
            println("Test 9 passed")
        }

    @Test
    @Order(10)
    fun deleteAccountAliasTest() =
        runBlocking {
            deleteIAMAccountAlias(accountAlias)
            println("Test 10 passed")
        }

    @Test
    @Order(11)
    fun deletePolicyTest() =
        runBlocking {
            deleteIAMPolicy(policyARN)
            println("Test 11 passed")
        }

    @Test
    @Order(12)
    fun deleteAccessKeyTest() =
        runBlocking {
            deleteKey(userName, keyId)
            println("Test 12 passed")
        }

    @Test
    @Order(13)
    fun deleteUserTest() =
        runBlocking {
            deleteIAMUser(userName)
            println("Test 13 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/iam"
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
    @DisplayName("A class used to get test values from test/iam (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val userName: String? = null
        val policyName: String? = null
        val roleName: String? = null
        val accountAlias: String? = null
        val usernameSc: String? = null
        val policyNameSc: String? = null
        val roleNameSc1: String? = null
        private val roleSessionName: String? = null
        val fileLocationSc: String? = null
        val bucketNameSc: String? = null

        fun getRoleNameSc(): String? = roleSessionName
    }
}
