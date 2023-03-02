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
        // Check to see if this tag exists in the Amazon DynamoDB table.
        if (!checkTagExists(table, tag)) {
            Photos photoRec = new Photos();
            photoRec.setCount(1);
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

            // Get the item by using the key.
            Photos myPhoto = table.getItem(myKey);
            int tagCount = myPhoto.getCount();
            System.out.println("******* The current tag count is " + tagCount);

            // Update the count.
            int newCount = tagCount+1;
            myPhoto.setCount(newCount);

            // Add the file name to the list.
            List<String> imageList = myPhoto.getImages();
            imageList.add(key);
            myPhoto.setImages(imageList);
            System.out.println("The current count is "+myPhoto.getCount());
            table.updateItem(myPhoto);
            Photos myPhoto2 = table.getItem(myKey);
            System.out.println("The current count is "+myPhoto2.getCount());
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
            wi.setCount(photo.getCount());
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