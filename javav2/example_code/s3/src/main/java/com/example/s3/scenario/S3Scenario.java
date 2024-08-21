// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


package com.example.s3.scenario;

// snippet-start:[s3.java2.s3_scenario.main]

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This Java code example performs the following tasks:
 *
 * 1. Creates an Amazon S3 bucket.
 * 2. Uploads an object to the bucket.
 * 3. Downloads the object to another local file.
 * 4. Uploads an object using multipart upload.
 * 5. List all objects located in the Amazon S3 bucket.
 * 6. Copies the object to another Amazon S3 bucket.
 * 7. Deletes the object from the Amazon S3 bucket.
 * 8. Deletes the Amazon S3 bucket.
 */

public class S3Scenario {

    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static final Logger logger = LoggerFactory.getLogger(S3Scenario.class);
    public static void main(String[] args) throws IOException {
        final String usage = """

            Usage:
               <key> <objectPath> <savePath> <toBucket>

            Where:
                key - The key to use.
                objectPath - The path where the file is located (for example, C:/AWS/book2.pdf).
                savePath - The path where the file is saved after it's downloaded (for example, C:/AWS/book2.pdf).
                toBucket - An Amazon S3 bucket to where an object is copied to (for example, C:/AWS/book2.pdf).\s
                """;

       if (args.length != 4) {
           logger.info(usage);
            return;
       }

        String bucketName = "scenario-" + UUID.randomUUID();
        String key = args[0];
        String objectPath = args[1];
        String savePath = args[2];
        String toBucket = args[3];
        Scanner scanner = new Scanner(System.in);
        S3Actions s3Actions = new S3Actions();

        logger.info(DASHES);
        logger.info("Welcome to the Amazon S3 example scenario.");
        logger.info("""
            Amazon S3 (Simple Storage Service) is a highly scalable and durable object storage 
            service provided by Amazon Web Services (AWS). It is designed to store and retrieve 
            any amount of data, from anywhere on the web, at any time.
                        
            The `S3AsyncClient` interface in the AWS SDK for Java 2.x provides a set of methods to 
            programmatically interact with the Amazon S3 (Simple Storage Service) service. This allows 
            developers to automate the management and manipulation of S3 buckets and objects as 
            part of their application deployment pipelines. With S3, teams can focus on building 
            and deploying their applications without having to worry about the underlying storage 
            infrastructure required to host and manage large amounts of data.
            
            This scenario walks you through how to perform key operations for this service.  
            Let's get started...
            """);
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("1. Create an Amazon S3 bucket.");
        try {
            CompletableFuture<Void> future = s3Actions.createBucketAsync(bucketName);
            future.join();
            logger.info("Bucket {} is ready", bucketName);
            waitForInputToContinue(scanner);
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
            return;
        }
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("2. Update a local file to the Amazon S3 bucket.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<PutObjectResponse> future = s3Actions.uploadLocalFileAsync(bucketName, key, objectPath);
            future.join();
            logger.info("File uploaded successfully to {}/{}", bucketName, key);
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);


        logger.info(DASHES);
        logger.info("3. Download the object to another local file.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = s3Actions.getObjectBytesAsync(bucketName, key, savePath);
            future.join();
            logger.info("Successfully obtained bytes from S3 object and wrote to file {}", savePath);
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. Perform a multipart upload.");
        waitForInputToContinue(scanner);
        String multipartKey = "multiPartKey";
        try {
            // Call the multipartUpload method
            CompletableFuture<Void> future = s3Actions.multipartUpload(bucketName, multipartKey);
            future.join();
            logger.info("Multipart upload completed successfully for bucket '{}' and key '{}'", bucketName, multipartKey);
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("5. List all objects located in the Amazon S3 bucket.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = s3Actions.listAllObjectsAsync(bucketName);
            future.join();
            logger.info("Object listing completed successfully.");
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
            return;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("6. Copy the object to another Amazon S3 bucket.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = s3Actions.copyBucketObjectAsync(bucketName, key, toBucket);
            String result = future.join(); // Wait for the operation to complete.
            logger.info("Copy operation result: {}", result);
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("7. Delete the object from the Amazon S3 bucket.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = s3Actions.deleteObjectFromBucketAsync(bucketName, key);
            future.join();
            logger.info("Object was deleted successfully.");
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
            return;
        }
        try {
            CompletableFuture<Void> future = s3Actions.deleteObjectFromBucketAsync(bucketName, "multiPartKey");
            future.join();
            logger.info("Object was deleted successfully.");
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("8. Delete the Amazon S3 bucket.");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = s3Actions.deleteBucketAsync(bucketName);
            future.join();
            logger.info("Bucket was deleted successfully.");
        } catch (RuntimeException rte) {
            logger.error("An S3 exception occurred: {}", rte.getCause() != null ? rte.getCause().getMessage() : rte.getMessage());
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("You successfully completed the Amazon S3 scenario.");
        logger.info(DASHES);
    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            logger.info("");
            logger.info("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                logger.info("Continuing with the program...");
                logger.info("");
                break;
            } else {
                // Handle invalid input.
                logger.info("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[s3.java2.s3_scenario.main]