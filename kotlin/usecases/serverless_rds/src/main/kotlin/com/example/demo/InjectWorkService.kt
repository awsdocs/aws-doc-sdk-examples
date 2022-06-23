/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.demo

import org.springframework.stereotype.Component
import java.sql.Date
import java.sql.SQLException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import aws.sdk.kotlin.services.rdsdata.RdsDataClient
import aws.sdk.kotlin.services.rdsdata.model.ExecuteStatementRequest

@Component
class InjectWorkService {

    private val secretArnVal = "<Enter ARN Value>"
    private val resourceArnVal = "<Enter ARN Value>"

   // Return a RdsDataClient object.
    private fun getClient(): RdsDataClient {

        val rdsDataClient = RdsDataClient{region ="us-east-1"}
        return rdsDataClient
    }

    // Modify an existing record.
    suspend fun modifySubmission(id: String, status: String?): String? {
        val dataClient = getClient()
        try {
            val sqlStatement = "update work set status = '$status' where idwork = '$id'"
            val sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database = "jobs"
                resourceArn = resourceArnVal
            }
            dataClient.executeStatement(sqlRequest)
            return id

        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String? {
        val arc = 0
        val dataClient = getClient()
        try {

             val name = item.name
            val guide = item.guide
            val description = item.description
            val status = item.status

            // Generate the work item ID.
            val uuid = UUID.randomUUID()
            val workId = uuid.toString()

            // Date conversion.
            val dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
            val now = LocalDateTime.now()
            val sDate1 = dtf.format(now)
            val date1 = SimpleDateFormat("yyyy/MM/dd").parse(sDate1)
            val sqlDate = Date(date1.time)

            // Inject an item into the system.
            val sqlStatement = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES('$workId', '$name', '$sqlDate','$description','$guide','$status','$arc');"
            val sqlRequest = ExecuteStatementRequest {
                secretArn = secretArnVal
                sql = sqlStatement
                database = "jobs"
                resourceArn = resourceArnVal
            }
            dataClient.executeStatement(sqlRequest)
            return workId
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
     }
   }
