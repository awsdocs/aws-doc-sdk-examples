// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3.async;

// snippet-start:[s3.java2.async.selectObjectContentMethod.import]

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CSVInput;
import software.amazon.awssdk.services.s3.model.CSVOutput;
import software.amazon.awssdk.services.s3.model.CompressionType;
import software.amazon.awssdk.services.s3.model.ExpressionType;
import software.amazon.awssdk.services.s3.model.FileHeaderInfo;
import software.amazon.awssdk.services.s3.model.InputSerialization;
import software.amazon.awssdk.services.s3.model.JSONInput;
import software.amazon.awssdk.services.s3.model.JSONOutput;
import software.amazon.awssdk.services.s3.model.JSONType;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.OutputSerialization;
import software.amazon.awssdk.services.s3.model.Progress;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.SelectObjectContentRequest;
import software.amazon.awssdk.services.s3.model.SelectObjectContentResponseHandler;
import software.amazon.awssdk.services.s3.model.Stats;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
// snippet-end:[s3.java2.async.selectObjectContentMethod.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
// snippet-start:[s3.java2.async.selectObjectContentMethod.main]
public class SelectObjectContentExample {
    static final Logger logger = LoggerFactory.getLogger(SelectObjectContentExample.class);
    static final String BUCKET_NAME = "amzn-s3-demo-bucket-" + UUID.randomUUID();
    static final S3AsyncClient s3AsyncClient = S3AsyncClient.create();
    static String FILE_CSV = "csv";
    static String FILE_JSON = "json";
    static String URL_CSV = "https://raw.githubusercontent.com/mledoze/countries/master/dist/countries.csv";
    static String URL_JSON = "https://raw.githubusercontent.com/mledoze/countries/master/dist/countries.json";

    public static void main(String[] args) {
        SelectObjectContentExample selectObjectContentExample = new SelectObjectContentExample();
        try {
            SelectObjectContentExample.setUp();
            selectObjectContentExample.runSelectObjectContentMethodForJSON();
            selectObjectContentExample.runSelectObjectContentMethodForCSV();
        } catch (SdkException e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        } finally {
            SelectObjectContentExample.tearDown();
        }
    }

// snippet-start:[s3.java2.async.selectObjectContentMethod.json]
    EventStreamInfo runSelectObjectContentMethodForJSON() {
        // Set up request parameters.
        final String queryExpression = "select * from s3object[*][*] c where c.area < 350000";
        final String fileType = FILE_JSON;

        InputSerialization inputSerialization = InputSerialization.builder()
                .json(JSONInput.builder().type(JSONType.DOCUMENT).build())
                .compressionType(CompressionType.NONE)
                .build();

        OutputSerialization outputSerialization = OutputSerialization.builder()
                .json(JSONOutput.builder().recordDelimiter(null).build())
                .build();

        // Build the SelectObjectContentRequest.
        SelectObjectContentRequest select = SelectObjectContentRequest.builder()
                .bucket(BUCKET_NAME)
                .key(FILE_JSON)
                .expression(queryExpression)
                .expressionType(ExpressionType.SQL)
                .inputSerialization(inputSerialization)
                .outputSerialization(outputSerialization)
                .build();

        EventStreamInfo eventStreamInfo = new EventStreamInfo();
        // Call the selectObjectContent method with the request and a response handler.
        // Supply an EventStreamInfo object to the response handler to gather records and information from the response.
        s3AsyncClient.selectObjectContent(select, buildResponseHandler(eventStreamInfo)).join();

        // Log out information gathered while processing the response stream.
        long recordCount = eventStreamInfo.getRecords().stream().mapToInt(record ->
                record.split("\n").length
        ).sum();
        logger.info("Total records {}: {}", fileType, recordCount);
        logger.info("Visitor onRecords for fileType {} called {} times", fileType, eventStreamInfo.getCountOnRecordsCalled());
        logger.info("Visitor onStats for fileType {}, {}", fileType, eventStreamInfo.getStats());
        logger.info("Visitor onContinuations for fileType {}, {}", fileType, eventStreamInfo.getCountContinuationEvents());
        return eventStreamInfo;
    }
// snippet-end:[s3.java2.async.selectObjectContentMethod.json]

// snippet-start:[s3.java2.async.selectObjectContentMethod.response-handler]
    static SelectObjectContentResponseHandler buildResponseHandler(EventStreamInfo eventStreamInfo) {
        // Use a Visitor to process the response stream. This visitor logs information and gathers details while processing.
        final SelectObjectContentResponseHandler.Visitor visitor = SelectObjectContentResponseHandler.Visitor.builder()
                .onRecords(r -> {
                    logger.info("Record event received.");
                    eventStreamInfo.addRecord(r.payload().asUtf8String());
                    eventStreamInfo.incrementOnRecordsCalled();
                })
                .onCont(ce -> {
                    logger.info("Continuation event received.");
                    eventStreamInfo.incrementContinuationEvents();
                })
                .onProgress(pe -> {
                    Progress progress = pe.details();
                    logger.info("Progress event received:\n bytesScanned:{}\nbytesProcessed: {}\nbytesReturned:{}",
                            progress.bytesScanned(),
                            progress.bytesProcessed(),
                            progress.bytesReturned());
                })
                .onEnd(ee -> logger.info("End event received."))
                .onStats(se -> {
                    logger.info("Stats event received.");
                    eventStreamInfo.addStats(se.details());
                })
                .build();

        // Build the SelectObjectContentResponseHandler with the visitor that processes the stream.
        return SelectObjectContentResponseHandler.builder()
                .subscriber(visitor).build();
    }
// snippet-end:[s3.java2.async.selectObjectContentMethod.response-handler]

// snippet-start:[s3.java2.async.selectObjectContentMethod.stream-info]
    // The EventStreamInfo class is used to store information gathered while processing the response stream.
    static class EventStreamInfo {
        private final List<String> records = new ArrayList<>();
        private Integer countOnRecordsCalled = 0;
        private Integer countContinuationEvents = 0;
        private Stats stats;

        void incrementOnRecordsCalled() {
            countOnRecordsCalled++;
        }

        void incrementContinuationEvents() {
            countContinuationEvents++;
        }

        void addRecord(String record) {
            records.add(record);
        }

        void addStats(Stats stats) {
            this.stats = stats;
        }

        public List<String> getRecords() {
            return records;
        }

        public Integer getCountOnRecordsCalled() {
            return countOnRecordsCalled;
        }

        public Integer getCountContinuationEvents() {
            return countContinuationEvents;
        }

        public Stats getStats() {
            return stats;
        }
    }
// snippet-end:[s3.java2.async.selectObjectContentMethod.stream-info]
// snippet-end:[s3.java2.async.selectObjectContentMethod.main]

// snippet-start:[s3.java2.async.selectObjectContentMethod.csv]
    EventStreamInfo runSelectObjectContentMethodForCSV() {
        final String queryExpression = "select * from s3object c where c.region in ('Asia', 'Americas', 'Africa')";
        final String fileType = FILE_CSV;

        InputSerialization inputSerialization = InputSerialization.builder()
                .csv(CSVInput.builder().fileHeaderInfo(FileHeaderInfo.USE).build())
                .compressionType(CompressionType.NONE)
                .build();

        OutputSerialization outputSerialization = OutputSerialization.builder()
                .csv(CSVOutput.builder().build())
                .build();

        SelectObjectContentRequest select = SelectObjectContentRequest.builder()
                .bucket(BUCKET_NAME)
                .key(FILE_CSV)
                .expression(queryExpression)
                .expressionType(ExpressionType.SQL)
                .inputSerialization(inputSerialization)
                .outputSerialization(outputSerialization)
                .build();

        EventStreamInfo eventStreamInfo = new EventStreamInfo();

        s3AsyncClient.selectObjectContent(select, buildResponseHandler(eventStreamInfo)).join();
        long recordCount = eventStreamInfo.getRecords().stream().mapToInt(record ->
                record.split("\n").length
        ).sum();
        logger.info("Total records {}: {}", fileType, recordCount);
        logger.info("Visitor onRecords for fileType {} called {} times", fileType, eventStreamInfo.getCountOnRecordsCalled());
        logger.info("Visitor onStats for fileType {}, {}", fileType, eventStreamInfo.getStats());
        logger.info("Visitor onContinuations for fileType {}, {}", fileType, eventStreamInfo.getCountContinuationEvents());
        return eventStreamInfo;
    }
// snippet-end:[s3.java2.async.selectObjectContentMethod.csv]

    static void tearDown() {
        s3AsyncClient.deleteObjects(b -> b.
                bucket(BUCKET_NAME).
                delete(b1 -> b1.
                        objects(ObjectIdentifier.builder().key(FILE_CSV).build(), ObjectIdentifier.builder().key(FILE_JSON).build())
                )
        ).join();
        s3AsyncClient.waiter().waitUntilObjectNotExists(b -> b.key(FILE_CSV).bucket(BUCKET_NAME)).join();
        s3AsyncClient.waiter().waitUntilObjectNotExists(b -> b.key(FILE_JSON).bucket(BUCKET_NAME
        )).join();
        s3AsyncClient.deleteBucket(b -> b.bucket(BUCKET_NAME))
                .thenAccept(resp -> {
                    logger.info(resp.toString());
                    resp.sdkHttpResponse().statusText().ifPresent(logger::info);
                }).join();
    }


    static void setUp() {
        s3AsyncClient.createBucket(r -> r
                        .bucket(BUCKET_NAME))
                .whenComplete((resp, err) -> {
                    if (err != null) {
                        System.exit(1);
                    }
                    logger.info("Bucket created: {}", resp.location());
                    logger.info("Waiting for bucket to exist before proceeding ...");
                    s3AsyncClient.waiter().waitUntilBucketExists(r -> r.bucket(BUCKET_NAME).build());
                }).join();

        try (S3AsyncClient asyncClient = S3AsyncClient.crtCreate()) {
            List<String> urls = List.of(URL_CSV, URL_JSON);
            CompletableFuture.allOf(
                    urls.stream().map(url -> {
                        BlockingInputStreamAsyncRequestBody body =
                                AsyncRequestBody.forBlockingInputStream(null);
                        CompletableFuture<PutObjectResponse> responseFuture = asyncClient.putObject(r -> r.bucket(BUCKET_NAME).key(
                                url.substring(url.lastIndexOf(".") + 1).trim()), body);
                        try {
                            body.writeInputStream(new URL(url).openStream());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return responseFuture;
                    }).toList().toArray(new CompletableFuture[]{})
            ).join();
        }
    }
}
