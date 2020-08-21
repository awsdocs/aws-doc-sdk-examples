// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.LowLevelScan] 
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/


package com.amazonaws.codesamples.lowlevel;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class LowLevelScan {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    static String tableName = "ProductCatalog";

    public static void main(String[] args) throws Exception {

        findProductsForPriceLessThanZero();
    }

    private static void findProductsForPriceLessThanZero() {

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":pr", new AttributeValue().withN("100"));

        ScanRequest scanRequest = new ScanRequest().withTableName(tableName).withFilterExpression("Price < :pr")
            .withExpressionAttributeValues(expressionAttributeValues)
            .withProjectionExpression("Id, Title, ProductCategory, Price");

        ScanResult result = client.scan(scanRequest);

        System.out.println("Scan of " + tableName + " for items with a price less than 100.");
        for (Map<String, AttributeValue> item : result.getItems()) {
            System.out.println("");
            printItem(item);
        }
    }

    private static void printItem(Map<String, AttributeValue> attributeList) {
        for (Map.Entry<String, AttributeValue> item : attributeList.entrySet()) {
            String attributeName = item.getKey();
            AttributeValue value = item.getValue();
            System.out.println(attributeName + " " + (value.getS() == null ? "" : "S=[" + value.getS() + "]")
                + (value.getN() == null ? "" : "N=[" + value.getN() + "]")
                + (value.getB() == null ? "" : "B=[" + value.getB() + "]")
                + (value.getSS() == null ? "" : "SS=[" + value.getSS() + "]")
                + (value.getNS() == null ? "" : "NS=[" + value.getNS() + "]")
                + (value.getBS() == null ? "" : "BS=[" + value.getBS() + "] \n"));
        }
    }
}

// snippet-end:[dynamodb.java.codeexample.LowLevelScan] 