// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.kotlin.polly.describeVoice
import com.kotlin.polly.listLexicons
import com.kotlin.polly.talkPolly
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
class PollyKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(PollyKotlinTest::class.java)

    @Test
    @Order(1)
    fun pollyDemo() =
        runBlocking {
            talkPolly()
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeVoicesSample() =
        runBlocking {
            describeVoice()
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listLexiconsTest() =
        runBlocking {
            listLexicons()
            logger.info("Test 3 passed")
        }
}
