// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import com.kotlin.secrets.getValue
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
class SecretsManagerKotlinTest {
    private val logger: Logger = LoggerFactory.getLogger(SecretsManagerKotlinTest::class.java)
    private var secretName = "mysecret"

    @Test
    @Order(1)
    fun getSecretValue() =
        runBlocking {
            getValue(secretName)
            logger.info("Test 1 passed")
        }
}
