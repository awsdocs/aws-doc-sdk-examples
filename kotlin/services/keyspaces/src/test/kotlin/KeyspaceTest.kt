/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

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
    private lateinit var session: CqlSession
    private val fileName = "<Enter value>"
    private val keyspaceName = "<Enter value>"
    private val titleUpdate = "The Family"
    private val yearUpdate = 2013
    private val tableName = "MovieKotlin"
    private val tableNameRestore = "MovieRestore"

    @BeforeAll
    fun setUp() {
        val loader = DriverConfigLoader.fromClasspath("application.conf")
        session = CqlSession.builder()
            .withConfigLoader(loader)
            .build()
    }

    @Test
    @Order(1)
    fun scenarioTest() = runBlocking {
        println(com.example.keyspace.DASHES)
        println("1. Create a keyspace.")
        createKeySpace(keyspaceName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        delay(5000)
        println("2. Check for keyspace existence.")
        checkKeyspaceExistence(keyspaceName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("3. List keyspaces using a paginator.")
        listKeyspacesPaginator()
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("4. Create a table with a simple movie data schema and enable point-in-time recovery.")
        createTable(keyspaceName, tableName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("5. Check for the table to be in an Active state.")
        delay(6000)
        checkTable(keyspaceName, tableName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("6. List all tables in the keyspace.")
        listTables(keyspaceName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("7. Use a Cassandra driver to insert some records into the Movie table.")
        delay(6000)
        loadData(session, fileName, keyspaceName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("8. Get all records from the Movie table.")
        getMovieData(session, keyspaceName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("9. Get a specific Movie.")
        getSpecificMovie(session, keyspaceName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("10. Get a UTC timestamp for the current time.")
        val utc = ZonedDateTime.now(ZoneOffset.UTC)
        println("DATETIME = ${Date.from(utc.toInstant())}")
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("11. Update the table schema to add a watched Boolean column.")
        updateTable(keyspaceName, tableName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("12. Update an item as watched.")
        delay(10000) // Wait 10 secs for the update.
        updateRecord(session, keyspaceName, titleUpdate, yearUpdate)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("13. Query for items with watched = True.")
        getWatchedData(session, keyspaceName)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("14. Restore the table back to the previous state using the timestamp.")
        println("Note that the restore operation can take up to 20 minutes.")
        restoreTable(keyspaceName, utc)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("15. Check for completion of the restore action.")
        delay(5000)
        checkRestoredTable(keyspaceName, "MovieRestore")
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("16. Delete both tables.")
        deleteTable(keyspaceName, tableName)
        deleteTable(keyspaceName, tableNameRestore)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("17. Confirm that both tables are deleted.")
        checkTableDelete(keyspaceName, tableName)
        checkTableDelete(keyspaceName, tableNameRestore)
        println(com.example.keyspace.DASHES)

        println(com.example.keyspace.DASHES)
        println("18. Delete the keyspace.")
        deleteKeyspace(keyspaceName)
        println(com.example.keyspace.DASHES)
    }
}
