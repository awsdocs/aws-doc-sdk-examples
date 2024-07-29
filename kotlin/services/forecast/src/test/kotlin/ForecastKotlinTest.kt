﻿// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.forecast.createForecastDataSet
import com.kotlin.forecast.createNewForecast
import com.kotlin.forecast.delForecast
import com.kotlin.forecast.deleteForecastDataSet
import com.kotlin.forecast.describe
import com.kotlin.forecast.listAllForeCasts
import com.kotlin.forecast.listDataGroups
import com.kotlin.forecast.listForecastDataSets
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.Random
import java.util.concurrent.TimeUnit

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class ForecastKotlinTest {
    private var predictorARN = ""
    private var forecastArn = "" // set in test 3
    private var forecastName = ""
    private var dataSetName = ""
    private var myDataSetARN = "" // set in test 2

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values to run these tests from AWS Secrets Manager.
            val rand = Random()
            val randomNum = rand.nextInt(10000 - 1 + 1) + 1
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            predictorARN = values.predARN.toString()
            forecastName = values.forecastName.toString() + randomNum
            dataSetName = values.dataSet.toString() + randomNum

        /*
        // load the properties file.
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        forecastName = prop.getProperty("forecastName")
        dataSetName = prop.getProperty("dataSetName")
        predictorARN = prop.getProperty("predictorARN")
        existingforecastDelete = prop.getProperty("existingforecastDelete")
         */
        }

    @Test
    @Order(1)
    fun createDataSet() =
        runBlocking {
            myDataSetARN = createForecastDataSet(dataSetName).toString()
            assertTrue(!myDataSetARN.isEmpty())
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun createForecast() =
        runBlocking {
            forecastArn = createNewForecast(forecastName, predictorARN).toString()
            assertTrue(!forecastArn.isEmpty())
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listDataSets() =
        runBlocking {
            listForecastDataSets()
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun listDataSetGroups() =
        runBlocking {
            listDataGroups()
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun listForecasts() =
        runBlocking {
            listAllForeCasts()
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun describeForecast() =
        runBlocking {
            describe(forecastArn)
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun deleteDataSet() =
        runBlocking {
            deleteForecastDataSet(myDataSetARN)
            println("Test 7 passed")
        }

    @Test
    @Order(8)
    fun deleteForecast() =
        runBlocking {
            println("Wait 40 mins for resource to become available.")
            TimeUnit.MINUTES.sleep(40)
            delForecast(forecastArn)
            println("Test 8 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/forecast"
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
    @DisplayName("A class used to get test values from test/forecast (an AWS Secrets Manager secret)")
    internal class SecretValues {
        val predARN: String? = null
        val forecastName: String? = null
        val dataSet: String? = null
    }
}
