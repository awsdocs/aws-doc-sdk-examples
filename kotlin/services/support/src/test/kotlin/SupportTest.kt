/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.support.addAttachSupportCase
import com.example.support.addAttachment
import com.example.support.createSupportCase
import com.example.support.describeAttachment
import com.example.support.displayAllServices
import com.example.support.displayServices
import com.example.support.displaySevLevels
import com.example.support.getOpenCase
import com.example.support.getResolvedCase
import com.example.support.listCommunications
import com.example.support.resolveSupportCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.IOException
import java.util.Properties
import kotlin.system.exitProcess

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class SupportTest {

    private var fileAttachment = ""

    @BeforeAll
    fun setUp() {
        try {
            SupportTest::class.java.classLoader.getResourceAsStream("config.properties").use { input ->
                val prop = Properties()
                prop.load(input)

                // Populate the data members required for all tests.
                fileAttachment = prop.getProperty("fileAttachment")
                if (input == null) {
                    println("Sorry, unable to find config.properties")
                    return
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    @Test
    @Order(1)
    fun supportHelloScenario() = runBlocking {
        displayAllServices()
        println("\n AWS Support Hello Test passed")
    }

    @Test
    @Order(2)
    fun supportScenario() = runBlocking {
        val sevCatList = displayServices()
        val sevLevel = displaySevLevels()
        Assertions.assertFalse(sevLevel.isEmpty())
        val caseId = createSupportCase(sevCatList, sevLevel)
        if (caseId != null) {
            if (caseId.compareTo("") == 0) {
                exitProcess(1)
            }
        }
        getOpenCase()
        val attachmentSetId = addAttachment(fileAttachment)
        if (attachmentSetId != null) {
            Assertions.assertFalse(attachmentSetId.isEmpty())
        }
        addAttachSupportCase(caseId, attachmentSetId)
        val attachId = listCommunications(caseId)
        if (attachId != null) {
            Assertions.assertFalse(attachId.isEmpty())
        }
        describeAttachment(attachId)
        if (caseId != null) {
            resolveSupportCase(caseId)
        }
        getResolvedCase()
        println("\n AWS Support Test passed")
    }
}
