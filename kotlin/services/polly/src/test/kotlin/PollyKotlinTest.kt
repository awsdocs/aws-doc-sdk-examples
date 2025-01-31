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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class PollyKotlinTest {
    @Test
    @Order(1)
    fun pollyDemo() =
        runBlocking {
            talkPolly()
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeVoicesSample() =
        runBlocking {
            describeVoice()
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listLexiconsTest() =
        runBlocking {
            listLexicons()
            println("Test 3 passed")
        }
}
