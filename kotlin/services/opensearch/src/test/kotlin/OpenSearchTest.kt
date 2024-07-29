// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.search.createNewDomain
import com.example.search.deleteSpecificDomain
import com.example.search.listAllDomains
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.util.Random

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class OpenSearchTest {
    private var domainName = ""

    @BeforeAll
    fun setup() {
        val random = Random()
        val randomNum: Int = random.nextInt(500 - 1 + 1) + 1
        domainName = "domain" + randomNum
    }

    @Test
    @Order(1)
    fun createDomainTest() =
        runBlocking {
            createNewDomain(domainName)
            println("Test 1 passed")
        }

    @Test
    @Order(2)
    fun listDomainNamesTest() =
        runBlocking {
            listAllDomains()
            println("Test 2 passed")
        }

    @Test
    @Order(3)
    fun deleteDomainTest() =
        runBlocking {
            deleteSpecificDomain(domainName)
            println("Test 4 passed")
        }
}
