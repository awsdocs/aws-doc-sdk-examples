// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.keyspace.listKeyspaces
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
class KeyspaceTest {
    private val logger: Logger = LoggerFactory.getLogger(KeyspaceTest::class.java)

    @Test
    @Order(1)
    fun keyspaceTest() =
        runBlocking {
            listKeyspaces()
            logger.info("Test 1 passed")
        }
}
