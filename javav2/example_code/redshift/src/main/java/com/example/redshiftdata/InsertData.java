//snippet-sourcedescription:[InsertData.java demonstrates how to insert data by using a RedshiftDataClient object and parameters for the SQL statement.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Redshift]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.redshiftdata;

// snippet-start:[redshift.java2.data_addrecord.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.redshiftdata.RedshiftDataClient;
import software.amazon.awssdk.services.redshiftdata.model.ExecuteStatementResponse;
import software.amazon.awssdk.services.redshiftdata.model.RedshiftDataException;
import software.amazon.awssdk.services.redshiftdata.model.SqlParameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
// snippet-end:[redshift.java2.data_addrecord.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * Also, create a Redshift cluster with a table named blog with these string fields:
 *
 * 1. idblog
 * 2. date
 * 3. title
 * 4. body
 * 5. author
 *
 * For information, see https://docs.aws.amazon.com/redshift/latest/gsg/database-tasks.html.
 */

// snippet-start:[redshift.java2.data_addrecord.main]
public class InsertData {

    public static void main(String [] args){

        String clusterId = "redshift-cluster-1";
        String database = "dev";
        String dbUser = "awsuser";
        AddRecord(clusterId, database, dbUser);
    }

    public static void AddRecord( String clusterId, String database, String dbUser) {

        try {
            RedshiftDataClient redshiftDataClient = getClient();
            UUID uuid = UUID.randomUUID();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String sDate1 = dtf.format(now);
            Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
            java.sql.Date date = new java.sql.Date( date1.getTime());

            String title ="OCT 2023 Weather";
            String body ="OCT is suppose to be spooky";
            String author = "user";

            // Use parameters to add a new record.
            String sqlStatement = "INSERT INTO blog VALUES( :idblog , :date, :title, :body, :author );";

            // Create the parameters.
            List<SqlParameter> parameterList = new ArrayList<>() ;
            SqlParameter ob1 = SqlParameter.builder()
                    .name("idblog")
                    .value(uuid.toString())
                    .build();

            SqlParameter ob2 = SqlParameter.builder()
                    .name("date")
                    .value(String.valueOf(date))
                    .build();

            SqlParameter ob3 = SqlParameter.builder()
                    .name("title")
                    .value(title)
                    .build();

            SqlParameter ob4 = SqlParameter.builder()
                    .name("body")
                    .value(body)
                    .build();

            SqlParameter ob5 = SqlParameter.builder()
                    .name("author")
                    .value(author)
                    .build();

            parameterList.add(ob1);
            parameterList.add(ob2);
            parameterList.add(ob3);
            parameterList.add(ob4);
            parameterList.add(ob5);

            ExecuteStatementRequest statementRequest = ExecuteStatementRequest.builder()
                .clusterIdentifier(clusterId)
                .database(database)
                .parameters(parameterList)
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

    private static RedshiftDataClient getClient() {
        Region region = Region.US_WEST_2;
        RedshiftDataClient redshiftDataClient = RedshiftDataClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        return redshiftDataClient;
    }
}
// snippet-end:[redshift.java2.data_addrecord.main]