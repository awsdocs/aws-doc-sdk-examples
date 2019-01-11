// snippet-sourcedescription:[LowLevelItemBinaryExample.java demonstrates how to ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.Java.CodeExample.f18bdd83-ef54-4c98-8789-7f1e0e465250] 

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

public class LowLevelItemBinaryExample {

    static AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
    static String tableName = "Reply";
    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static void main(String[] args) throws IOException {
        try {

            // Format the primary key values
            String threadId = "Amazon DynamoDB#DynamoDB Thread 2";

            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            String replyDateTime = dateFormatter.format(new Date());

            // Add a new reply with a binary attribute type
            createItem(threadId, replyDateTime);

            // Retrieve the reply with a binary attribute type
            retrieveItem(threadId, replyDateTime);

            // clean up by deleting the item
            deleteItem(threadId, replyDateTime);
        }
        catch (Exception e) {
            System.err.println("Error running the binary attribute type example: " + e);
            e.printStackTrace(System.err);
        }
    }

    public static void createItem(String threadId, String replyDateTime) throws IOException {
        // Craft a long message
        String messageInput = "Long message to be compressed in a lengthy forum reply";

        // Compress the long message
        ByteBuffer compressedMessage = compressString(messageInput.toString());

        // Add a the reply
        Map<String, AttributeValue> replyInput = new HashMap<String, AttributeValue>();
        replyInput.put("Id", new AttributeValue().withS(threadId));
        replyInput.put("ReplyDateTime", new AttributeValue().withS(replyDateTime));
        replyInput.put("Message", new AttributeValue().withS("Long message follows"));
        replyInput.put("ExtendedMessage", new AttributeValue().withB(compressedMessage));
        replyInput.put("PostedBy", new AttributeValue().withS("User A"));

        PutItemRequest putReplyRequest = new PutItemRequest().withTableName(tableName).withItem(replyInput);

        client.putItem(putReplyRequest);
    }

    public static void retrieveItem(String threadId, String replyDateTime) throws IOException {
        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("Id", new AttributeValue().withS(threadId));
        key.put("ReplyDateTime", new AttributeValue().withS(replyDateTime));

        GetItemRequest getReplyRequest = new GetItemRequest().withTableName(tableName).withKey(key)
            .withConsistentRead(true);

        GetItemResult getReplyResult = client.getItem(getReplyRequest);

        // Decompress the reply message and print
        Map<String, AttributeValue> reply = getReplyResult.getItem();
        String message = decompressString(reply.get("ExtendedMessage").getB());
        System.out.println("Reply message:\n" + " Id: " + reply.get("Id").getS() + "\n" + " ReplyDateTime: "
            + reply.get("ReplyDateTime").getS() + "\n" + " PostedBy: " + reply.get("PostedBy").getS() + "\n"
            + " Message: " + reply.get("Message").getS() + "\n" + " ExtendedMessage (decompressed): " + message);
    }

    public static void deleteItem(String threadId, String replyDateTime) {
        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put("Id", new AttributeValue().withS(threadId));
        key.put("ReplyDateTime", new AttributeValue().withS(replyDateTime));

        DeleteItemRequest deleteReplyRequest = new DeleteItemRequest().withTableName(tableName).withKey(key);
        client.deleteItem(deleteReplyRequest);
    }

    private static ByteBuffer compressString(String input) throws IOException {
        // Compress the UTF-8 encoded String into a byte[]
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream os = new GZIPOutputStream(baos);
        os.write(input.getBytes("UTF-8"));
        os.finish();
        byte[] compressedBytes = baos.toByteArray();

        // The following code writes the compressed bytes to a ByteBuffer.
        // A simpler way to do this is by simply calling
        // ByteBuffer.wrap(compressedBytes);
        // However, the longer form below shows the importance of resetting the
        // position of the buffer
        // back to the beginning of the buffer if you are writing bytes directly
        // to it, since the SDK
        // will consider only the bytes after the current position when sending
        // data to DynamoDB.
        // Using the "wrap" method automatically resets the position to zero.
        ByteBuffer buffer = ByteBuffer.allocate(compressedBytes.length);
        buffer.put(compressedBytes, 0, compressedBytes.length);
        buffer.position(0); // Important: reset the position of the ByteBuffer
                            // to the beginning
        return buffer;
    }

    private static String decompressString(ByteBuffer input) throws IOException {
        byte[] bytes = input.array();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPInputStream is = new GZIPInputStream(bais);

        int chunkSize = 1024;
        byte[] buffer = new byte[chunkSize];
        int length = 0;
        while ((length = is.read(buffer, 0, chunkSize)) != -1) {
            baos.write(buffer, 0, length);
        }

        return new String(baos.toByteArray(), "UTF-8");
    }
}
// snippet-end:[dynamodb.Java.CodeExample.f18bdd83-ef54-4c98-8789-7f1e0e465250]