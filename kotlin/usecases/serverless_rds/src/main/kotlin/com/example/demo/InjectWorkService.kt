/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.demo

import aws.sdk.kotlin.services.rdsdata.RdsDataClient
import aws.sdk.kotlin.services.rdsdata.model.ExecuteStatementRequest
import org.springframework.stereotype.Component
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class InjectWorkService {

    private val secretArnVal = "<Enter value>"
    private val resourceArnVal = "<Enter value>"

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String? {
        val arc = 0
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

        RdsDataClient { region = "us-east-1" }.use { rdsDataClient ->
            rdsDataClient.executeStatement(sqlRequest)
        }
        return workId
    }
}
