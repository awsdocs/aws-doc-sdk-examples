//snippet-sourcedescription:[InsertData.java demonstrates how to insert data by using a RedshiftDataClient object.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Redshift ]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[04/05/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshiftdata;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.*;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;


/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class InsertData {

    public static void main(String [] args){

        try {

            String clusterId = "redshift-cluster-1";
            String database = "dev";
            String dbUser = "awsuser";

            RedshiftDataClient redshiftDataClient = getClient();
            UUID uuid = UUID.randomUUID();
            String id  = uuid.toString();

            // Date conversion
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date sqlDate = new java.sql.Date( date1.getTime());

            String title ="Aug Weather";
            String body ="Aug is suppose to be mild";
            String author = "user";

            // Inject an item into the system
            String sqlStatement = "INSERT INTO blog (idblog, date, title, body, author) VALUES( '"+uuid+"' ,'"+sqlDate +"','"+title +"' , '"+body +"', '"+author +"');";

            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                    .clusterIdentifier(clusterId)
                    .database(database)
                    .dbUser(dbUser)
                    .sql(sqlStatement)
                    .build();

            ExecuteStatementResponse response = redshiftDataClient.executeStatement(statementRequest);
            System.out.println( "The id is "+response.id());

        } catch (RedshiftDataException | ParseException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    private static  RedshiftDataClient getClient() {

        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
                .region(region)
                .build();

        return redshiftDataClient;
    }
}
