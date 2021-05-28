/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.ppe;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class PPEHandler implements RequestHandler<Map<String,String>, String> {

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        LambdaLogger logger = context.getLogger();
        String bucketName = event.get("bucketName");
        logger.log("Bucket name is: " + bucketName);

        S3Service s3Service = new S3Service() ;
        DynamoDBService ddb = new DynamoDBService();
        AnalyzePhotos photos = new AnalyzePhotos();
        SendEmail email = new SendEmail();

        List<String> items = s3Service.listBucketObjects(bucketName);
        List<ArrayList<GearItem>> myList = new ArrayList<>();
        for (String item : items) {

            byte[] keyData = s3Service.getObjectBytes(bucketName, item);

            // Analyze the photo and return a list where each element is a WorkItem.
            ArrayList<GearItem> gearItem = photos.detectLabels(keyData, item);

            // Only add a list with items.
            if (gearItem != null)
                myList.add(gearItem);
        }

        ddb.persistItem(myList);

        // Create a new list with only unique keys to email.
        Set<String> unqiueKeys = createUniqueList(myList);
        email.sendMsg(unqiueKeys);
        logger.log("Updated the DynamoDB table with PPE data");
        return bucketName;
    }

    // Create a list of unique keys.
    private static Set<String> createUniqueList(List<ArrayList<GearItem>> gearList) {


        List<String> keys = new ArrayList<>();

        // Persist the data into a DynamoDB table.
        for (Object o : gearList) {

            //Need to get the WorkItem from each list.
            List innerList = (List) o;

            for (Object value : innerList) {
                GearItem gearItem = (GearItem) value;
                keys.add(gearItem.getKey());
            }
        }

        // create list without duplicates image names...
        Set<String> uniqueKeys = new HashSet<String>(keys);
        return uniqueKeys;
    }
}


