/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.demo

import org.springframework.stereotype.Component
import org.w3c.dom.Document
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.*

/*
Before running this code example, create an Amazon DynamoDB table named Work with a primary key named id.
*/
@Component
class DynamoDBService {

    // Update the archive column.
    suspend fun archiveItemEC(id: String) {
        val tableNameVal = "Work"
        val itemKey = mutableMapOf<String, AttributeValue>()
        itemKey["id"] = AttributeValue.S(id)

        val updatedValues = mutableMapOf<String, AttributeValueUpdate>()
        updatedValues["archive"] = AttributeValueUpdate {
            value = AttributeValue.S("Closed")
            action = AttributeAction.Put
        }

        val request = UpdateItemRequest {
            tableName = tableNameVal
            key = itemKey
            attributeUpdates= updatedValues
        }

        DynamoDbClient { region = "us-east-1" }.use { dynamoDBClient ->
            dynamoDBClient.updateItem(request)
        }
    }

    // Updates the status of a given item.
    suspend fun updateTableItem( id: String, status: String) {

        val tableNameVal = "Work"
        val itemKey = mutableMapOf<String, AttributeValue>()
        itemKey["id"] = AttributeValue.S(id)

        val updatedValues = mutableMapOf<String, AttributeValueUpdate>()
        updatedValues["status"] = AttributeValueUpdate {
            value = AttributeValue.S(status)
            action = AttributeAction.Put
        }

        val request = UpdateItemRequest {
            tableName = tableNameVal
            key = itemKey
            attributeUpdates= updatedValues
        }

        DynamoDbClient { region = "us-east-1" }.use { dynamoDBClient ->
            dynamoDBClient.updateItem(request)
            println("Item in $tableNameVal was updated")
        }
    }

    // Get a single item from the Work table based on idValue.
    suspend fun getItem(idValue: String): String? {
        val tableNameVal = "Work"
        val keyToGet = mutableMapOf<String, AttributeValue>()
        keyToGet["id"] = AttributeValue.S(idValue)

        val request = GetItemRequest {
            key = keyToGet
            tableName = tableNameVal
        }

        DynamoDbClient { region = "us-east-1" }.use { dynamoDBClient ->
            var status = ""
            var description = ""
            val returnedItem = dynamoDBClient.getItem(request).item
            // Get keys and values and get description and status.
            for ((k, v) in returnedItem!!) {
                if (k.compareTo("description") == 0) {
                    description = splitMyString(v.toString())
                } else if (k.compareTo("status") == 0) {
                    status = splitMyString(v.toString())
                }
            }

            val myXML: Document = toXmlItem(idValue, description, status)!!
            return convertToString(myXML)
        }
    }


    suspend fun getOpenItems(myArc:Boolean):String? {

       val tableNameVal = "Work"
       val myList = mutableListOf<WorkItem>()
       val myMap = HashMap<String, String>()
       myMap.put("#archive2", "archive")
       val myExMap = mutableMapOf<String, AttributeValue>()

       if (myArc)
          myExMap.put(":val", AttributeValue.S("Open"))
       else
          myExMap.put(":val", AttributeValue.S("Closed"))

       val scanRequest = ScanRequest {
           expressionAttributeNames = myMap
           expressionAttributeValues = myExMap
           tableName = tableNameVal
           filterExpression = "#archive2 = :val"
       }

        DynamoDbClient { region = "us-east-1" }.use { dynamoDBClient ->
           val response = dynamoDBClient.scan(scanRequest)
            for (item in response.items!!) {
                val keys = item.keys
                val myItem = WorkItem()

                for (key in keys) {
                    when (key) {
                        "date" -> {
                            myItem.date = splitMyString(item[key].toString())
                        }

                        "status" -> {
                            myItem.status = splitMyString(item[key].toString())
                        }

                        "username" -> {
                            myItem.name = "user"
                        }

                        "archive" -> {
                            myItem.arc = splitMyString(item[key].toString())
                        }

                        "description" -> {
                            myItem.description = splitMyString(item[key].toString())
                        }
                        "id" -> {
                            myItem.id = splitMyString(item[key].toString())
                        }
                        else -> {

                            myItem.guide = splitMyString(item[key].toString())
                            myList.add(myItem)
                        }
                    }
                }
            }
           return toXml(myList)?.let { convertToString(it) }
        }
    }

    // Puts an item into an Amazon DynamoDB table.
    suspend fun putItemInTable( itemOb:WorkItem):String {

        val tableNameVal = "Work"

        // Get all the values to store in the Amazon DynamoDB table.
        val myGuid = UUID.randomUUID().toString()
        val user  = itemOb.name
        val desc = itemOb.description
        val status = itemOb.status
        val guide = itemOb.guide

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formatedDate = formatter.format(date)

        // Add the data to the DynamoDB table.
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues["id"] = AttributeValue.S(myGuid)
        itemValues["username"] = AttributeValue.S(user.toString())
        itemValues["archive"] = AttributeValue.S("Open")
        itemValues["date"] =  AttributeValue.S(formatedDate)
        itemValues["description"] = AttributeValue.S(desc.toString())
        itemValues["guide"] = AttributeValue.S(guide.toString())
        itemValues["status"] = AttributeValue.S(status.toString())

        val request = PutItemRequest {
            tableName=tableNameVal
            item = itemValues
        }

        DynamoDbClient { region = "us-east-1" }.use { dynamoDBClient ->
            dynamoDBClient.putItem(request)
            return myGuid
        }
    }
}

// Splits the item[key] value.
fun splitMyString(str:String):String{

    val del1 = "="
    val del2 = ")"
    val parts = str.split(del1, del2)
    val myVal = parts[1]
    return myVal
}

// Convert Work item data into XML to pass back to the view.
private fun toXml(itemList:MutableList<WorkItem>): Document? {
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

private fun toXmlItem(id2: String, desc2: String, status2: String): Document? {
    try {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.newDocument()

        //Start building the XML.
        val root = doc.createElement("Items")
        doc.appendChild(root)
        val item = doc.createElement("Item")
        root.appendChild(item)

        //Set Id.
        val id = doc.createElement("Id")
        id.appendChild(doc.createTextNode(id2))
        item.appendChild(id)

        //Set Description.
        val desc = doc.createElement("Description")
        desc.appendChild(doc.createTextNode(desc2))
        item.appendChild(desc)

        //Set Status.
        val status = doc.createElement("Status")
        status.appendChild(doc.createTextNode(status2))
        item.appendChild(status)
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