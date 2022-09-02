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
import kotlinx.coroutines.delay
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class RetrieveItems {

    private val databaseVal = "dev"
    private val dbUserVal = "awsuser"
    private val clusterId = "redshift-cluster-1"

    // Return items from the work table.
    suspend fun getData(arch: Int): MutableList<WorkItem> {
        val username = "user"
        val sqlStatement = "Select * FROM work where username = '$username' and archive = $arch"
        val id = performSQLStatement(sqlStatement)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResults(id)
    }

    // Return items from the work table.
    suspend fun getDataXML(arch: Int): String? {
        val username = "user"
        val sqlStatement = "Select * FROM work where username = '$username' and archive = $arch"
        val id = performSQLStatement(sqlStatement)
        println("The identifier of the statement is $id")
        checkStatement(id)
        return getResultsXML(id)
    }

    // Returns items within a collection.
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

            // Get the records.
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        val value = parseValue(field)
                        if (index == 0) {
                            workItem.id = value
                        } else if (index == 1) {
                            workItem.date = value
                        } else if (index == 2) {
                            workItem.description = value
                        } else if (index == 3) {
                            workItem.guide = value
                        } else if (index == 4) {
                            workItem.status = value
                        } else if (index == 5) {
                            workItem.name = value
                        }

                        // Increment the index.
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
            if (dataList != null) {
                for (list in dataList) {
                    workItem = WorkItem()
                    index = 0
                    for (field in list) {
                        val value = parseValue(field)
                        if (index == 0)
                            workItem.id = value
                        else if (index == 1)
                            workItem.date = value
                        else if (index == 2)
                            workItem.description = value
                        else if (index == 3)
                            workItem.guide = value
                        else if (index == 4)
                            workItem.status = value
                        else if (index == 5)
                            workItem.name = value

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
        val arc = 1
        val sqlStatement = "update work set archive = '$arc' where idwork ='$id' "

        val statementRequest = ExecuteStatementRequest {
            this.clusterIdentifier = clusterId
            this.database = databaseVal
            this.dbUser = dbUserVal
            sql = sqlStatement
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

    suspend fun performSQLStatement(sqlStatement: String?): String? {
        val statementRequest = ExecuteStatementRequest {
            this.clusterIdentifier = clusterId
            this.database = databaseVal
            this.dbUser = dbUserVal
            sql = sqlStatement
        }

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
