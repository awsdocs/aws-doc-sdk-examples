// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.kotlin.eventbridge.listBusesHello
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class EventBridgeKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(EventBridgeKotlinTest::class.java)
    private var roleNameSc = ""
    private var bucketNameSc = ""
    private var topicNameSc = ""
    private var eventRuleNameSc = ""
    private var json = ""
    private var targetId = ""

    @Test
    @Order(1)
    fun helloEventBridgeTest() =
        runBlocking {
            listBusesHello()
            logger.info("Test 1 passed")
        }
}
