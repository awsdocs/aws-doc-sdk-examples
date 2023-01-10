/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.demo

import aws.sdk.kotlin.services.rdsdata.RdsDataClient
import aws.sdk.kotlin.services.rdsdata.model.ExecuteStatementRequest
import aws.sdk.kotlin.services.rdsdata.model.Field
import aws.sdk.kotlin.services.rdsdata.model.SqlParameter
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

@Component
class WorkItemRepository {
    private val secretArnVal = "<Enter value>"
    private val resourceArnVal = "<Enter value>"

    fun param(nameVal: String, valueVal: String): SqlParameter {
        val myPar = SqlParameter {
            name = nameVal
            value = Field.StringValue(valueVal)
        }
        return myPar
    }

    // Archive the specific item.
    suspend fun flipItemArchive(id: String): String {
        val sqlStatement: String
        val arc = "1"

        // Specify the SQL statement to query data.
        sqlStatement = "update work set archive = (:arch) where idwork =(:id);"
        val parametersVal = listOf(
            param("arch", arc),
            param("id", id)
        )
        val sqlRequest = ExecuteStatementRequest {
            secretArn = secretArnVal
            sql = sqlStatement
            database = "jobs"
            resourceArn = resourceArnVal
            parameters = parametersVal
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            rdsDataClient.executeStatement(sqlRequest)
        }
        return id
    }

    // Get items from the database.
    suspend fun getItemsDataSQL(status: String): MutableList<WorkItem> {
        val records = mutableListOf<WorkItem>()
        val sqlStatement: String
        val sqlRequest: ExecuteStatementRequest
        val isArc: String
        if (status.compareTo("true") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive " +
                "FROM work WHERE archive = :arch ;"
            isArc = "1"
            val parametersVal = listOf(param("arch", isArc))
            sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database = "jobs"
                resourceArn = resourceArnVal
                parameters = parametersVal
            }
        } else if (status.compareTo("false") == 0) {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive " +
                "FROM work WHERE archive = :arch ;"
            isArc = "0"
            val parametersVal = listOf(param("arch", isArc))

            sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database = "jobs"
                resourceArn = resourceArnVal
                parameters = parametersVal
            }
        } else {
            sqlStatement = "SELECT idwork, date, description, guide, status, username, archive FROM work ;"
            sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database = "jobs"
                resourceArn = resourceArnVal
            }
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            val response = rdsDataClient.executeStatement(sqlRequest)
            val dataList: List<List<Field>>? = response.records
            var workItem: WorkItem
            var index: Int

            // Get the records.
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (myField in list) {
                        val field: Field = myField
                        val result = field.toString()
                        val value = result.substringAfter("=").substringBefore(')')
                        when (index) {
                            0 -> {
                                workItem.id = value
                            }
                            1 -> {
                                workItem.date = value
                            }
                            2 -> {
                                workItem.description = value
                            }
                            3 -> {
                                workItem.guide = value
                            }
                            4 -> {
                                workItem.status = value
                            }
                            5 -> {
                                workItem.name = value
                            }
                            6 -> {
                                workItem.archived = value != "false"
                            }
                        }
                        index++
                    }

                    // Push the object to the list.
                    records.add(workItem)
                }
            }
        }
        return records
    }

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String {
        val arc = "0"
        val name = item.name.toString()
        val guide = item.guide.toString()
        val description = item.description.toString()
        val status = item.status.toString()

        // Generate the work item ID.
        val uuid = UUID.randomUUID()
        val workId = uuid.toString()

        // Date conversion.
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
            param("arch", arc),
            param("username", name),
            param("status", status),
            param("date", sqlDate.toString()),
            param("description", description),
            param("guide", guide),
            param("idwork", workId)
        )

        val sqlRequest = ExecuteStatementRequest {
            secretArn = secretArnVal
            sql = sqlStatement
            database = "jobs"
            resourceArn = resourceArnVal
            parameters = parametersVal
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            rdsDataClient.executeStatement(sqlRequest)
        }

        return workId
    }

    // Get Items data for the content that is sent using Amazon SES.
    suspend fun getItemsDataSQLReport(arch: String): String? {
        val records = mutableListOf<WorkItem>()
        val sqlStatement = "SELECT idwork, date, description, guide, status, username, archive " +
            "FROM work WHERE archive = :arch ;"

        val parametersVal = listOf(param("arch", arch))
        val sqlRequest = ExecuteStatementRequest {
            secretArn = secretArnVal
            sql = sqlStatement
            database = "jobs"
            resourceArn = resourceArnVal
            parameters = parametersVal
        }

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            val response = rdsDataClient.executeStatement(sqlRequest)
            val dataList: List<List<Field>>? = response.records
            var workItem: WorkItem
            var index: Int

            // Get the records.
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (myField in list) {
                        val field: Field = myField
                        val result = field.toString()
                        val value = result.substringAfter("=").substringBefore(')')
                        when (index) {
                            0 -> {
                                workItem.id = value
                            }
                            1 -> {
                                workItem.date = value
                            }
                            2 -> {
                                workItem.description = value
                            }
                            3 -> {
                                workItem.guide = value
                            }
                            4 -> {
                                workItem.status = value
                            }
                            5 -> {
                                workItem.name = value
                            }
                        }
                        index++
                    }

                    // Push the object to the list.
                    records.add(workItem)
                }
            }
        }
        return convertToString(toXml(records))
    }

    // Convert Work data into XML to use in the report.
    fun toXml(itemList: List<WorkItem>): Document? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.newDocument()
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

    fun convertToString(xml: Document?): String? {
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
}
