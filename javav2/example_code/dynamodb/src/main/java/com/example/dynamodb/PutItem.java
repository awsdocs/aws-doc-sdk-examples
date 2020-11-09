//snippet-sourcedescription:[PutItem.java demonstrates how to place an item into an Amazon DynamoDB table.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/30/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.put_item.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import java.util.HashMap;
// snippet-end:[dynamodb.java2.put_item.import]

public class PutItem {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    PutItem <tableName> <key> <keyVal> <albumtitle> <albumtitleval> <awards> <awardsval> <Songtitle> <songtitleval>\n\n" +
                "Where:\n" +
                "    tableName - the Amazon DynamoDB table in which an item is placed (for example, Music3).\n" +
                "    key - the key used in the Amazon DynamoDB table (for example, Artist).\n" +
                "    keyval - the key value that represents the item to get (for example, Famous Band).\n" +
                "    albumTitle - album title (for example, AlbumTitle).\n" +
                "    AlbumTitleValue - the name of the album (for example, Songs About Life ).\n" +
                "    Awards - the awards column (for example, Awards).\n" +
                "    AwardVal - the value of the awards (for example, 10).\n" +
                "    SongTitle - the song title (for example, SongTitle).\n" +
                "    SongTitleVal - the value of the song title (for example, Happy Day).\n" +
                "Example:\n" +
                "    Music3 Artist Famous Band AlbumTitle Songs About Life Awards 10 SongTitle Happy Day \n" +
                "**Warning** This program will actually place an item that you specify into a table!\n";

        if (args.length != 9) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String tableName = args[0];
        String key = args[1];
        String keyVal = args[2];
        String albumTitle = args[3];
        String albumTitleValue = args[4];
        String awards = args[5];
        String awardVal = args[6];
        String songTitle = args[7];
        String songTitleVal = args[8];

        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        putItemInTable(ddb, tableName, key, keyVal, albumTitle, albumTitleValue, awards, awardVal, songTitle, songTitleVal);
        System.out.println("Done!");
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.put_item.main]
    public static void putItemInTable(DynamoDbClient ddb,
                                      String tableName,
                                      String key,
                                      String keyVal,
                                      String albumTitle,
                                      String albumTitleValue,
                                      String awards,
                                      String awardVal,
                                      String songTitle,
                                      String songTitleVal){

        HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();

        // Add all content to the table
        itemValues.put(key, AttributeValue.builder().s(keyVal).build());
        itemValues.put(songTitle, AttributeValue.builder().s(songTitleVal).build());
        itemValues.put(albumTitle, AttributeValue.builder().s(albumTitleValue).build());
        itemValues.put(awards, AttributeValue.builder().s(awardVal).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            ddb.putItem(request);
            System.out.println(tableName +" was successfully updated");

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[dynamodb.java2.put_item.main]
    }
}
