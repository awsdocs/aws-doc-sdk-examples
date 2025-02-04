// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.support.displaySomeServices
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SupportTest {
    private val logger: Logger = LoggerFactory.getLogger(SupportTest::class.java)

    @Test
    @Order(1)
    fun supportHelloScenario() =
        runBlocking {
            displaySomeServices()
            logger.info("\n AWS Support Hello Test passed")
        }
}
