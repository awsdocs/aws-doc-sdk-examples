// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.photo.services

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import aws.sdk.kotlin.services.dynamodb.model.QueryRequest
import aws.sdk.kotlin.services.dynamodb.model.QueryResponse
import aws.sdk.kotlin.services.dynamodb.model.ScanRequest
import aws.sdk.kotlin.services.dynamodb.model.UpdateItemRequest
import com.example.photo.LabelCount
import com.example.photo.PhotoApplicationResources
import com.example.photo.WorkCount
import java.util.Collections

class DynamoDBService {
    // Put the tags in the given list into an Amazon DynamoDB table.
    suspend fun putRecord(list: ArrayList<LabelCount>) {
        for (count in list) {
            val label = count.getName()
            val key = count.getKey()
            if ((label != null) && (key != null)) {
                addSingleRecord(label, key)
            }
        }
    }

    suspend fun addSingleRecord(label: String, keyVal: String) {
        // Check to see if the label exists in the Amazon DynamoDB table.
        val scanResult = scanLabelTable(PhotoApplicationResources.LABELS_TABLE)
        if (scanResult == 0) {
            // There are no records in the table.
            addNewLabel(PhotoApplicationResources.LABELS_TABLE, label)
            updateCount(PhotoApplicationResources.LABELS_TABLE, label)
            updateTableList(PhotoApplicationResources.LABELS_TABLE, label, keyVal)
        } else {
            // There are records. Check to see if a Label exists.
            val labelCount = checkTagExists(label)
            if (labelCount > 0) {
                // The Label exists in the table.
                updateCount(PhotoApplicationResources.LABELS_TABLE, label)
                updateTableList(PhotoApplicationResources.LABELS_TABLE, label, keyVal)
            } else {
                // The Label doesn't exist in the table.
                addNewLabel(PhotoApplicationResources.LABELS_TABLE, label)
                updateCount(PhotoApplicationResources.LABELS_TABLE, label)
                updateTableList(PhotoApplicationResources.LABELS_TABLE, label, keyVal)
            }
        }
        println("All Data added to the Amazon DynamoDB table")
    }

    // Scan table before looking for Labels.
    suspend fun scanLabelTable(tableNameVal: String): Int {
        val request = ScanRequest {
            tableName = tableNameVal
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            val response = ddb.scan(request)
            println("${response.count}")
            return response.count
        }
    }

    suspend fun checkTagExists(tag: String): Int {
        val tableNameVal = PhotoApplicationResources.LABELS_TABLE
        val partitionKeyName = "Label"
        val partitionKeyVal = tag
        val partitionAlias = "#a"

        val attrNameAlias = mutableMapOf<String, String>()
        attrNameAlias[partitionAlias] = partitionKeyName

        // Set up mapping of the partition name with the value.
        val attrValues = mutableMapOf<String, AttributeValue>()
        attrValues[":$partitionKeyName"] = AttributeValue.S(partitionKeyVal)

        val request = QueryRequest {
            tableName = tableNameVal
            keyConditionExpression = "$partitionAlias = :$partitionKeyName"
            expressionAttributeNames = attrNameAlias
            this.expressionAttributeValues = attrValues
        }

        var count: Int
        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            val response = ddb.query(request)
            count = response.count
            println("$count")
        }
        return count
    }

    suspend fun updateCount(tableNameVal: String, label: String) {
        // Specify the item key.
        val keyMap: MutableMap<String, AttributeValue> = HashMap()
        keyMap["Label"] = AttributeValue.S(label)

        // Create a map to hold the update expression attributes.
        val expressionAttributeValuesMap: MutableMap<String, AttributeValue> = HashMap()
        expressionAttributeValuesMap[":countval"] = AttributeValue.N("1")

        // Specify the update item request with an expression.
        val request = UpdateItemRequest {
            tableName = tableNameVal
            key = keyMap
            updateExpression = "ADD #count :countval"
            expressionAttributeNames = Collections.singletonMap("#count", "count")
            expressionAttributeValues = expressionAttributeValuesMap
        }

        // Update the item in the table.
        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            ddb.updateItem(request)
            println("Item in $tableNameVal was updated")
        }
    }

    suspend fun updateTableList(tableNameVal: String, keyVal: String, updateVal: String) {
        // Build the item key for the DynamoDB update request.
        val itemKey: MutableMap<String, AttributeValue> = java.util.HashMap()
        itemKey["Label"] = AttributeValue.S(keyVal)

        // Build the updated values for the "images" column.
        val updatedValues = mutableMapOf<String, AttributeValue>()
        val myFileList = ArrayList<AttributeValue>()
        myFileList.add(AttributeValue.S(updateVal))
        updatedValues["images"] = AttributeValue.L(myFileList)

        val expressionAttValues = mutableMapOf<String, AttributeValue>()
        expressionAttValues[":val"] = AttributeValue.L(myFileList)

        // Build the update request and execute it using the DynamoDB client.
        val updateItemRequest = UpdateItemRequest {
            tableName = tableNameVal
            key = itemKey
            updateExpression = "SET images = list_append(images, :val)"
            expressionAttributeValues = expressionAttValues
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            ddb.updateItem(updateItemRequest)
            println("Item in $tableNameVal was updated")
        }
    }

    suspend fun addNewLabel(tableNameVal: String, keyName: String) {
        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        val emptyList = ArrayList<AttributeValue>()
        itemValues["Label"] = AttributeValue.S(keyName)
        itemValues["images"] = AttributeValue.L(emptyList)

        val request = PutItemRequest {
            tableName = tableNameVal
            item = itemValues
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            ddb.putItem(request)
            println(" A new item was placed into $tableNameVal.")
        }
    }

    suspend fun scanPhotoTable(): HashMap<String, WorkCount> {
        val myMap = HashMap<String, WorkCount>()
        val request = ScanRequest {
            tableName = PhotoApplicationResources.LABELS_TABLE
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            val response = ddb.scan(request)
            response.items?.forEach { item ->
                val wc = WorkCount()
                var labelName = ""
                var myCount: String
                item.keys.forEach { key ->
                    if (key.compareTo("Label") == 0) {
                        val inputString = item[key].toString()
                        val regex = Regex("=(.*?)\\)")
                        val matchResult = regex.find(inputString)
                        if (matchResult != null) {
                            labelName = matchResult.groupValues[1]
                            println(labelName)
                        }
                    }

                    if (key.compareTo("count") == 0) {
                        val inputString = item[key].toString()
                        val regex = Regex("=(.*?)\\)")
                        val matchResult = regex.find(inputString)
                        if (matchResult != null) {
                            myCount = matchResult.groupValues[1]
                            println("The value of count is $myCount")
                            val countInt = myCount.toInt()
                            wc.setCount(countInt)
                        }
                    }
                }
                myMap[labelName] = wc
            }
        }
        return myMap
    }

    suspend fun getImagesByLabel(partitionKeyVal: String): List<String> {
        val partitionAlias = "#a"
        val partitionKeyName = "Label"
        val attrNameAlias = mutableMapOf<String, String>()
        attrNameAlias[partitionAlias] = partitionKeyName

        // Set up mapping of the partition name with the value.
        val attrValues = mutableMapOf<String, AttributeValue>()
        attrValues[":$partitionKeyName"] = AttributeValue.S(partitionKeyVal)

        val request = QueryRequest {
            tableName = PhotoApplicationResources.LABELS_TABLE
            keyConditionExpression = "$partitionAlias = :$partitionKeyName"
            expressionAttributeNames = attrNameAlias
            expressionAttributeValues = attrValues
        }

        DynamoDbClient { region = "us-east-1" }.use { ddb ->
            val response = ddb.query(request)
            return processItems(response)
        }
    }

    fun processItems(queryRes: QueryResponse): List<String> {
        val allImages = queryRes.items?.get(0)
        val fileString = allImages.toString()
        val imagesValue = fileString.substringAfter("images=L(value=[").substringBefore("]),")
        return extractJpgFiles(imagesValue)
    }

    private fun extractJpgFiles(input: String): List<String> {
        val pattern =
            "S\\(value=([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}-[a-zA-Z0-9]+\\.jpg)".toRegex()
        val matchResults = pattern.findAll(input)
        return matchResults.map { it.groupValues[1] }.toList()
    }
}
