// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.URISyntaxException
import java.util.Random
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class ElasticBeanstalkTest {
    private val logger: Logger = LoggerFactory.getLogger(ElasticBeanstalkTest::class.java)
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
        logger.info("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createApp() =
        runBlocking {
            appArn = createApp(appName)
            assertTrue(!appArn.isEmpty())
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun createEnvironment() =
        runBlocking {
            envArn = createEBEnvironment(envName, appName)
            assertTrue(!envArn.isEmpty())
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun describeApplications() =
        runBlocking {
            describeApps()
            logger.info("Test 4 passed")
        }

    @Test
    @Order(5)
    fun describeEnvironment() =
        runBlocking {
            describeEnv(appName)
            logger.info("Test 5 passed")
        }

    @Test
    @Order(6)
    fun describeOptions() =
        runBlocking {
            getOptions(envName)
            logger.info("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteApplication() =
        runBlocking {
            println("*** Wait for 5 MIN so the app can be deleted")
            TimeUnit.MINUTES.sleep(5)
            deleteApp(appName)
            logger.info("Test 7 passed")
        }
}
