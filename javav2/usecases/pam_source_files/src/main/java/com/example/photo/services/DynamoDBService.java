package com.example.photo.services;

import com.example.photo.PhotoApplicationResources;
import com.example.photo.Label;
import com.example.photo.WorkCount;
import com.example.photo.LabelCount;
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

public class DynamoDBService {
    private DynamoDbClient getClient() {
        return DynamoDbClient.builder()
            .region(PhotoApplicationResources.REGION)
            .build();
    }

    // Insert label data into an Amazon DynamoDB table.
    public void putRecord(List<LabelCount> list) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getClient())
            .build();

        DynamoDbTable<com.example.photo.Label> table = enhancedClient.table(PhotoApplicationResources.LABELS_TABLE,
            TableSchema.fromBean(Label.class));

        for (LabelCount count : list) {
            addSingleRecord(table, count.getName(), count.getKey());
        }
    }

    private void addSingleRecord(DynamoDbTable<Label> table, String label, String key) {
        // Check to see if the label exists in the Amazon DynamoDB table.
        // The count item uses an @DynamoDbAtomicCounter which means it is
        // updated automatically. No need to manually set this value when the record is
        // created or updated.
        if (!checkLabelExists(table, label)) {
            Label photoRec = new Label();
            photoRec.setId(label);
            List<String> keyList = new ArrayList<>();
            keyList.add(key);
            photoRec.setImages(keyList);
            table.putItem(photoRec);
        } else {
            // The label exists in the table.
            Key myKey = Key.builder()
                .partitionValue(label)
                .build();

            // Add the file name to the list.
            Label myPhoto = table.getItem(myKey);
            Label updatedPhoto = new Label();
            List<String> imageList = myPhoto.getImages();
            imageList.add(key);
            updatedPhoto.setId(label);
            updatedPhoto.setImages(imageList);
            table.updateItem(updatedPhoto);
        }
    }

    private Boolean checkLabelExists(DynamoDbTable<Label> table, String label) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
            .partitionValue(label)
            .build());

        Iterator<Label> results = table.query(queryConditional).items().iterator();
        return results.hasNext();
    }

    public List<String> getImagesByLabel(String label) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(getClient())
            .build();

        DynamoDbTable<com.example.photo.Label> table = enhancedClient.table(PhotoApplicationResources.LABELS_TABLE,
            TableSchema.fromBean(Label.class));
        Key key = Key.builder()
            .partitionValue(label)
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