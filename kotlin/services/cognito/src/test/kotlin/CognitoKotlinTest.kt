/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.kotlin.cognito.adminRespondToAuthChallenge
import com.kotlin.cognito.checkAuthMethod
import com.kotlin.cognito.confirmSignUp
import com.kotlin.cognito.createIdPool
import com.kotlin.cognito.createNewUser
import com.kotlin.cognito.createPool
import com.kotlin.cognito.delPool
import com.kotlin.cognito.deleteIdPool
import com.kotlin.cognito.describePool
import com.kotlin.cognito.getAdminUser
import com.kotlin.cognito.getAllPools
import com.kotlin.cognito.getPools
import com.kotlin.cognito.getSecretForAppMFA
import com.kotlin.cognito.listAllUserPoolClients
import com.kotlin.cognito.listPoolIdentities
import com.kotlin.cognito.resendConfirmationCode
import com.kotlin.cognito.signUp
import com.kotlin.cognito.verifyTOTP
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.Properties
import java.util.Scanner

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
    fun setup() {
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
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
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() {
        Assertions.assertTrue(!userPoolName.isEmpty())
        Assertions.assertTrue(!identityId.isEmpty())
        Assertions.assertTrue(!username.isEmpty())
        Assertions.assertTrue(!email.isEmpty())
        Assertions.assertTrue(!clientName.isEmpty())
        Assertions.assertTrue(!identityPoolName.isEmpty())
        Assertions.assertTrue(!appId.isEmpty())
        Assertions.assertTrue(!existingUserPoolId.isEmpty())
        Assertions.assertTrue(!existingIdentityPoolId.isEmpty())
        Assertions.assertTrue(!providerName.isEmpty())
        Assertions.assertTrue(!existingPoolName.isEmpty())
        Assertions.assertTrue(!clientId.isEmpty())
        Assertions.assertTrue(!secretkey.isEmpty())
        Assertions.assertTrue(!password.isEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createUserPoolTest() = runBlocking {
        userPoolId = createPool(userPoolName).toString()
        Assertions.assertTrue(!userPoolId.isEmpty())
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun createAdminUserTest() = runBlocking {
        createNewUser(userPoolId, username, email, password)
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun signUpUserTest() = runBlocking {
        signUp(clientId, secretkey, username, password, email)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun listUserPoolsTest() = runBlocking {
        getAllPools()
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun listUserPoolClientsTest() = runBlocking {
        listAllUserPoolClients(existingUserPoolId)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun listUsersTest() = runBlocking {
        listAllUserPoolClients(existingUserPoolId)
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun describeUserPoolTest() = runBlocking {
        describePool(existingUserPoolId)
        println("Test 8 passed")
    }

    @Test
    @Order(9)
    fun deleteUserPool() = runBlocking {
        delPool(userPoolId)
        println("Test 9 passed")
    }

    @Test
    @Order(10)
    fun createIdentityPoolTest() = runBlocking {
        identityPoolId = createIdPool(identityPoolName).toString()
        Assertions.assertTrue(!identityPoolId.isEmpty())
        println("Test 10 passed")
    }

    @Test
    @Order(11)
    fun listIdentityProvidersTest() = runBlocking {
        getPools()
        println("Test 11 passed")
    }

    @Test
    @Order(12)
    fun listIdentitiesTest() = runBlocking {
        listPoolIdentities(identityPoolId)
        println("Test 12 passed")
    }

    @Test
    @Order(13)
    fun deleteIdentityPool() = runBlocking {
        deleteIdPool(identityPoolId)
        println("Test 13 passed")
    }

    @Test
    @Order(14)
    fun testCognitoMVP() = runBlocking {
        println("*** Enter your use name")
        val inOb = Scanner(System.`in`)
        val userName = inOb.nextLine()
        println(userName)

        println("*** Enter your password")
        val password: String = inOb.nextLine()

        println("*** Enter your email")
        val email = inOb.nextLine()

        println("*** Signing up $userName")
        signUp(clientIdMVP, userName, password, email)

        println("*** Getting $userName in the user pool")
        getAdminUser(userName, poolIdMVP)

        println("*** Conformation code sent to $userName. Would you like to send a new code? (Yes/No)")
        val ans = inOb.nextLine()

        if (ans.compareTo("Yes") == 0) {
            println("*** Sending a new confirmation code")
            resendConfirmationCode(clientIdMVP, userName)
        }
        println("*** Enter the confirmation code that was emailed")
        val code = inOb.nextLine()
        confirmSignUp(clientIdMVP, code, userName)

        println("*** Rechecking the status of $userName in the user pool")
        getAdminUser(userName, poolIdMVP)

        val authResponse = checkAuthMethod(clientIdMVP, userName, password, poolIdMVP)
        val mySession = authResponse.session

        val newSession = getSecretForAppMFA(mySession)
        println("*** Enter the 6-digit code displayed in Google Authenticator")
        val myCode = inOb.nextLine()

        // Verify the TOTP and register for MFA.
        verifyTOTP(newSession, myCode)
        println("*** Re-enter a 6-digit code displayed in Google Authenticator")
        val mfaCode: String = inOb.nextLine()
        val authResponse1 = checkAuthMethod(clientId, userNameMVP, password, poolIdMVP)
        val session2 = authResponse1.session
        adminRespondToAuthChallenge(userName, clientId, mfaCode, session2)
    }
}
