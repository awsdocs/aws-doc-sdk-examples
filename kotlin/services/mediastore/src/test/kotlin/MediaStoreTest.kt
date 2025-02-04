// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.kotlin.mediastore.checkContainer
import com.kotlin.mediastore.createMediaContainer
import com.kotlin.mediastore.deleteMediaContainer
import com.kotlin.mediastore.listAllContainers
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class MediaStoreTest {
    private val logger: Logger = LoggerFactory.getLogger(MediaStoreTest::class.java)
    private var containerName = ""

    @BeforeAll
    fun setup() {
        val random = Random()
        val randomNum: Int = random.nextInt(10000 - 1 + 1) + 1
        containerName = "containerName" + randomNum
    }

    @Test
    @Order(1)
    fun createContainerTest() =
        runBlocking {
            logger.info("Status is " + createMediaContainer(containerName))
            logger.info("Test 1 passed")
        }

    @Test
    @Order(2)
    fun describeContainerTest() =
        runBlocking {
            logger.info("Status is " + checkContainer(containerName))
            logger.info("Test 2 passed")
        }

    @Test
    @Order(3)
    fun listContainersTest() =
        runBlocking {
            listAllContainers()
            logger.info("Test 3 passed")
        }

    @Test
    @Order(4)
    fun deleteContainerTest() =
        runBlocking {
            deleteMediaContainer(containerName)
            logger.info("Test 4 passed")
        }
}
