/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.Properties

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

    @BeforeAll
    fun setup() {

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
    }

    @Test
    @Order(1)
    fun whenInitializingAWSService_thenNotNull() {
        Assertions.assertNotNull(logGroup)
        Assertions.assertNotNull(alarmName)
        Assertions.assertNotNull(streamName)
        Assertions.assertNotNull(ruleResource)
        Assertions.assertNotNull(metricId)
        Assertions.assertNotNull(filterName)
        Assertions.assertNotNull(destinationArn)
        Assertions.assertNotNull(roleArn)
        Assertions.assertNotNull(filterPattern)
        Assertions.assertNotNull(instanceId)
        Assertions.assertNotNull(ruleName)
        Assertions.assertNotNull(ruleArn)
        Assertions.assertNotNull(namespace)
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun createAlarmTest() = runBlocking {
        putAlarm(alarmName, instanceId)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun describeAlarmsTest() = runBlocking {
        desCWAlarms()
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun createSubscriptionFiltersTest() = runBlocking {
        putSubFilters(filterName, filterPattern, logGroup, destinationArn)
        println("Test 4 passed")
    }

    @Test
    @Order(5)
    fun describeSubscriptionFiltersTest() = runBlocking {
        describeFilters(logGroup)
        println("Test 5 passed")
    }

    @Test
    @Order(6)
    fun disableAlarmActionsTest() = runBlocking {
        disableActions(alarmName)
        println("Test 6 passed")
    }

    @Test
    @Order(7)
    fun enableAlarmActionsTest() = runBlocking {
        enableActions(alarmName)
        println("Test 7 passed")
    }

    @Test
    @Order(8)
    fun getLogEventsTest() = runBlocking {
        getCWLogEvents(logGroup, streamName)
        println("Test 8 passed")
    }

    @Test
    @Order(9)
    fun putCloudWatchEventTest() = runBlocking {
        putCWEvents(ruleResource)
        println("Test 9 passed")
    }

    @Test
    @Order(10)
    fun getMetricDataTest() = runBlocking {
        getMetData()
        println("Test 10 passed")
    }

    @Test
    @Order(11)
    fun deleteSubscriptionFilterTest() = runBlocking {
        deleteSubFilter(filterName, logGroup)
        println("Test 11 passed")
    }

    @Test
    @Order(12)
    fun putRuleTest() = runBlocking {
        putCWRule(ruleName, ruleArn)
        println("Test 12 passed")
    }

    @Test
    @Order(13)
    fun putLogEvents() = runBlocking {
        putCWLogEvents(logGroup, streamName)
        println("Test 13 passed")
    }

    @Test
    @Order(14)
    fun deleteCWAlarmTest() = runBlocking {
        deleteCWAlarm(alarmName)
        println("Test 14 passed")
    }
}
