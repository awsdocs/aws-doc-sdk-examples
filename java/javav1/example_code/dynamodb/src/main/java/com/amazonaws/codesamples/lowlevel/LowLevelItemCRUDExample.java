// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.LowLevelItemCRUDExample] 
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

public class LowLevelItemCRUDExample {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    static String tableName = "ProductCatalog";

    public static void main(String[] args) throws IOException {

        createItems();

        retrieveItem();

        // Perform various updates.
        updateMultipleAttributes();
        updateAddNewAttribute();
        updateExistingAttributeConditionally();

        // Delete the item.
        deleteItem();

    }

    private static void createItems() {
        try {
            Map<String, AttributeValue> item1 = new HashMap<String, AttributeValue>();
            item1.put("Id", new AttributeValue().withN("120"));
            item1.put("Title", new AttributeValue().withS("Book 120 Title"));
            item1.put("ISBN", new AttributeValue().withS("120-1111111111"));
            item1.put("Authors", new AttributeValue().withSS(Arrays.asList("Author12", "Author22")));
            item1.put("Price", new AttributeValue().withN("20.00"));
            item1.put("Category", new AttributeValue().withS("Book"));
            item1.put("Dimensions", new AttributeValue().withS("8.5x11.0x.75"));
            item1.put("InPublication", new AttributeValue().withBOOL(false));

            PutItemRequest putItemRequest1 = new PutItemRequest().withTableName(tableName).withItem(item1);
            client.putItem(putItemRequest1);

            Map<String, AttributeValue> item2 = new HashMap<String, AttributeValue>();
            item2.put("Id", new AttributeValue().withN("121"));
            item2.put("Title", new AttributeValue().withS("Book 121 Title"));
            item2.put("ISBN", new AttributeValue().withS("121-1111111111"));
            item2.put("Price", new AttributeValue().withN("20.00"));
            item2.put("ProductCategory", new AttributeValue().withS("Book"));
            item2.put("Authors", new AttributeValue().withSS(Arrays.asList("Author21", "Author22")));
            item1.put("Dimensions", new AttributeValue().withS("8.5x11.0x.75"));
            item1.put("InPublication", new AttributeValue().withBOOL(true));

            PutItemRequest putItemRequest2 = new PutItemRequest().withTableName(tableName).withItem(item2);
            client.putItem(putItemRequest2);
        }
        catch (AmazonServiceException ase) {
            System.err.println("Create items failed.");
        }
    }

    private static void retrieveItem() {
        try {

            HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("Id", new AttributeValue().withN("120"));
            GetItemRequest getItemRequest = new GetItemRequest().withTableName(tableName).withKey(key)
                .withProjectionExpression("Id, ISBN, Title, Authors");

            GetItemResult result = client.getItem(getItemRequest);

            // Check the response.
            System.out.println("Printing item after retrieving it....");
            printItem(result.getItem());

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to retrieve item in " + tableName);
        }

    }

    private static void updateAddNewAttribute() {
        try {
            HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("Id", new AttributeValue().withN("121"));

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
            expressionAttributeValues.put(":val1", new AttributeValue().withS("Some value"));

            ReturnValue returnValues = ReturnValue.ALL_NEW;

            UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(tableName).withKey(key)
                .withUpdateExpression("set NewAttribute = :val1")
                .withExpressionAttributeValues(expressionAttributeValues).withReturnValues(returnValues);

            UpdateItemResult result = client.updateItem(updateItemRequest);

            // Check the response.
            System.out.println("Printing item after adding new attribute...");
            printItem(result.getAttributes());

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to add new attribute in " + tableName);
            System.err.println(ase.getMessage());
        }
    }

    private static void updateMultipleAttributes() {
        try {

            HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("Id", new AttributeValue().withN("120"));

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
            expressionAttributeValues.put(":val1", new AttributeValue().withSS("Author YY", "Author ZZ"));
            expressionAttributeValues.put(":val2", new AttributeValue().withS("someValue"));

            ReturnValue returnValues = ReturnValue.ALL_NEW;

            UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(tableName).withKey(key)
                .withUpdateExpression("add Authors :val1 set NewAttribute=:val2")
                .withExpressionAttributeValues(expressionAttributeValues).withReturnValues(returnValues);

            UpdateItemResult result = client.updateItem(updateItemRequest);

            // Check the response.
            System.out.println("Printing item after multiple attribute update...");
            printItem(result.getAttributes());

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to update multiple attributes in " + tableName);
            System.out.println(ase.getMessage()); // DELETEME
            System.err.println("Failed to update multiple attributes in " + tableName); // DELETEME
        }
    }

    private static void updateExistingAttributeConditionally() {
        try {

            HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("Id", new AttributeValue().withN("120"));

            // Specify the desired price (25.00) and also the condition (price =
            // 20.00)

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
            expressionAttributeValues.put(":val1", new AttributeValue().withN("25.00"));
            expressionAttributeValues.put(":val2", new AttributeValue().withN("20.00"));

            ReturnValue returnValues = ReturnValue.ALL_NEW;

            UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(tableName).withKey(key)
                .withUpdateExpression("set Price = :val1").withConditionExpression("Price = :val2")
                .withExpressionAttributeValues(expressionAttributeValues).withReturnValues(returnValues);

            UpdateItemResult result = client.updateItem(updateItemRequest);

            // Check the response.
            System.out.println("Printing item after conditional update to new attribute...");
            printItem(result.getAttributes());
        }
        catch (ConditionalCheckFailedException cse) {
            // Reload object and retry code.
            System.err.println("Conditional check failed in " + tableName);
        }
        catch (AmazonServiceException ase) {
            System.err.println("Error updating item in " + tableName);
        }
    }

    private static void deleteItem() {
        try {

            HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            key.put("Id", new AttributeValue().withN("120"));

            Map<String, AttributeValue> expressionAttributeValues = new HashMap<String, AttributeValue>();
            expressionAttributeValues.put(":val", new AttributeValue().withBOOL(false));

            ReturnValue returnValues = ReturnValue.ALL_OLD;

            DeleteItemRequest deleteItemRequest = new DeleteItemRequest().withTableName(tableName).withKey(key)
                .withConditionExpression("InPublication = :val")
                .withExpressionAttributeValues(expressionAttributeValues).withReturnValues(returnValues);

            DeleteItemResult result = client.deleteItem(deleteItemRequest);

            // Check the response.
            System.out.println("Printing item that was deleted...");
            printItem(result.getAttributes());

        }
        catch (AmazonServiceException ase) {
            System.err.println("Failed to get item after deletion " + tableName);
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

// snippet-end:[dynamodb.java.codeexample.LowLevelItemCRUDExample] 