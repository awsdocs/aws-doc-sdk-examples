/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.aws.rds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.RdsDataException;

@Component
public class InjectWorkService {

    private String secretArn = "arn:aws:secretsmanager:us-east-1:814548047983:secret:sqlscott2-WEJX1b" ;
    private String resourceArn = "arn:aws:rds:us-east-1:814548047983:cluster:database-4" ;

   // Returns a RdsDataClient object.
    private RdsDataClient getClient() {

        Region region = Region.US_EAST_1;
        RdsDataClient dataClient = RdsDataClient.builder()
                .region(region)
                .build();

        return dataClient;
    }

    // Inject a new submission.
    public String injestNewSubmission(WorkItem item) {

        RdsDataClient dataClient = getClient();

        try {

            // Convert rev to int.
            String name = item.getName();
            String guide = item.getGuide();
            String description = item.getDescription();
            String status = item.getStatus();
            int arc = 0;

            // Generate the work item ID.
            UUID uuid = UUID.randomUUID();
            String workId = uuid.toString();

            // Date conversion.
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date( date1.getTime());

            // Inject an item into the system.
            String insert = "INSERT INTO work (idwork, username,date,description, guide, status, archive) VALUES('"+workId+"', '"+name+"', '"+sqlDate+"','"+description+"','"+guide+"','"+status+"','"+arc+"');";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(insert)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();
            dataClient.executeStatement(sqlRequest);
            return workId;

        } catch (RdsDataException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Modifies an existing record.
    public String modifySubmission(String id, String desc, String status) {

        RdsDataClient dataClient = getClient();
        try {

           String query = "update work set description = '"+desc+"', status = '"+status+"' where idwork = '" +id +"'";
           ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(query)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();
            dataClient.executeStatement(sqlRequest);
            return id;

        } catch (RdsDataException e) {
            e.printStackTrace();
        }
        return null;
    }
}
