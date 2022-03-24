/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import aws.sdk.kotlin.services.elasticbeanstalk.ElasticBeanstalkClient
import com.aws.example.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import java.io.IOException
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class ElasticBeanstalkTest {

    lateinit var beanstalkClient: ElasticBeanstalkClient
    var appName: String = ""
    var envName: String = ""
    var appArn: String = ""
    var envArn: String = ""

    @BeforeAll
    @Throws(IOException::class, URISyntaxException::class)
    fun setUp() {
        beanstalkClient = ElasticBeanstalkClient{ region = "us-east-1" }

        try {
            ElasticBeanstalkTest::class.java.classLoader.getResourceAsStream("config.properties").use { input ->
                val prop = Properties()
                if (input == null) {
                    println("Sorry, unable to find config.properties")
                    return
                }

                prop.load(input)
                appName = prop.getProperty("appName")
                envName = prop.getProperty("envName")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() {
        Assertions.assertNotNull(beanstalkClient)
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
        envArn = createEBEnvironment( envName, appName)
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
