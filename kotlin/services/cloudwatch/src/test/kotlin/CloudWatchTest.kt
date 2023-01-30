/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.kotlin.cloudwatch.DASHES
import com.kotlin.cloudwatch.addAnomalyDetector
import com.kotlin.cloudwatch.addMetricDataForAlarm
import com.kotlin.cloudwatch.addMetricToDashboard
import com.kotlin.cloudwatch.checkForMetricAlarm
import com.kotlin.cloudwatch.createAlarm
import com.kotlin.cloudwatch.createDashboardWithMetrics
import com.kotlin.cloudwatch.createNewCustomMetric
import com.kotlin.cloudwatch.deleteAlarm
import com.kotlin.cloudwatch.deleteAnomalyDetector
import com.kotlin.cloudwatch.deleteCWAlarm
import com.kotlin.cloudwatch.deleteDashboard
import com.kotlin.cloudwatch.deleteSubFilter
import com.kotlin.cloudwatch.desCWAlarms
import com.kotlin.cloudwatch.describeAlarms
import com.kotlin.cloudwatch.describeAnomalyDetectors
import com.kotlin.cloudwatch.describeFilters
import com.kotlin.cloudwatch.disableActions
import com.kotlin.cloudwatch.enableActions
import com.kotlin.cloudwatch.getAlarmHistory
import com.kotlin.cloudwatch.getAndDisplayMetricStatistics
import com.kotlin.cloudwatch.getAndOpenMetricImage
import com.kotlin.cloudwatch.getCWLogEvents
import com.kotlin.cloudwatch.getCustomMetricData
import com.kotlin.cloudwatch.getMetData
import com.kotlin.cloudwatch.getMetricStatistics
import com.kotlin.cloudwatch.getSpecificMet
import com.kotlin.cloudwatch.listDashboards
import com.kotlin.cloudwatch.listMets
import com.kotlin.cloudwatch.listNameSpaces
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
import java.util.Scanner
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

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
        myDateSc = prop.getProperty("myDateSc")
        costDateWeekSc = prop.getProperty("costDateWeekSc")
        dashboardNameSc = prop.getProperty("dashboardNameSc")
        dashboardJsonSc = prop.getProperty("dashboardJsonSc")
        dashboardAddSc = prop.getProperty("dashboardAddSc")
        settingsSc = prop.getProperty("settingsSc")
        metricImageSc = prop.getProperty("metricImageSc")
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

    @Test
    @Order(16)
    fun TestScenario() = runBlocking {
        val inOb = Scanner(System.`in`)
        val dataPoint = "10.0".toDouble()
        println(DASHES)
        println("1. List at least five available unique namespaces from Amazon CloudWatch. Select a CloudWatch namespace from the list.")
        val list: ArrayList<String> = listNameSpaces()
        for (z in 0..4) {
            println("    ${z + 1}. ${list[z]}")
        }

        var selectedNamespace: String
        var selectedMetrics = ""
        var num = inOb.nextLine().toInt()
        println("You selected $num")

        if (1 <= num && num <= 5) {
            selectedNamespace = list[num - 1]
        } else {
            println("You did not select a valid option.")
            exitProcess(1)
        }
        println("You selected $selectedNamespace")
        println(DASHES)

        println(DASHES)
        println("2. List available metrics within the selected namespace and select one from the list.")
        val metList = listMets(selectedNamespace)
        for (z in 0..4) {
            println("    ${ z + 1}. ${metList?.get(z)}")
        }
        num = inOb.nextLine().toInt()
        if (1 <= num && num <= 5) {
            selectedMetrics = metList!![num - 1]
        } else {
            println("You did not select a valid option.")
            System.exit(1)
        }
        println("You selected $selectedMetrics")
        val myDimension = getSpecificMet(selectedNamespace)
        if (myDimension == null) {
            println("Error - Dimension is null")
            exitProcess(1)
        }
        println(DASHES)

        println(DASHES)
        println("3. Get statistics for the selected metric over the last day.")
        val metricOption: String
        val statTypes = ArrayList<String>()
        statTypes.add("SampleCount")
        statTypes.add("Average")
        statTypes.add("Sum")
        statTypes.add("Minimum")
        statTypes.add("Maximum")

        for (t in 0..4) {
            println("    ${t + 1}. ${statTypes[t]}")
        }
        println("Select a metric statistic by entering a number from the preceding list:")
        num = inOb.nextLine().toInt()
        if (1 <= num && num <= 5) {
            metricOption = statTypes[num - 1]
        } else {
            println("You did not select a valid option.")
            exitProcess(1)
        }
        println("You selected $metricOption")
        getAndDisplayMetricStatistics(selectedNamespace, selectedMetrics, metricOption, myDateSc, myDimension)
        println(DASHES)

        println(DASHES)
        println("4. Get CloudWatch estimated billing for the last week.")
        getMetricStatistics(costDateWeekSc)
        println(DASHES)

        println(DASHES)
        println("5. Create a new CloudWatch dashboard with metrics.")
        createDashboardWithMetrics(dashboardNameSc, dashboardJsonSc)
        println(DASHES)

        println(DASHES)
        println("6. List dashboards using a paginator.")
        listDashboards()
        println(DASHES)

        println(DASHES)
        println("7. Create a new custom metric by adding data to it.")
        createNewCustomMetric(dataPoint)
        println(DASHES)

        println(DASHES)
        println("8. Add an additional metric to the dashboard.")
        addMetricToDashboard(dashboardAddSc, dashboardNameSc)
        println(DASHES)

        println(DASHES)
        println("9. Create an alarm for the custom metric.")
        val alarmName: String = createAlarm(settingsSc)
        println(DASHES)

        println(DASHES)
        println("10. Describe 10 current alarms.")
        describeAlarms()
        println(DASHES)

        println(DASHES)
        println("11. Get current data for the new custom metric.")
        getCustomMetricData(settingsSc)
        println(DASHES)

        println(DASHES)
        println("12. Push data into the custom metric to trigger the alarm.")
        addMetricDataForAlarm(settingsSc)
        println(DASHES)

        println(DASHES)
        println("13. Check the alarm state using the action DescribeAlarmsForMetric.")
        checkForMetricAlarm(settingsSc)
        println(DASHES)

        println(DASHES)
        println("14. Get alarm history for the new alarm.")
        getAlarmHistory(settingsSc, myDateSc)
        println(DASHES)

        println(DASHES)
        println("15. Add an anomaly detector for the custom metric.")
        addAnomalyDetector(settingsSc)
        println(DASHES)

        println(DASHES)
        println("16. Describe current anomaly detectors.")
        describeAnomalyDetectors(settingsSc)
        println(DASHES)

        println(DASHES)
        println("17. Get a metric image for the custom metric.")
        getAndOpenMetricImage(metricImageSc)
        println(DASHES)

        println(DASHES)
        println("18. Clean up the Amazon CloudWatch resources.")
        deleteDashboard(dashboardNameSc)
        deleteAlarm(alarmName)
        deleteAnomalyDetector(settingsSc)
        println(DASHES)
    }
}
