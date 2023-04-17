/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.kotlin.eventbridge.DASHES
import com.kotlin.eventbridge.addEventRule
import com.kotlin.eventbridge.addSnsEventRule
import com.kotlin.eventbridge.changeRuleState
import com.kotlin.eventbridge.checkBucket
import com.kotlin.eventbridge.checkRule
import com.kotlin.eventbridge.cleanupResources
import com.kotlin.eventbridge.createBucket
import com.kotlin.eventbridge.createIAMRole
import com.kotlin.eventbridge.createSnsTopic
import com.kotlin.eventbridge.listBusesHello
import com.kotlin.eventbridge.listRules
import com.kotlin.eventbridge.listTargetRules
import com.kotlin.eventbridge.listTargets
import com.kotlin.eventbridge.setBucketNotification
import com.kotlin.eventbridge.subEmail
import com.kotlin.eventbridge.triggerCustomRule
import com.kotlin.eventbridge.updateCustomRuleTargetWithTransform
import com.kotlin.eventbridge.updateSnsEventRule
import com.kotlin.eventbridge.updateToCustomRule
import com.kotlin.eventbridge.uploadTextFiletoS3
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.*
import kotlin.system.exitProcess

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class EventBridgeKotlinTest {
    private var roleNameSc = ""
    private var bucketNameSc = ""
    private var topicNameSc = ""
    private var eventRuleNameSc = ""
    private var json = ""
    private var targetId = ""

    @BeforeAll
    fun setup() {
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()
        prop.load(input)

        // Populate the data members required for all tests.
        roleNameSc = prop.getProperty("roleNameSc")
        bucketNameSc = prop.getProperty("bucketNameSc")
        topicNameSc = prop.getProperty("topicNameSc")
        eventRuleNameSc = prop.getProperty("eventRuleNameSc")
    }

    @Test
    @Order(1)
    fun HelloEventBridgeTest() = runBlocking {
        listBusesHello()
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun EventBridgeTest() = runBlocking {
        val sc = Scanner(System.`in`)
        val polJSON = "{" +
            "\"Version\": \"2012-10-17\"," +
            "\"Statement\": [{" +
            "\"Effect\": \"Allow\"," +
            "\"Principal\": {" +
            "\"Service\": \"events.amazonaws.com\"" +
            "}," +
            "\"Action\": \"sts:AssumeRole\"" +
            "}]" +
            "}"

        println(DASHES)
        println("1. Create an AWS Identity and Access Management (IAM) role to use with Amazon EventBridge.")
        val roleArn = createIAMRole(roleNameSc, polJSON)
        Assertions.assertFalse(roleArn!!.isEmpty())
        println(DASHES)

        println(DASHES)
        println("2. Create an S3 bucket with EventBridge events enabled.")
        if (checkBucket(bucketNameSc)) {
            println("$bucketNameSc already exists. Ending this scenario.")
            exitProcess(1)
        }

        createBucket(bucketNameSc)
        delay(3000)
        setBucketNotification(bucketNameSc)
        println(DASHES)

        println(DASHES)
        println("3. Create a rule that triggers when an object is uploaded to Amazon S3.")
        delay(10000)
        addEventRule(roleArn, bucketNameSc, eventRuleNameSc)
        println(DASHES)

        println(DASHES)
        println("4. List rules on the event bus.")
        listRules()
        println(DASHES)

        println(DASHES)
        println("5. Create a new SNS topic for testing and let the user subscribe to the topic.")
        val topicArn = createSnsTopic(topicNameSc)
        Assertions.assertFalse(topicArn!!.isEmpty())
        println(DASHES)

        println(DASHES)
        println("6. Add a target to the rule that sends an email to the specified topic.")
        println("Enter your email to subscribe to the Amazon SNS topic:")
        val email = sc.nextLine()
        subEmail(topicArn, email)
        println("Use the link in the email you received to confirm your subscription. Then press enter to continue.")
        sc.nextLine()
        println(DASHES)

        println(DASHES)
        println("7. Create an EventBridge event that sends an email when an Amazon S3 object is created.")
        addSnsEventRule(eventRuleNameSc, topicArn, topicNameSc, eventRuleNameSc, bucketNameSc)
        println(DASHES)

        println(DASHES)
        println("8. List Targets.")
        listTargets(eventRuleNameSc)
        println(DASHES)

        println(DASHES)
        println(" 9. List the rules for the same target.")
        listTargetRules(topicArn)
        println(DASHES)

        println(DASHES)
        println("10. Trigger the rule by uploading a file to the S3 bucket.")
        println("Press enter to continue.")
        sc.nextLine()
        uploadTextFiletoS3(bucketNameSc)
        println(DASHES)

        println(DASHES)
        println("11. Disable a specific rule.")
        changeRuleState(eventRuleNameSc, false)
        println(DASHES)

        println(DASHES)
        println("12. Check and print the state of the rule.")
        checkRule(eventRuleNameSc)
        println(DASHES)

        println(DASHES)
        println("13. Add a transform to the rule to change the text of the email.")
        updateSnsEventRule(topicArn, eventRuleNameSc)
        println(DASHES)

        println(DASHES)
        println("14. Enable a specific rule.")
        changeRuleState(eventRuleNameSc, true)
        println(DASHES)

        println(DASHES)
        println("15. Trigger the updated rule by uploading a file to the S3 bucket.")
        println("Press Enter to continue.")
        sc.nextLine()
        uploadTextFiletoS3(bucketNameSc)
        println(DASHES)

        println(DASHES)
        println("16. Update the rule to be a custom rule pattern.")
        updateToCustomRule(eventRuleNameSc)
        println("Updated event rule $eventRuleNameSc to use a custom pattern.")
        updateCustomRuleTargetWithTransform(topicArn, eventRuleNameSc)
        println("Updated event target $topicArn.")
        println(DASHES)

        println(DASHES)
        println("17. Send an event to trigger the rule. This will trigger a subscription email.")
        triggerCustomRule(email)
        println("Events have been sent. Press Enter to continue.")
        sc.nextLine()
        println(DASHES)

        println(DASHES)
        println("18. Clean up resources.")
        println("Do you want to clean up resources (y/n)")
        val ans = sc.nextLine()
        if (ans.compareTo("y") == 0) {
            cleanupResources(topicArn, eventRuleNameSc, bucketNameSc, roleNameSc)
        } else {
            println("The resources will not be cleaned up. ")
        }
        println(DASHES)

        println(DASHES)
        println("The Amazon EventBridge example scenario has successfully completed.")
        println(DASHES)
    }
}
