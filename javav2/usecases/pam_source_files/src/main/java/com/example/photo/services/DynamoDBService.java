/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.services;

import com.example.photo.PhotoApplicationResources;
import com.example.photo.Label;
import com.example.photo.WorkCount;
import com.example.photo.LabelCount;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class DynamoDBService {
    private DynamoDbClient getClient() {
        return DynamoDbClient.builder()
                .region(PhotoApplicationResources.REGION)
                .build();
    }

    // Insert tag data into an Amazon DynamoDB table.
    public void putRecord(List<List<LabelCount>> list) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getClient())
                .build();

        DynamoDbTable<com.example.photo.Label> table = enhancedClient.table(PhotoApplicationResources.LABELS_TABLE,
                TableSchema.fromBean(Label.class));

        for (List<LabelCount> innerList : list) {
            for (LabelCount wi : innerList) {
                addSingleRecord(table, wi.getName(), wi.getKey());
            }
        }
    }

    private void addSingleRecord(DynamoDbTable<Label> table, String tag, String key) {
        // Check to see if the label exists in the Amazon DynamoDB table.
        // The count item uses an @DynamoDbAtomicCounter which means it is
        // updated automatically. No need to manually set this value when the record is
        // created or updated.
        if (!checkTagExists(table, tag)) {
            Label photoRec = new Label();
            photoRec.setId(tag);
            List<String> keyList = new ArrayList<>();
            keyList.add(key);
            photoRec.setImages(keyList);
            table.putItem(photoRec);
        } else {
            // The tag exists in the table.
            Key myKey = Key.builder()
                    .partitionValue(tag)
                    .build();

            // Add the file name to the list.
            Label myPhoto = table.getItem(myKey);
            Label updatedPhoto = new Label();
            List<String> imageList = myPhoto.getImages();
            imageList.add(key);
            updatedPhoto.setId(tag);
            updatedPhoto.setImages(imageList);
            table.updateItem(updatedPhoto);
        }
    }

    private Boolean checkTagExists(DynamoDbTable<Label> table, String tag) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                .partitionValue(tag)
                .build());

        Iterator<Label> results = table.query(queryConditional).items().iterator();
        return results.hasNext();
    }

    public List<String> getImagesTag(String tag) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getClient())
                .build();

        DynamoDbTable<com.example.photo.Label> table = enhancedClient.table(PhotoApplicationResources.LABELS_TABLE,
                TableSchema.fromBean(Label.class));
        Key key = Key.builder()
                .partitionValue(tag)
                .build();

        // Get the item by using the key.
        Label result = table.getItem(r -> r.key(key));
        return (result == null) ? List.of() : result.getImages();
    }

    // Scan the table and send data back to the client.
    public Map<String, WorkCount> scanPhotoTable() {
        Map<String, WorkCount> myMap = new HashMap<>();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(getClient())
                .build();

        DynamoDbTable<Label> table = enhancedClient.table(PhotoApplicationResources.LABELS_TABLE,
                TableSchema.fromBean(Label.class));

        for (Label photo : table.scan().items()) {
            WorkCount wc = new WorkCount();
            wc.setCount(photo.getCount());
            myMap.put(photo.getId(), wc);
        }

        return myMap;
    }
}