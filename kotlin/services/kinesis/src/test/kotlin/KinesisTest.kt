/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.kotlin.kinesis.createStream
import com.kotlin.kinesis.deleteStream
import com.kotlin.kinesis.describeKinLimits
import com.kotlin.kinesis.getStockTrades
import com.kotlin.kinesis.listKinShards
import com.kotlin.kinesis.setStockData
import com.kotlin.kinesis.validateStream
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class KinesisTest {
    private var streamName = ""

    @BeforeAll
    fun setup() {
        val random = Random()
        val randomNum: Int = random.nextInt(10000 - 1 + 1) + 1
        val input: InputStream = this.javaClass.getClassLoader().getResourceAsStream("config.properties")
        val prop = Properties()

        // load the properties file.
        prop.load(input)

        // Populate the data members required for all tests
        streamName = prop.getProperty("streamName")+randomNum
    }

    @Test
    @Order(2)
    fun createDataStreamTest() = runBlocking {
        createStream(streamName)
        println("Test 2 passed")
    }

    @Test
    @Order(3)
    fun describeLimitsTest() = runBlocking {
        describeKinLimits()
        println("Test 3 passed")
    }

    @Test
    @Order(4)
    fun listShardsTest() = runBlocking {
        try {
            // Wait 60 secs for table to complete
            TimeUnit.SECONDS.sleep(60)
            listKinShards(streamName)
        } catch (e: InterruptedException) {
            System.err.println(e.message)
            exitProcess(1)
        }
        println("Test 4 passed")
    }


    @Test
    @Order(5)
    fun deleteDataStreamTest() = runBlocking {
        deleteStream(streamName)
        println("Test 7 passed")
    }
}
