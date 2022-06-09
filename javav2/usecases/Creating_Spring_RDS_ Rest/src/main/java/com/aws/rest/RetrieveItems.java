/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.aws.rest;

import java.util.ArrayList ;
import java.util.List;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.rdsdata.model.Field;
import software.amazon.awssdk.services.rdsdata.model.RdsDataException;

@Component
public class RetrieveItems {

    private final String secretArn = "<Enter value>" ;
    private final String resourceArn = "<Enter value>" ;

    private RdsDataClient getClient() {

        Region region = Region.US_EAST_1;
        RdsDataClient dataClient = RdsDataClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(region)
                .build();

        return dataClient;
    }

    public void flipItemArchive(String id ) {

        RdsDataClient dataClient = getClient();
        int arc = 1;

        try {
            // Specify the SQL statement to query data.
            String sqlStatement = "update work set archive = '"+arc+"' where idwork ='" +id + "' ";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(sqlStatement)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();

            dataClient.executeStatement(sqlRequest);
        } catch (RdsDataException e) {
            e.printStackTrace();
        }
    }

    // Get Items data from the database.
    public List<WorkItem> getItemsDataSQLReport(int arch) {

        RdsDataClient dataClient = getClient();
        String username = "User";
        List<WorkItem>records = new ArrayList<>();

        try {
            String sqlStatement = "Select * FROM work where username = '" +username +"' and archive = " + arch +"";
            ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                    .secretArn(secretArn)
                    .sql(sqlStatement)
                    .database("jobs")
                    .resourceArn(resourceArn)
                    .build();

            ExecuteStatementResponse response = dataClient.executeStatement(sqlRequest);
            List<List<Field>> dataList = response.records();
            WorkItem workItem ;
            int index = 0 ;

            // Get the records.
            for (List list: dataList) {

                // New WorkItem object.
                workItem = new WorkItem();
                index = 0;
                for (Object myField : list) {

                    Field field = (Field) myField;
                    String value = field.stringValue();

                    if (index == 0)
                        workItem.setId(value);

                    else if (index == 1)
                        workItem.setDate(value);

                    else if (index == 2)
                        workItem.setDescription(value);

                    else if (index == 3)
                        workItem.setGuide(value);

                    else if (index == 4)
                        workItem.setStatus(value);

                    else if (index == 5)
                        workItem.setName(value);

                    // Increment the index.
                    index++;
                }

                // Push the object to the List.
                records.add(workItem);
            }

            return records;

        } catch (RdsDataException e) {
            e.printStackTrace();
        }
        return null;
    }
}
