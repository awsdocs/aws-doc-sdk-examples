/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.aws.rest.DynamoDBService
import com.aws.rest.SendMessage
import com.aws.rest.WorkItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.IOException
import java.io.InputStream
import java.util.Properties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DynamoDBTest {

    private var email = ""
    private var idToFlip = ""
    private var nameVal = ""
    private var guideVal = ""
    private var descriptionVal = ""
    private var statusVal = ""

    @BeforeAll
    fun setup() {
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // Load the properties file.
        prop.load(input)
        email = prop.getProperty("email")
        idToFlip = prop.getProperty("idToFlip")
        nameVal = prop.getProperty("nameVal")
        guideVal = prop.getProperty("guideVal")
        descriptionVal = prop.getProperty("descriptionVal")
        statusVal = prop.getProperty("statusVal")
    }

    @Test
    @Order(1)
    fun getItems() = runBlocking {
        val dbService = DynamoDBService()
        val list = dbService.getOpenItems(false)
        Assertions.assertTrue(list.isNotEmpty())
        println("Test 1 passed")
    }

    @Test
    @Order(2)
    fun flipItemTest() = runBlocking {
        val dbService = DynamoDBService()
        dbService.archiveItemEC(idToFlip)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun reportTest() = runBlocking {
        val sendMessage = SendMessage()
        val dbService = DynamoDBService()
        val xml = dbService.getOpenReport(false)
        try {
            sendMessage.send(email, xml)
        } catch (e: IOException) {
            e.stackTrace
        }
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun addTest() = runBlocking {
        val dbService = DynamoDBService()
        val myWork = WorkItem()
        myWork.guide = guideVal
        myWork.description = descriptionVal
        myWork.status = statusVal
        myWork.name = nameVal
        val id = dbService.putItemInTable(myWork)
        Assertions.assertTrue(id.isNotEmpty())
        println("Test 4 passed")
    }
}
