/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest

import aws.sdk.kotlin.services.redshiftdata.RedshiftDataClient
import aws.sdk.kotlin.services.redshiftdata.model.DescribeStatementRequest
import aws.sdk.kotlin.services.redshiftdata.model.ExecuteStatementRequest
import aws.sdk.kotlin.services.redshiftdata.model.Field
import aws.sdk.kotlin.services.redshiftdata.model.GetStatementResultRequest
import aws.sdk.kotlin.services.redshiftdata.model.SqlParameter
import kotlinx.coroutines.delay
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import java.io.StringWriter
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.collections.ArrayList

@Component
class RedshiftService {
    // Update these values to reflect your environment.
    private val databaseVal = "dev"
    private val dbUserVal = "awsuser"
    private val clusterId = "redshift-cluster-1"

    fun param(nameVal: String, valueVal: Boolean): SqlParameter {
        val myPar = SqlParameter {
            name = nameVal
            value = valueVal.toString()
        }
        return myPar
    }

    fun param2(nameVal: String, valueVal: String): SqlParameter {
        val myPar = SqlParameter {
            name = nameVal
            value = valueVal
        }
        return myPar
    }

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String {
        val name = item.name
        val guide = item.guide
        val description = item.description
        val status = item.status
        val arc = "0"

        // Generate the work item ID.
        val uuid = UUID.randomUUID()
        val workId = uuid.toString()

        // Date conversion.
        SimpleDateFormat("yyyy-MM-dd")
        val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
        val now = LocalDateTime.now()
        val sDate1 = dtf.format(now)
        val date1 = SimpleDateFormat("yyyy/MM/dd").parse(sDate1)
        val sqlDate = Date(date1.time)

        // Inject an item into the database.
        val sqlStatement =
            "INSERT INTO work (idwork, username, date, description, guide, status, archive) VALUES" +
                "(:idwork, :username, :date, :description, :guide, :status, :arch);"

        val parametersVal = listOf(
            param2("arch", arc),
            param2("username", name.toString()),
            param2("status", status.toString()),
            param2("date", sqlDate.toString()),
            param2("description", description.toString()),
            param2("guide", guide.toString()),
            param2("idwork", workId)
        )

        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
            parameters = parametersVal
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
            return workId
        }
    }

    // Return items from the work table.
    suspend fun getData(status: String): MutableList<WorkItem> {
        val isArc: Boolean
        val statementRequest: ExecuteStatementRequest
        val sqlStatement: String
        if (status.compareTo("true") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work WHERE archive = :arch ;"
            isArc = true
            val parametersVal = listOf(param("arch", isArc))
            statementRequest = ExecuteStatementRequest {
                this.clusterIdentifier = clusterId
                this.database = databaseVal
                this.dbUser = dbUserVal
                this.parameters = parametersVal
                sql = sqlStatement
            }
        } else if (status.compareTo("false") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work WHERE archive = :arch ;"
            isArc = false
            val parametersVal = listOf(param("arch", isArc))
            statementRequest = ExecuteStatementRequest {
                this.clusterIdentifier = clusterId
                this.database = databaseVal
                this.dbUser = dbUserVal
                this.parameters = parametersVal
                sql = sqlStatement
            }
        } else {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work ;"
            statementRequest = ExecuteStatementRequest {
                this.clusterIdentifier = clusterId
                this.database = databaseVal
                this.dbUser = dbUserVal
                sql = sqlStatement
            }
        }

        val id = performSQLStatement(statementRequest)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResults(id)
    }

    // Return items from the work table.
    suspend fun getDataXML(): String? {
        val sqlStatement = "SELECT idwork, date, description, guide, status, username, archive " +
            "FROM work WHERE archive = :arch ;"
        val isArc = false
        val parametersVal = listOf(param("arch", isArc))
        val statementRequest = ExecuteStatementRequest {
            this.clusterIdentifier = clusterId
            this.database = databaseVal
            this.dbUser = dbUserVal
            this.parameters = parametersVal
            sql = sqlStatement
        }
        val id = performSQLStatement(statementRequest)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResultsXML(id)
    }

    // Returns items.
    suspend fun getResults(statementId: String?): MutableList<WorkItem> {
        val records = mutableListOf<WorkItem>()
        val resultRequest = GetStatementResultRequest {
            id = statementId
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            val response = redshiftDataClient.getStatementResult(resultRequest)
            var workItem: WorkItem
            var index: Int

            // Iterate through the List.
            val dataList: List<List<Field>>? = response.records
            var value: String
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        when (index) {
                            0 -> {
                                value = parseValue(field)
                                workItem.id = value
                            }
                            1 -> {
                                value = parseValue(field)
                                workItem.date = value
                            }
                            2 -> {
                                value = parseValue(field)
                                workItem.description = value
                            }
                            3 -> {
                                value = parseValue(field)
                                workItem.guide = value
                            }
                            4 -> {
                                value = parseValue(field)
                                workItem.status = value
                            }
                            5 -> {
                                value = parseValue(field)
                                workItem.name = value
                            }
                            6 -> {
                                value = parseBooleanValue(field)
                                workItem.archived = value != "false"
                            }
                        }
                        index++
                    }

                    // Push the object to the List.
                    records.add(workItem)
                }
            }
            return records
        }
    }

    // Returns open items within XML.
    suspend fun getResultsXML(statementId: String?): String? {
        val records: MutableList<WorkItem> = ArrayList()
        val resultRequest = GetStatementResultRequest {
            id = statementId
        }
        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            val response = redshiftDataClient.getStatementResult(resultRequest)
            var workItem: WorkItem
            var index: Int

            // Iterate through the List element where each element is a List object.
            val dataList: List<List<Field>>? = response.records
            var value: String
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        when (index) {
                            0 -> {
                                value = parseValue(field)
                                workItem.id = value
                            }
                            1 -> {
                                value = parseValue(field)
                                workItem.date = value
                            }
                            2 -> {
                                value = parseValue(field)
                                workItem.description = value
                            }
                            3 -> {
                                value = parseValue(field)
                                workItem.guide = value
                            }
                            4 -> {
                                value = parseValue(field)
                                workItem.status = value
                            }
                            5 -> {
                                value = parseValue(field)
                                workItem.name = value
                            }
                        }

                        // Increment the index.
                        index++
                    }

                    // Push the object to the List.
                    records.add(workItem)
                }
            }
            return toXml(records)?.let { convertToString(it) }
        }
    }

    // Update the work table.
    suspend fun flipItemArchive(id: String) {
        val arc = "1"
        // Specify the SQL statement to query data.
        val sqlStatement = "update work set archive = (:arch) where idwork =(:id);"
        val parametersVal = listOf(
            param2("arch", arc),
            param2("id", id)
        )
        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
            parameters = parametersVal
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
        }
    }

    // Convert Work item data into XML.
    private fun toXml(itemList: MutableList<WorkItem>): Document? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.newDocument()

            // Start building the XML.
            val root = doc.createElement("Items")
            doc.appendChild(root)

            // Get the elements from the collection.
            val custCount = itemList.size

            // Iterate through the collection.
            for (index in 0 until custCount) {
                // Get the WorkItem object from the collection.
                val myItem = itemList[index]
                val item = doc.createElement("Item")
                root.appendChild(item)

                // Set Id.
                val id = doc.createElement("Id")
                id.appendChild(doc.createTextNode(myItem.id))
                item.appendChild(id)

                // Set Name.
                val name = doc.createElement("Name")
                name.appendChild(doc.createTextNode(myItem.name))
                item.appendChild(name)

                // Set Date.
                val date = doc.createElement("Date")
                date.appendChild(doc.createTextNode(myItem.date))
                item.appendChild(date)

                // Set Description.
                val desc = doc.createElement("Description")
                desc.appendChild(doc.createTextNode(myItem.description))
                item.appendChild(desc)

                // Set Guide.
                val guide = doc.createElement("Guide")
                guide.appendChild(doc.createTextNode(myItem.guide))
                item.appendChild(guide)

                // Set Status.
                val status = doc.createElement("Status")
                status.appendChild(doc.createTextNode(myItem.status))
                item.appendChild(status)
            }
            return doc
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return null
    }

    private fun convertToString(xml: Document): String? {
        try {
            val transformer = TransformerFactory.newInstance().newTransformer()
            val result = StreamResult(StringWriter())
            val source = DOMSource(xml)
            transformer.transform(source, result)
            return result.writer.toString()
        } catch (ex: TransformerException) {
            ex.printStackTrace()
        }
        return null
    }

    // Return the String value of the field.
    fun parseValue(myField: Field): String {
        val ss = myField.toString()
        if ("StringValue" in ss) {
            var str = ss.substringAfterLast("=")
            str = str.substring(0, str.length - 1)
            return str
        }
        return ""
    }

    fun parseBooleanValue(myField: Field): String {
        val ss = myField.toString()
        if ("BooleanValue" in ss) {
            var str = ss.substringAfterLast("=")
            str = str.substring(0, str.length - 1)
            return str
        }
        return ""
    }

    suspend fun performSQLStatement(statementRequest: ExecuteStatementRequest): String? {
        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            val response = redshiftDataClient.executeStatement(statementRequest)
            return response.id
        }
    }

    suspend fun checkStatement(sqlId: String?) {
        val statementRequest = DescribeStatementRequest {
            id = sqlId
        }

        // Wait until the sql statement processing is finished.
        var finished = false
        var status: String
        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            while (!finished) {
                val response = redshiftDataClient.describeStatement(statementRequest)
                status = response.status.toString()
                println("...$status")

                if (status.compareTo("FINISHED") == 0) {
                    finished = true
                } else {
                    delay(500)
                }
            }
        }
        println("The statement is finished!")
    }
}
