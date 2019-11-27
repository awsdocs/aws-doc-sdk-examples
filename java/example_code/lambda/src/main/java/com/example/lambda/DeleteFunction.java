package com.example.dyn;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.ListTablesResponse;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.List;

// snippet-end:[dynamodb.java2.list_tables.import]
/**
 * List DynamoDB tables for the current AWS account.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class ListTables
{
    public static void main(String[] args)
    {
        System.out.println("Your DynamoDB tables:\n");

        // snippet-start:[dynamodb.java2.list_tables.main]
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();;

        boolean more_tables = true;
        String last_name = null;

        while(more_tables) {
            try {
                ListTablesResponse response = null;
                if (last_name == null) {
                    ListTablesRequest request = ListTablesRequest.builder().build();
                    response = ddb.listTables(request);
                }
                else {
                    ListTablesRequest request = ListTablesRequest.builder()
                            .exclusiveStartTableName(last_name).build();
                    response = ddb.listTables(request);
                }

                List<String> table_names = response.tableNames();

                if (table_names.size() > 0) {
                    for (String cur_name : table_names) {
                        System.out.format("* %s\n", cur_name);
                    }
                } else {
                    System.out.println("No tables found!");
                    System.exit(0);
                }

                last_name = response.lastEvaluatedTableName();
                if (last_name == null) {
                    more_tables = false;
                }
            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
        // snippet-end:[dynamodb.java2.list_tables.main]
        System.out.println("\nDone!");
    }
}
