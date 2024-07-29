// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import aws.sdk.kotlin.runtime.auth.credentials.EnvironmentCredentialsProvider
import aws.sdk.kotlin.services.secretsmanager.SecretsManagerClient
import aws.sdk.kotlin.services.secretsmanager.model.GetSecretValueRequest
import com.google.gson.Gson
import com.kotlin.cloudwatch.deleteCWAlarm
import com.kotlin.cloudwatch.deleteSubFilter
import com.kotlin.cloudwatch.desCWAlarms
import com.kotlin.cloudwatch.describeFilters
import com.kotlin.cloudwatch.disableActions
import com.kotlin.cloudwatch.enableActions
import com.kotlin.cloudwatch.getCWLogEvents
import com.kotlin.cloudwatch.getMetData
import com.kotlin.cloudwatch.putAlarm
import com.kotlin.cloudwatch.putCWEvents
import com.kotlin.cloudwatch.putCWLogEvents
import com.kotlin.cloudwatch.putCWRule
import com.kotlin.cloudwatch.putSubFilters
import kotlinx.coroutines.runBlocking
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
class CloudWatchTest {
    private var logGroup = ""
    private var alarmName = ""
    private var streamName = ""
    private var metricId = ""
    private var instanceId = ""
    private var ruleResource = ""
    private var filterName = ""
    private var destinationArn = ""
    private var roleArn = ""
    private var ruleArn = ""
    private var namespace = ""
    private var filterPattern = ""
    private var ruleName = ""
    private var myDateSc = ""
    private var costDateWeekSc = ""
    private var dashboardNameSc = ""
    private var dashboardJsonSc = ""
    private var dashboardAddSc = ""
    private var settingsSc = ""
    private var metricImageSc = ""

    @BeforeAll
    fun setup() =
        runBlocking {
            // Get the values from AWS Secrets Manager.
            val gson = Gson()
            val json: String = getSecretValues()
            val values = gson.fromJson(json, SecretValues::class.java)
            logGroup = values.logGroup.toString()
            alarmName = values.alarmName.toString()
            streamName = values.streamName.toString()
            ruleResource = values.ruleResource.toString()
            metricId = values.metricId.toString()
            filterName = values.filterName.toString()
            destinationArn = values.destinationArn.toString()
            roleArn = values.roleArn.toString()
            filterPattern = values.filterPattern.toString()
            instanceId = values.instanceId.toString()
            ruleName = values.ruleName.toString()
            ruleArn = values.ruleArn.toString()
            namespace = values.namespace.toString()
            myDateSc = values.myDateSc.toString()
            costDateWeekSc = values.costDateWeekSc.toString()
            dashboardNameSc = values.dashboardNameSc.toString()
            dashboardJsonSc = values.dashboardJsonSc.toString()
            dashboardAddSc = values.dashboardAddSc.toString()
            settingsSc = values.settingsSc.toString()
            metricImageSc = values.metricImageSc.toString()

            // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
        /*
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)
        logGroup = prop.getProperty("logGroup")
        alarmName = prop.getProperty("alarmName")
        streamName = prop.getProperty("streamName")
        ruleResource = prop.getProperty("ruleResource")
        metricId = prop.getProperty("metricId")
        filterName = prop.getProperty("filterName")
        destinationArn = prop.getProperty("destinationArn")
        roleArn = prop.getProperty("roleArn")
        filterPattern = prop.getProperty("filterPattern")
        instanceId = prop.getProperty("instanceId")
        ruleName = prop.getProperty("ruleName")
        ruleArn = prop.getProperty("ruleArn")
        namespace = prop.getProperty("namespace")
        myDateSc = prop.getProperty("myDateSc")
        costDateWeekSc = prop.getProperty("costDateWeekSc")
        dashboardNameSc = prop.getProperty("dashboardNameSc")
        dashboardJsonSc = prop.getProperty("dashboardJsonSc")
        dashboardAddSc = prop.getProperty("dashboardAddSc")
        settingsSc = prop.getProperty("settingsSc")
        metricImageSc = prop.getProperty("metricImageSc")
         */
        }

    @Test
    @Order(1)
    fun createAlarmTest() =
        runBlocking {
            putAlarm(alarmName, instanceId)
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeAlarmsTest() =
        runBlocking {
            desCWAlarms()
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun createSubscriptionFiltersTest() =
        runBlocking {
            putSubFilters(filterName, filterPattern, logGroup, destinationArn)
            println("Test 3 passed")
        }

    @Test
    @Order(4)
    fun describeSubscriptionFiltersTest() =
        runBlocking {
            describeFilters(logGroup)
            println("Test 4 passed")
        }

    @Test
    @Order(5)
    fun disableAlarmActionsTest() =
        runBlocking {
            disableActions(alarmName)
            println("Test 5 passed")
        }

    @Test
    @Order(6)
    fun enableAlarmActionsTest() =
        runBlocking {
            enableActions(alarmName)
            println("Test 6 passed")
        }

    @Test
    @Order(7)
    fun getLogEventsTest() =
        runBlocking {
            getCWLogEvents(logGroup, streamName)
            println("Test 7 passed")
        }

    @Test
    @Order(8)
    fun putCloudWatchEventTest() =
        runBlocking {
            putCWEvents(ruleResource)
            println("Test 8 passed")
        }

    @Test
    @Order(9)
    fun getMetricDataTest() =
        runBlocking {
            getMetData()
            println("Test 9 passed")
        }

    @Test
    @Order(10)
    fun deleteSubscriptionFilterTest() =
        runBlocking {
            deleteSubFilter(filterName, logGroup)
            println("Test 10 passed")
        }

    @Test
    @Order(11)
    fun putRuleTest() =
        runBlocking {
            putCWRule(ruleName, ruleArn)
            println("Test 11 passed")
        }

    @Test
    @Order(12)
    fun putLogEvents() =
        runBlocking {
            putCWLogEvents(logGroup, streamName)
            println("Test 12 passed")
        }

    @Test
    @Order(13)
    fun deleteCWAlarmTest() =
        runBlocking {
            deleteCWAlarm(alarmName)
            println("Test 13 passed")
        }

    private suspend fun getSecretValues(): String {
        val secretName = "test/cloudwatch"
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
    @DisplayName("A class used to get test values from test/cloudwatch (an AWS Secrets Manager secret)")
    internal class SecretValues {
        // Provide getter methods for each of the test values
        val logGroup: String? = null
        val alarmName: String? = null
        val instanceId: String? = null
        val streamName: String? = null
        val ruleResource: String? = null
        val metricId: String? = null
        val filterName: String? = null
        val destinationArn: String? = null
        val roleArn: String? = null
        val ruleArn: String? = null
        val filterPattern: String? = null
        val ruleName: String? = null
        val namespace: String? = null
        val myDateSc: String? = null
        val costDateWeekSc: String? = null
        val dashboardNameSc: String? = null
        val dashboardJsonSc: String? = null
        val dashboardAddSc: String? = null
        val settingsSc: String? = null
        val metricImageSc: String? = null
    }
}
