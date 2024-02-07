/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.aws.example.createApp
import com.aws.example.createEBEnvironment
import com.aws.example.deleteApp
import com.aws.example.describeApps
import com.aws.example.describeEnv
import com.aws.example.getOptions
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.IOException
import java.net.URISyntaxException
import java.util.Random
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class ElasticBeanstalkTest {
    var appName: String = "TestApp"
    var envName: String = "TestEnv"
    var appArn: String = ""
    var envArn: String = ""

    @BeforeAll
    @Throws(IOException::class, URISyntaxException::class)
    fun setUp() {
        val random = Random()
        val randomNum = random.nextInt(10000 - 1 + 1) + 1
        appName = appName + randomNum
        envName = envName + randomNum
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() {
        Assertions.assertNotNull(appName)
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun CreateApp() = runBlocking {
        appArn = createApp(appName)
        assertTrue(!appArn.isEmpty())
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun CreateEnvironment() = runBlocking {
        envArn = createEBEnvironment(envName, appName)
        assertTrue(!envArn.isEmpty())
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun DescribeApplications() = runBlocking {
        describeApps()
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun DescribeEnvironment() = runBlocking {
        describeEnv(appName)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun DescribeOptions() = runBlocking {
        getOptions(envName)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun DeleteApplication() = runBlocking {
        println("*** Wait for 5 MIN so the app can be deleted")
        TimeUnit.MINUTES.sleep(5)
        deleteApp(appName)
        println("Test 7 passed")
    }
}
