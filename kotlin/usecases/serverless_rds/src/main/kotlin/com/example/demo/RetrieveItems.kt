/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.demo

import aws.sdk.kotlin.services.rdsdata.RdsDataClient
import aws.sdk.kotlin.services.rdsdata.model.ExecuteStatementRequest
import aws.sdk.kotlin.services.rdsdata.model.Field
import org.springframework.stereotype.Component
import org.w3c.dom.Document
import java.io.StringWriter
import java.sql.SQLException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@Component
class RetrieveItems {

    private val secretArnVal = "<Enter ARN Value>"
    private val resourceArnVal = "<Enter ARN Value>"

    // Return a RdsDataClient object.
    private fun getClient(): RdsDataClient {

        val rdsDataClient = RdsDataClient{region ="us-east-1"}
        return rdsDataClient
    }

    // Retrieve an item based on the ID.
    suspend fun flipItemArchive(id: String): String? {
        val dataClient = getClient()
        val sqlStatement: String
        val arc = 1
        try {
            // Specify the SQL Statement to query data.
            sqlStatement = "update work set archive = '$arc' where idwork ='$id' "
            val sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database ="jobs"
                resourceArn = resourceArnVal
            }
            dataClient.executeStatement(sqlRequest)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    // Get Items Data.
    suspend fun getItemsDataSQL(username: String, arch:Int ): String? {

        val dataClient = getClient()
        val records = mutableListOf<WorkItem>()
        val sqlStatement: String
       
        try {
            sqlStatement = "Select * FROM work where username = '$username ' and archive = $arch";
            val sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database ="jobs"
                resourceArn = resourceArnVal
            }

            val response = dataClient.executeStatement(sqlRequest)
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

                        index++
                    }

                    // Push the object to the list.
                   records.add(workItem)
                }
            }
            return convertToString(toXml(records))
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    // Retrieve an item based on the ID.
    suspend fun getItemSQL(id: String): String? {
        val dataClient = getClient()
        val sqlStatement: String
        var status = ""
        var description = ""
        try {
            sqlStatement = "Select description, status FROM work where idwork ='$id' "
            val sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database ="jobs"
                resourceArn = resourceArnVal
            }

            val response = dataClient.executeStatement(sqlRequest)
            val dataList: List<List<Field>>? = response.records

            // Get the records.
            if (dataList != null) {
                for (list in dataList) {

                  var  index = 0
                    for (myField in list) {
                        val field: Field = myField
                        val result = field.toString()
                        val value = result.substringAfter("=").substringBefore(')')

                        if (index == 0)
                            description = value
                        else
                            status = value
       
                        index++
                    }
                }
            }
            return convertToString(toXmlItem(id, description, status))
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    // Get Items data.
    suspend fun getItemsDataSQLReport(username: String, arch:Int): String? {
        val dataClient = getClient()
        val records = mutableListOf<WorkItem>()
        val sqlStatement: String
        var item: WorkItem? = null
        
        try {
            sqlStatement = "Select * FROM work where username = '" +username +"' and archive = " + arch +"";
            val sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database ="jobs"
                resourceArn = resourceArnVal
            }

            val response = dataClient.executeStatement(sqlRequest)
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

                        index++
                    }

                    records.add(workItem)
                }
            }
            return convertToString(toXml(records))
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    // Convert Work data into XML to pass back to the view.
    private fun toXml(itemList: List<WorkItem>): Document? {
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

    private fun convertToString(xml: Document?): String? {
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

    // Convert Work data into XML to pass back to the view.
    private fun toXmlItem(id2: String, desc2: String, status2: String): Document? {
        try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val doc = builder.newDocument()

            // Start building the XML.
            val root = doc.createElement("Items")
            doc.appendChild(root)
            val item = doc.createElement("Item")
            root.appendChild(item)

            // Set Id.
            val id = doc.createElement("Id")
            id.appendChild(doc.createTextNode(id2))
            item.appendChild(id)

            // Set Description.
            val desc = doc.createElement("Description")
            desc.appendChild(doc.createTextNode(desc2))
            item.appendChild(desc)

            // Set Status.
            val status = doc.createElement("Status")
            status.appendChild(doc.createTextNode(status2))
            item.appendChild(status)
            return doc
        } catch (e: ParserConfigurationException) {
            e.printStackTrace()
        }
        return null
    }
}
