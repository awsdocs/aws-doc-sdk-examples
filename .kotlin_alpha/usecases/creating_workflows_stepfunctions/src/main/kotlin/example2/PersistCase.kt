/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package example2

import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import aws.sdk.kotlin.services.dynamodb.model.PutItemRequest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class PersistCase {

    suspend fun putItemInTable(caseId:String, employeeName:String, email:String){

        val ddb = DynamoDbClient{ region = "us-west-2" }
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formatedDate = formatter.format(date)

        val itemValues = mutableMapOf<String, AttributeValue>()

        // Add all content to the table.
        itemValues["id"] = AttributeValue.S(caseId)
        itemValues["email"] = AttributeValue.S(email)
        itemValues["name"] =  AttributeValue.S(employeeName)
        itemValues["registrationDate"] = AttributeValue.S(formatedDate)

        val request = PutItemRequest {
            tableName="Case"
            item = itemValues
        }

        try {
            ddb.putItem(request)
            println(" A new item was placed into Case")

        } catch (ex: Exception) {
            println(ex.message)
            ddb.close()
            exitProcess(0)
        }
    }
}