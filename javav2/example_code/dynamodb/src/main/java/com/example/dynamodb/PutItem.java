/*
Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at
http://aws.amazon.com/apache2.0/
This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/

//snippet-sourcedescription:[PutItem.java demonstrates how to place an item into an AWS DynamoDB table]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[dynamodb]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/5/2020]
//snippet-sourceauthor:[soo-aws]

package com.example.dynamodb;
// snippet-start:[dynamodb.java2.put_item.complete]
// snippet-start:[dynamodb.java2.put_item.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import java.util.HashMap;
// snippet-end:[dynamodb.java2.put_item.import]

/**
 * Put an item in a DynamoDB table.
 *
 * Takes the name of the table, a name (primary key value) and additional values
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class PutItem {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutItem <table> <key> <keyVal> <albumtitle> <albumtitleval> <awards> <awardsval> <Songtitle> <songtitleval>\n\n" +
                "Where:\n" +
                "    table - the table in which an item is placed (ie - Music3),\n" +
                "    key -  the key used in the table (ie - Artist),\n" +
                "    keyval  - the key value that represents the item to get (ie -Famous Band),\n" +
                "    albumTitle -  album title (ie - AlbumTitle),\n" +
                "    AlbumTitleValue -  the name of the album (ie - Songs About Life ),\n" +
                "    Awards -  awards column (ie - Awards),\n" +
                "    AwardVal -  the value of the awards (10),\n" +
                "    SongTitle -  song title (ie - SongTitle),\n" +
                "    SongTitleVal -  the value of the song title (ie Happy Day).\n" +
                "Example:\n" +
                "    Music3 Artist Famous Band AlbumTitle Songs About Life Awards 10 SongTitle Happy Day \n" +
                "**Warning** This program will actually place an item\n" +
                "            that you specify into a table!\n";

        if (args.length < 9) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // snippet-start:[dynamodb.java2.put_item.main]
        String tableName = args[0];
        String key = args[1];
        String keyVal = args[2];
        String albumTitle = args[3];
        String albumTitleValue = args[4];
        String awards = args[5];
        String awardVal = args[6];
        String songTitle = args[7];
        String songTitleVal = args[8];

        // Create the DynamoDbClient object
        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

        HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();

        // Add content to the table
        itemValues.put(key, AttributeValue.builder().s(keyVal).build());
        itemValues.put(songTitle, AttributeValue.builder().s(songTitleVal).build());
        itemValues.put(albumTitle, AttributeValue.builder().s(albumTitleValue).build());
        itemValues.put(awards, AttributeValue.builder().s(awardVal).build());

        // Create a PutItemRequest object
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            ddb.putItem(request);
            System.out.println(tableName +" was successfully updated");

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamodb.java2.put_item.main]
        System.out.println("Done!");
    }
}
// snippet-end:[dynamodb.java2.put_item.complete]
