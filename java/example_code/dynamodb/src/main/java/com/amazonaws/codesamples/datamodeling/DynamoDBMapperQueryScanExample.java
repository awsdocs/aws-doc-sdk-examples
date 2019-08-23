// snippet-sourcedescription:[ ]
// snippet-service:[dynamodb]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon DynamoDB]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ ]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[ ]
// snippet-sourceauthor:[AWS]
// snippet-start:[dynamodb.java.codeexample.DynamoDBMapperQueryScanExample] 
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


package com.amazonaws.codesamples.datamodeling;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DynamoDBMapperQueryScanExample {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

    public static void main(String[] args) throws Exception {
        try {

            DynamoDBMapper mapper = new DynamoDBMapper(client);

            // Get a book - Id=101
            GetBook(mapper, 101);
            // Sample forum and thread to test queries.
            String forumName = "Amazon DynamoDB";
            String threadSubject = "DynamoDB Thread 1";
            // Sample queries.
            FindRepliesInLast15Days(mapper, forumName, threadSubject);
            FindRepliesPostedWithinTimePeriod(mapper, forumName, threadSubject);

            // Scan a table and find book items priced less than specified
            // value.
            FindBooksPricedLessThanSpecifiedValue(mapper, "20");

            // Scan a table with multiple threads and find bicycle items with a
            // specified bicycle type
            int numberOfThreads = 16;
            FindBicyclesOfSpecificTypeWithMultipleThreads(mapper, numberOfThreads, "Road");

            System.out.println("Example complete!");

        }
        catch (Throwable t) {
            System.err.println("Error running the DynamoDBMapperQueryScanExample: " + t);
            t.printStackTrace();
        }
    }

    private static void GetBook(DynamoDBMapper mapper, int id) throws Exception {
        System.out.println("GetBook: Get book Id='101' ");
        System.out.println("Book table has no sort key. You can do GetItem, but not Query.");
        Book book = mapper.load(Book.class, id);
        System.out.format("Id = %s Title = %s, ISBN = %s %n", book.getId(), book.getTitle(), book.getISBN());
    }

    private static void FindRepliesInLast15Days(DynamoDBMapper mapper, String forumName, String threadSubject)
        throws Exception {
        System.out.println("FindRepliesInLast15Days: Replies within last 15 days.");

        String partitionKey = forumName + "#" + threadSubject;

        long twoWeeksAgoMilli = (new Date()).getTime() - (15L * 24L * 60L * 60L * 1000L);
        Date twoWeeksAgo = new Date();
        twoWeeksAgo.setTime(twoWeeksAgoMilli);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String twoWeeksAgoStr = dateFormatter.format(twoWeeksAgo);

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(partitionKey));
        eav.put(":val2", new AttributeValue().withS(twoWeeksAgoStr.toString()));

        DynamoDBQueryExpression<Reply> queryExpression = new DynamoDBQueryExpression<Reply>()
            .withKeyConditionExpression("Id = :val1 and ReplyDateTime > :val2").withExpressionAttributeValues(eav);

        List<Reply> latestReplies = mapper.query(Reply.class, queryExpression);

        for (Reply reply : latestReplies) {
            System.out.format("Id=%s, Message=%s, PostedBy=%s %n, ReplyDateTime=%s %n", reply.getId(),
                reply.getMessage(), reply.getPostedBy(), reply.getReplyDateTime());
        }
    }

    private static void FindRepliesPostedWithinTimePeriod(DynamoDBMapper mapper, String forumName, String threadSubject)
        throws Exception {
        String partitionKey = forumName + "#" + threadSubject;

        System.out.println(
            "FindRepliesPostedWithinTimePeriod: Find replies for thread Message = 'DynamoDB Thread 2' posted within a period.");
        long startDateMilli = (new Date()).getTime() - (14L * 24L * 60L * 60L * 1000L); // Two
                                                                                        // weeks
                                                                                        // ago.
        long endDateMilli = (new Date()).getTime() - (7L * 24L * 60L * 60L * 1000L); // One
                                                                                     // week
                                                                                     // ago.
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String startDate = dateFormatter.format(startDateMilli);
        String endDate = dateFormatter.format(endDateMilli);

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(partitionKey));
        eav.put(":val2", new AttributeValue().withS(startDate));
        eav.put(":val3", new AttributeValue().withS(endDate));

        DynamoDBQueryExpression<Reply> queryExpression = new DynamoDBQueryExpression<Reply>()
            .withKeyConditionExpression("Id = :val1 and ReplyDateTime between :val2 and :val3")
            .withExpressionAttributeValues(eav);

        List<Reply> betweenReplies = mapper.query(Reply.class, queryExpression);

        for (Reply reply : betweenReplies) {
            System.out.format("Id=%s, Message=%s, PostedBy=%s %n, PostedDateTime=%s %n", reply.getId(),
                reply.getMessage(), reply.getPostedBy(), reply.getReplyDateTime());
        }

    }

    private static void FindBooksPricedLessThanSpecifiedValue(DynamoDBMapper mapper, String value) throws Exception {

        System.out.println("FindBooksPricedLessThanSpecifiedValue: Scan ProductCatalog.");

        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withN(value));
        eav.put(":val2", new AttributeValue().withS("Book"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("Price < :val1 and ProductCategory = :val2").withExpressionAttributeValues(eav);

        List<Book> scanResult = mapper.scan(Book.class, scanExpression);

        for (Book book : scanResult) {
            System.out.println(book);
        }
    }

    private static void FindBicyclesOfSpecificTypeWithMultipleThreads(DynamoDBMapper mapper, int numberOfThreads,
        String bicycleType) throws Exception {

        System.out.println("FindBicyclesOfSpecificTypeWithMultipleThreads: Scan ProductCatalog With Multiple Threads.");
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS("Bicycle"));
        eav.put(":val2", new AttributeValue().withS(bicycleType));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression("ProductCategory = :val1 and BicycleType = :val2").withExpressionAttributeValues(eav);

        List<Bicycle> scanResult = mapper.parallelScan(Bicycle.class, scanExpression, numberOfThreads);
        for (Bicycle bicycle : scanResult) {
            System.out.println(bicycle);
        }
    }

    @DynamoDBTable(tableName = "ProductCatalog")
    public static class Book {
        private int id;
        private String title;
        private String ISBN;
        private int price;
        private int pageCount;
        private String productCategory;
        private boolean inPublication;

        @DynamoDBHashKey(attributeName = "Id")
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @DynamoDBAttribute(attributeName = "Title")
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @DynamoDBAttribute(attributeName = "ISBN")
        public String getISBN() {
            return ISBN;
        }

        public void setISBN(String ISBN) {
            this.ISBN = ISBN;
        }

        @DynamoDBAttribute(attributeName = "Price")
        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        @DynamoDBAttribute(attributeName = "PageCount")
        public int getPageCount() {
            return pageCount;
        }

        public void setPageCount(int pageCount) {
            this.pageCount = pageCount;
        }

        @DynamoDBAttribute(attributeName = "ProductCategory")
        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        @DynamoDBAttribute(attributeName = "InPublication")
        public boolean getInPublication() {
            return inPublication;
        }

        public void setInPublication(boolean inPublication) {
            this.inPublication = inPublication;
        }

        @Override
        public String toString() {
            return "Book [ISBN=" + ISBN + ", price=" + price + ", product category=" + productCategory + ", id=" + id
                + ", title=" + title + "]";
        }

    }

    @DynamoDBTable(tableName = "ProductCatalog")
    public static class Bicycle {
        private int id;
        private String title;
        private String description;
        private String bicycleType;
        private String brand;
        private int price;
        private List<String> color;
        private String productCategory;

        @DynamoDBHashKey(attributeName = "Id")
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @DynamoDBAttribute(attributeName = "Title")
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @DynamoDBAttribute(attributeName = "Description")
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @DynamoDBAttribute(attributeName = "BicycleType")
        public String getBicycleType() {
            return bicycleType;
        }

        public void setBicycleType(String bicycleType) {
            this.bicycleType = bicycleType;
        }

        @DynamoDBAttribute(attributeName = "Brand")
        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        @DynamoDBAttribute(attributeName = "Price")
        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        @DynamoDBAttribute(attributeName = "Color")
        public List<String> getColor() {
            return color;
        }

        public void setColor(List<String> color) {
            this.color = color;
        }

        @DynamoDBAttribute(attributeName = "ProductCategory")
        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        @Override
        public String toString() {
            return "Bicycle [Type=" + bicycleType + ", color=" + color + ", price=" + price + ", product category="
                + productCategory + ", id=" + id + ", title=" + title + "]";
        }

    }

    @DynamoDBTable(tableName = "Reply")
    public static class Reply {
        private String id;
        private String replyDateTime;
        private String message;
        private String postedBy;

        // Partition key
        @DynamoDBHashKey(attributeName = "Id")
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        // Range key
        @DynamoDBRangeKey(attributeName = "ReplyDateTime")
        public String getReplyDateTime() {
            return replyDateTime;
        }

        public void setReplyDateTime(String replyDateTime) {
            this.replyDateTime = replyDateTime;
        }

        @DynamoDBAttribute(attributeName = "Message")
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @DynamoDBAttribute(attributeName = "PostedBy")
        public String getPostedBy() {
            return postedBy;
        }

        public void setPostedBy(String postedBy) {
            this.postedBy = postedBy;
        }
    }

    @DynamoDBTable(tableName = "Thread")
    public static class Thread {
        private String forumName;
        private String subject;
        private String message;
        private String lastPostedDateTime;
        private String lastPostedBy;
        private Set<String> tags;
        private int answered;
        private int views;
        private int replies;

        // Partition key
        @DynamoDBHashKey(attributeName = "ForumName")
        public String getForumName() {
            return forumName;
        }

        public void setForumName(String forumName) {
            this.forumName = forumName;
        }

        // Range key
        @DynamoDBRangeKey(attributeName = "Subject")
        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        @DynamoDBAttribute(attributeName = "Message")
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @DynamoDBAttribute(attributeName = "LastPostedDateTime")
        public String getLastPostedDateTime() {
            return lastPostedDateTime;
        }

        public void setLastPostedDateTime(String lastPostedDateTime) {
            this.lastPostedDateTime = lastPostedDateTime;
        }

        @DynamoDBAttribute(attributeName = "LastPostedBy")
        public String getLastPostedBy() {
            return lastPostedBy;
        }

        public void setLastPostedBy(String lastPostedBy) {
            this.lastPostedBy = lastPostedBy;
        }

        @DynamoDBAttribute(attributeName = "Tags")
        public Set<String> getTags() {
            return tags;
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
        }

        @DynamoDBAttribute(attributeName = "Answered")
        public int getAnswered() {
            return answered;
        }

        public void setAnswered(int answered) {
            this.answered = answered;
        }

        @DynamoDBAttribute(attributeName = "Views")
        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        @DynamoDBAttribute(attributeName = "Replies")
        public int getReplies() {
            return replies;
        }

        public void setReplies(int replies) {
            this.replies = replies;
        }

    }

    @DynamoDBTable(tableName = "Forum")
    public static class Forum {
        private String name;
        private String category;
        private int threads;

        @DynamoDBHashKey(attributeName = "Name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @DynamoDBAttribute(attributeName = "Category")
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        @DynamoDBAttribute(attributeName = "Threads")
        public int getThreads() {
            return threads;
        }

        public void setThreads(int threads) {
            this.threads = threads;
        }
    }
}

// snippet-end:[dynamodb.java.codeexample.DynamoDBMapperQueryScanExample] 