/*
   Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package aws.example.dynamodb;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.AmazonServiceException;
import java.util.ArrayList;

/**
 * Create a DynamoDB table.
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class CreateTable
{
    public static void main(String[] args)
    {
        /* The table name */
        String table_name = "HelloTable";

        /* Table attributes */
        ArrayList<AttributeDefinition> attributes = new ArrayList<AttributeDefinition>();
        attributes.add(new AttributeDefinition("Name", ScalarAttributeType.S));

        /* Create the table schema */
        ArrayList<KeySchemaElement> table_schema = new ArrayList<KeySchemaElement>();
        table_schema.add(new KeySchemaElement("Name", KeyType.HASH));

        System.out.format("Creating table %s\n", table_name);

        final AmazonDynamoDBClient ddb = new AmazonDynamoDBClient();

        try {
            ddb.createTable(
                    attributes, table_name, table_schema,
                    new ProvisionedThroughput(new Long(1000), new Long(1000)));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Done!");
    }
}

