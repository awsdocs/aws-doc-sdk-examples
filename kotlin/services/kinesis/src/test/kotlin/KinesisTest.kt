// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.kotlin.kinesis.createStream
import com.kotlin.kinesis.deleteStream
import com.kotlin.kinesis.describeKinLimits
import com.kotlin.kinesis.listKinShards
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Random
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class KinesisTest {
    private val logger: Logger = LoggerFactory.getLogger(KinesisTest::class.java)
    private var streamName = "Stream"

    @BeforeAll
    fun setup() {
        val random = Random()
        val randomNum: Int = random.nextInt(10000 - 1 + 1) + 1
        streamName += randomNum
    }

    @Test
    @Order(1)
    fun createDataStreamTest() =
        runBlocking {
            createStream(streamName)
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeLimitsTest() =
        runBlocking {
            describeKinLimits()
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listShardsTest() =
        runBlocking {
            try {
                // Wait 60 secs for table to complete
                TimeUnit.SECONDS.sleep(60)
                listKinShards(streamName)
            } catch (e: InterruptedException) {
                System.err.println(e.message)
                exitProcess(1)
            }
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun deleteDataStreamTest() =
        runBlocking {
            deleteStream(streamName)
            logger.info("Test 4 passed")
        }
}
