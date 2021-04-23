/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ppe;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DynamoDBService {

    private DynamoDbClient getClient() {

        // Create a DynamoDbClient object.
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        return ddb;
    }

    // Persist the PPE Items into the Gear table.
    public void persistItem(List<ArrayList<GearItem>> gearList) {
        DynamoDbClient ddb = getClient();

        try {

            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();

            DynamoDbTable<Gear> gearTable = enhancedClient.table("Gear", TableSchema.fromBean(Gear.class));
            Gear gearRecord;

            // Create an Instant.
            LocalDateTime now = LocalDateTime.now(); // current date and time
            LocalDateTime timeVal = now.toLocalDate().atStartOfDay();
            Instant instant = timeVal.toInstant(ZoneOffset.UTC);

            // Persist the data into a DynamoDB table.
            for (Object o : gearList) {

                //Need to get the WorkItem from each list.
                List innerList = (List) o;

                for (Object value : innerList) {

                    gearRecord = new Gear();
                    UUID uuid = UUID.randomUUID();
                    GearItem gearItem = (GearItem) value;

                    gearRecord.setId(uuid.toString());
                    gearRecord.setKey(gearItem.getKey());
                    gearRecord.setDate(instant.toString());
                    gearRecord.setItem(gearItem.getName());
                    gearRecord.setCoverDescription(gearItem.getBodyCoverDescription());
                    gearRecord.setItemDescription(gearItem.getItemDescription());
                    gearRecord.setConfidence(gearItem.getConfidence());

                    // Put PPE data into a DynamoDB table.
                    gearTable.putItem(gearRecord);
                }
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
   }
}