//snippet-sourcedescription:[PutItemEncrypt.java demonstrates how to place an encrypted item into an Amazon DynamoDB table.]
//snippet-keyword:[SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.dynamodb;

// snippet-start:[dynamodb.java2.put_item_enc.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import java.util.HashMap;
// snippet-end:[dynamodb.java2.put_item_enc.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutItemEncrypt {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <tableName> <key> <keyVal> <albumtitle> <albumtitleval> <awards> <awardsval> <Songtitle> <songtitleval>\n\n" +
                "Where:\n" +
                "    tableName - The Amazon DynamoDB table in which an item is placed (for example, Music3).\n" +
                "    key - The key used in the Amazon DynamoDB table (for example, Artist).\n" +
                "    keyval - The key value that represents the item to get (for example, Famous Band).\n" +
                "    albumTitle - The album title (for example, AlbumTitle).\n" +
                "    AlbumTitleValue - The name of the album (for example, Songs About Life ).\n" +
                "    Awards - The awards column (for example, Awards).\n" +
                "    AwardVal - The value of the awards (for example, 10).\n" +
                "    SongTitle - The song title (for example, SongTitle).\n" +
                "    SongTitleVal - The value of the song title (for example, Happy Day).\n" +
                "    keyId - A KMS key id value to use to encrypt/decrypt the data (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab).";

        if (args.length != 10) {
            System.out.println(usage);
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
        String keyId = args[9] ;

        Region region = Region.US_WEST_2;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        // Create a KmsClient object to use to encrpt data.
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        putItemInTable(ddb, kmsClient, tableName, key, keyVal, albumTitle, albumTitleValue, awards, awardVal, songTitle, songTitleVal, keyId);
        System.out.println("Done!");
        ddb.close();
    }

    // snippet-start:[dynamodb.java2.put_item_enc.main]
   public static void putItemInTable(DynamoDbClient ddb,
                                      KmsClient kmsClient,
                                      String tableName,
                                      String key,
                                      String keyVal,
                                      String albumTitle,
                                      String albumTitleValue,
                                      String awards,
                                      String awardVal,
                                      String songTitle,
                                      String songTitleVal,
                                      String keyId ){

        HashMap<String,AttributeValue> itemValues = new HashMap<>();
        SdkBytes myBytes = SdkBytes.fromUtf8String(albumTitleValue);
        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(myBytes)
                .build();

        EncryptResponse response = kmsClient.encrypt(encryptRequest);
        SdkBytes encryptedData = response.ciphertextBlob();

        // Add content to the table.
        itemValues.put(key, AttributeValue.builder().s(keyVal).build());
        itemValues.put(songTitle, AttributeValue.builder().s(songTitleVal).build());
        itemValues.put(albumTitle, AttributeValue.builder().bs(encryptedData).build());
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
    }
    // snippet-end:[dynamodb.java2.put_item_enc.main]
}

