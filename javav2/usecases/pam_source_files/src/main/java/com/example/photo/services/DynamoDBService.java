/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.photo.services;

import com.example.photo.Job;
import com.example.photo.PhotoApplicationResources;
import com.example.photo.Photos;
import com.example.photo.WorkItem;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import java.util.ArrayList;
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
    public void putRecord(List<List<WorkItem>> list) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getClient())
            .build();

        DynamoDbTable<com.example.photo.Photos> table = enhancedClient.table(PhotoApplicationResources.TAGS_TABLE, TableSchema.fromBean(Photos.class));
        for (List<WorkItem> innerList : list) {
            for (WorkItem wi : innerList) {
                addSingleRecord(table, wi.getName(), wi.getKey());
            }
        }
    }

    private void addSingleRecord(DynamoDbTable<Photos> table, String tag, String key) {
        // Check to see if the label exists in the Amazon DynamoDB table.
        // The count item uses an @DynamoDbAtomicCounter which means it is
        // updated automatically. No need to manually set this value when the record is
        // created or updated.
        if (!checkTagExists(table, tag)) {
            Photos photoRec = new Photos();
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
            Photos myPhoto = table.getItem(myKey);
            Photos updatedPhoto = new Photos();
            List<String> imageList = myPhoto.getImages();
            imageList.add(key);
            updatedPhoto.setId(tag);
            updatedPhoto.setImages(imageList);
            table.updateItem(updatedPhoto);
        }
    }

    private Boolean checkTagExists(DynamoDbTable<Photos> table, String tag) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
            .partitionValue(tag)
            .build());

        Iterator<Photos> results = table.query(queryConditional).items().iterator();
        return results.hasNext();
    }

    public List<String> getImagesTag(String tag) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getClient())
            .build();

        DynamoDbTable<com.example.photo.Photos> table = enhancedClient.table("Photo", TableSchema.fromBean(Photos.class));
        Key key = Key.builder()
            .partitionValue(tag)
            .build();

        // Get the item by using the key.
        Photos result = table.getItem(r->r.key(key));
        return result.getImages();
    }

    // Scan the table to send data back to the client.
    public List<WorkItem> scanPhotoTable() {
        ArrayList<WorkItem> dataList = new ArrayList<>();
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getClient())
            .build();

        DynamoDbTable<Photos> table = enhancedClient.table(PhotoApplicationResources.TAGS_TABLE, TableSchema.fromBean(Photos.class));
        for (Photos photo : table.scan().items()) {
            WorkItem wi = new WorkItem();
            wi.setKey(photo.getId());

            // Count uses 0 based value. Add 1 to value to get accurate label count.
            int myCount = photo.getCount() + 1;
            wi.setCount(myCount);
            String listString = String.join(", ", photo.getImages());
            wi.setName(listString);
            dataList.add(wi);
        }

        return dataList;
    }

    /**
     * Store the subscription and topic with the notification destination of a job.
     * @param job to watch for completion.
     */
    public void putSubscription(Job job) {
        getClient().putItem(
                PutItemRequest.builder()
                        .tableName(PhotoApplicationResources.JOBS_TABLE)
                        .item(Map.of(
                                "job", AttributeValue.builder().s(job.getJobId()).build(),
                                "topic", AttributeValue.builder().s(job.getTopicArn()).build()))
                .build());
    }
}