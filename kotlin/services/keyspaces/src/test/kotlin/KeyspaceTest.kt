// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.config.DriverConfigLoader
import com.example.keyspace.checkKeyspaceExistence
import com.example.keyspace.checkRestoredTable
import com.example.keyspace.checkTable
import com.example.keyspace.checkTableDelete
import com.example.keyspace.createKeySpace
import com.example.keyspace.createTable
import com.example.keyspace.deleteKeyspace
import com.example.keyspace.deleteTable
import com.example.keyspace.getMovieData
import com.example.keyspace.getSpecificMovie
import com.example.keyspace.getWatchedData
import com.example.keyspace.listKeyspaces
import com.example.keyspace.listKeyspacesPaginator
import com.example.keyspace.listTables
import com.example.keyspace.loadData
import com.example.keyspace.restoreTable
import com.example.keyspace.updateRecord
import com.example.keyspace.updateTable
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Date

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class KeyspaceTest {

    @Test
    @Order(1)
    fun KeyspaceTest() = runBlocking {
        listKeyspaces()
    }
}
