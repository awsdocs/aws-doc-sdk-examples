/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest

import aws.sdk.kotlin.services.redshiftdata.RedshiftDataClient
import aws.sdk.kotlin.services.redshiftdata.model.ExecuteStatementRequest
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class InjectWorkService {

    private val databaseVal = "dev"
    private val dbUserVal = "awsuser"
    private val clusterId = "redshift-cluster-1"

    // Inject a new submission.
    suspend fun injestNewSubmission(item: WorkItem): String {
        val name = item.name
        val guide = item.guide
        val description = item.description
        val status = item.status
        val arc = 0

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

        // Inject an item into the system.
        val sqlStatement = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES('$workId', '$name', '$sqlDate','$description','$guide','$status','$arc');"
        val statementRequest = ExecuteStatementRequest {
            clusterIdentifier = clusterId
            database = databaseVal
            dbUser = dbUserVal
            sql = sqlStatement
        }

        RedshiftDataClient { region = "us-west-2" }.use { redshiftDataClient ->
            redshiftDataClient.executeStatement(statementRequest)
            return workId
        }
    }
}