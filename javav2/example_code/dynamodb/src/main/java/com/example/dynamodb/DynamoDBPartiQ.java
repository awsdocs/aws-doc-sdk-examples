//snippet-sourcedescription:[DynamoDBPartiQ.java demonstrates how to work with PartiQL for Amazon DynamoDB.]
//snippet-keyword:[AWS SDK for Java V2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[02/02/2022]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.partiql.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ExecuteStatementRequest;
import software.amazon.awssdk.services.dynamodb.model.ExecuteStatementResponse;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[dynamodb.java2.partiql.import]

/*
 * Prior to running this code example, create an Amazon DynamoDB table named Music as discussed in this topic:
 *
 * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/ql-gettingstarted.html
 *
 * You must also have set up your development environment, including your credentials.
 *
 * For more information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

// snippet-start:[dynamodb.java2.partiql.main]
public class DynamoDBPartiQ {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

         AttributeValue att1 =  AttributeValue.builder()
                .s("Acme Band")
                .build();

        AttributeValue att2 =  AttributeValue.builder()
                .s("PartiQL Rocks")
                .build();

        List<AttributeValue> parameters = new ArrayList<AttributeValue>();
        parameters.add(att1);
        parameters.add(att2);

        // Retrieve an item from the Music table using the SELECT PartiQL statement.
        ExecuteStatementResponse response = executeStatementRequest(ddb, "SELECT * FROM Music  where Artist=? and SongTitle=?", parameters);
        processResults(response);

        //Update an item in the Music table using the UPDATE PartiQL statement.
        processResults(executeStatementRequest(ddb, "UPDATE Music SET AwardsWon=1 SET AwardDetail={'Grammys':[2020, 2018]}  where Artist=? and SongTitle=?", parameters));

        //Add a list value for an item in the Music table.
        ExecuteStatementResponse resp2 = executeStatementRequest(ddb, "UPDATE Music SET AwardDetail.Grammys =LIST_APPEND(AwardDetail.Grammys,[2016])  where Artist=? and SongTitle=?", parameters);
        processResults(resp2);

        // Add a new string set attribute for an item in the Music table.
        processResults(executeStatementRequest(ddb, "UPDATE Music SET BandMembers =<<'member1', 'member2'>> where Artist=? and SongTitle=?", parameters));

        // Add a list value for an item in the Music table.
        processResults(executeStatementRequest(ddb, "UPDATE Music SET AwardDetail.Grammys =list_append(AwardDetail.Grammys,[2016])  where Artist=? and SongTitle=?", parameters));

        // Remove a list value for an item in the Music table.
        processResults(executeStatementRequest(ddb, "UPDATE Music REMOVE AwardDetail.Grammys[2]   where Artist=? and SongTitle=?", parameters));

        // Add a new map member for an item in the Music table.
        processResults(executeStatementRequest(ddb, "UPDATE Music set AwardDetail.BillBoard=[2020] where Artist=? and SongTitle=?", parameters));

        // Add a new string set attribute for an item in the Music table.
        processResults(executeStatementRequest(ddb, "UPDATE Music SET BandMembers =<<'member1', 'member2'>> where Artist=? and SongTitle=?", parameters));

        // Update a string set attribute for an item in the Music table.
        processResults(executeStatementRequest(ddb, "UPDATE Music SET BandMembers =set_add(BandMembers, <<'newmember'>>) where Artist=? and SongTitle=?", parameters));

        System.out.println("This code example has completed");

        ddb.close();
    }

        private static ExecuteStatementResponse executeStatementRequest(DynamoDbClient ddb, String statement, List<AttributeValue> parameters ) {
            ExecuteStatementRequest request = ExecuteStatementRequest.builder()
                    .statement(statement)
                    .parameters(parameters)
                    .build();

            return ddb.executeStatement(request);
        }

    private static void processResults(ExecuteStatementResponse executeStatementResult) {
        System.out.println("ExecuteStatement successful: "+ executeStatementResult.toString());
    }
}
// snippet-end:[dynamodb.java2.partiql.main]
