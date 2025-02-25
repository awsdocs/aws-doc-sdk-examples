// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.entity.scenario;


import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.entityresolution.model.JobMetrics;

import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.CompletionException;

// snippet-start:[entityres.java2_scenario.main]
public class EntityResScenario {
    private static final Logger logger = LoggerFactory.getLogger(EntityResScenario.class);
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static final String STACK_NAME = "EntityResolutionCdkStack";
    private static final String ENTITY_RESOLUTION_ROLE_ARN_KEY = "EntityResolutionRoleArn";
    private static final String GLUE_DATA_BUCKET_NAME_KEY = "GlueDataBucketName";
    private static final String JSON_GLUE_TABLE_ARN_KEY = "JsonErGlueTableArn";
    private static final String CSV_GLUE_TABLE_ARN_KEY = "CsvErGlueTableArn";
    private static String glueBucketName;
    private static String workflowName = "workflow-"+ UUID.randomUUID();

    public static void main(String[] args) throws InterruptedException {
        String jsonSchemaMappingName = "jsonschema-" + UUID.randomUUID();
        String jsonSchemaMappingArn = null;
        String csvSchemaMappingName = "csv-" + UUID.randomUUID();
        String csvSchemaMappingArn = null;
        String roleARN;
        String csvGlueTableArn;
        String jsonGlueTableArn;

        EntityResActions actions = new EntityResActions();
        Scanner scanner = new Scanner(System.in);
        logger.info("Welcome to the AWS Entity Resolution Scenario.");
        logger.info("""
            AWS Entity Resolution is a fully-managed machine learning service provided by 
            Amazon Web Services (AWS) that helps organizations extract, link, and 
            organize information from multiple data sources. It leverages natural 
            language processing and deep learning models to identify and resolve 
            entities, such as people, places, organizations, and products, 
            across structured and unstructured data.
                         
            With Entity Resolution, customers can build robust data integration 
            pipelines to combine and reconcile data from multiple systems, databases, 
            and documents. The service can handle ambiguous, incomplete, or conflicting 
            information, and provide a unified view of entities and their relationships. 
            This can be particularly valuable in applications such as customer 360, 
            fraud detection, supply chain management, and knowledge management, where 
            accurate entity identification is crucial.
                         
            The `EntityResolutionAsyncClient` interface in the AWS SDK for Java 2.x 
            provides a set of methods to programmatically interact with the AWS Entity 
            Resolution service. This allows developers to automate the entity extraction, 
            linking, and deduplication process as part of their data processing workflows. 
            With Entity Resolution, organizations can unlock the value of their data, 
            improve decision-making, and enhance customer experiences by having a reliable, 
            comprehensive view of their key entities.
            """);

        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("""
                To prepare the AWS resources needed for this scenario application, the next step uploads
                a CloudFormation template whose resulting stack creates the following resources:
                - An AWS Glue Data Catalog table
                - An AWS IAM role
                - An AWS S3 bucket
                - An AWS Entity Resolution Schema
                
                It can take a couple minutes for the Stack to finish creating the resources.
                """);
        waitForInputToContinue(scanner);
        logger.info("Generating resources...");
        CloudFormationHelper.deployCloudFormationStack(STACK_NAME);
        Map<String, String> outputsMap = CloudFormationHelper.getStackOutputsAsync(STACK_NAME).join();
        roleARN = outputsMap.get(ENTITY_RESOLUTION_ROLE_ARN_KEY);
        glueBucketName = outputsMap.get(GLUE_DATA_BUCKET_NAME_KEY);
        csvGlueTableArn = outputsMap.get(CSV_GLUE_TABLE_ARN_KEY);
        jsonGlueTableArn = outputsMap.get(JSON_GLUE_TABLE_ARN_KEY);
        logger.info(DASHES);
        waitForInputToContinue(scanner);
        /*
         This JSON is a valid input for the AWS Entity Resolution service.
         The JSON represents an array of three objects, each containing an "id", "name", and "email"
         property. This format aligns with the expected input structure for the
         Entity Resolution service.
         */
        String json = """
                {"id":"1","name":"Alice Johnson","email":"alice.johnson@example.com"}
                {"id":"2","name":"Bob Smith","email":"bob.smith@example.com"}
                {"id":"3","name":"Charlie Black","email":"charlie.black@example.com"}
                """;
        logger.info("Upload the following JSON objects to the {} S3 bucket.", glueBucketName);
        logger.info(json);
        String csv = """
                id,name,email,phone
                1,Alice B. Johnson,alice.johnson@example.com,746-876-9846
                2,Bob Smith Jr.,bob.smith@example.com,987-654-3210
                3,Charlie Black,charlie.black@company.com,345-567-1234
                7,Jane E. Doe,jane_doe@company.com,111-222-3333
                """;
        logger.info("Upload the following CSV data to the {} S3 bucket.", glueBucketName);
        logger.info(csv);
        waitForInputToContinue(scanner);
        actions.uploadInputData(glueBucketName, json, csv);
        logger.info("The JSON objects have been uploaded to the S3 bucket.");
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("1. Create Schema Mapping");
        logger.info("""
            Entity Resolution schema mapping aligns and integrates data from 
            multiple sources by identifying and matching corresponding entities 
            like customers or products. It unifies schemas, resolves conflicts, 
            and uses machine learning to link related entities, enabling a 
            consolidated, accurate view for improved data quality and decision-making.
                        
            In this example, the schema mapping lines up with the fields in the JSON ans CSV objects. That is, 
            it contains these fields: id, name, and email. 
            """);
        waitForInputToContinue(scanner);
        try {
            CreateSchemaMappingResponse response = actions.createSchemaMappingAsync(jsonSchemaMappingName).join();
            jsonSchemaMappingArn = response.schemaArn();
            logger.info("The JSON schema mapping ARN is "+jsonSchemaMappingArn);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.info("Failed to create JSON schema mapping: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }

        try {
            CreateSchemaMappingResponse response = actions.createSchemaMappingAsync(csvSchemaMappingName).join();
            csvSchemaMappingArn = response.schemaArn();
            logger.info("The CSV schema mapping ARN is "+csvSchemaMappingArn);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.info("Failed to create CSV schema mapping: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("2. Create an AWS Entity Resolution Workflow. ");
        logger.info("""
            An Entity Resolution matching workflow identifies and links records 
            across datasets that represent the same real-world entity, such as 
            customers or products. Using techniques like schema mapping, 
            data profiling, and machine learning algorithms, 
            it evaluates attributes like names or emails to detect duplicates
            or relationships, even with variations or inconsistencies. 
            The workflow outputs consolidated, de-duplicated data.
            
            We will use the machine learning-based matching technique.
            """);
        waitForInputToContinue(scanner);
        try {
            String workflowArn = actions.createMatchingWorkflowAsync(roleARN, workflowName, glueBucketName, jsonGlueTableArn
                                                                     , jsonSchemaMappingName, csvGlueTableArn, csvSchemaMappingName).join();
            logger.info("The workflow ARN is: " + workflowArn);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.info("Failed to create workflow: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("3. Start the matching job of the " + workflowName + " workflow.");
        waitForInputToContinue(scanner);
        String jobId = null;
        try {
            jobId = actions.startMatchingJobAsync(workflowName).join();
            logger.info("The matching job was successfully started.");
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.info("Failed to start matching job: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. While the matching job is running, let's look at other API methods. First, let's get details for job "+jobId);
        waitForInputToContinue(scanner);
        try {
            actions.getMatchingJobAsync(jobId, workflowName).join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            logger.info("Failed to start matching job: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("5. Get the schema mapping for the JSON data.");
        waitForInputToContinue(scanner);
        try {
            actions.getSchemaMappingAsync(jsonSchemaMappingName).join();
            logger.info("Schema mapping retrieval completed.");
        } catch (CompletionException ce) {
            logger.info("Error retrieving schema mapping: " + ce.getCause().getMessage());
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("6. List Schema Mappings.");
        actions.ListSchemaMappings();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("7. Tag the {} resource.", jsonSchemaMappingName);
        logger.info("""
            Tags can help you organize and categorize your Entity Resolution resources. 
            You can also use them to scope user permissions by granting a user permission 
            to access or change only resources with certain tag values. 
            In Entity Resolution, SchemaMapping and MatchingWorkflow can be tagged. For this example, 
            the SchemaMapping is tagged.
                """);
        actions.tagEntityResource(jsonSchemaMappingArn).join();
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("8. View the results of the AWS Entity Resolution Workflow.");
        logger.info("""
            You cannot view the result of the workflow that is in a running state.  
            In order to view the results, you need to wait for the workflow that we started in step 3 to complete.
            
            If you choose not to wait, you cannot view the results or delete the workflow. You would have to 
            perform both tasks manually in the AWS Management Console.
           
            This can take up to 30 mins (y/n).
            """);
        String viewAns = scanner.nextLine().trim();
        if (viewAns.equalsIgnoreCase("y")) {
            logger.info("You selected to view the Entity Resolution Workflow results.");
            waitForInputToContinue(scanner);
            countdownWithWorkflowCheck(actions, 1800, jobId, workflowName);
            JobMetrics metrics = actions.getJobInfo(workflowName, jobId).join();
            logger.info("Number of input records: {}", metrics.inputRecords());
            logger.info("Number of match ids: {}", metrics.matchIDs());
            logger.info("Number of records not processed: {}", metrics.recordsNotProcessed());
            logger.info("Number of total records processed: {}", metrics.totalRecordsProcessed());
            logger.info("""
                      
                      The output of the machinelearning-based matching job is a CSV file in the S3 bucket. The following is a sample of the output:
                      
                      ------------------------------------------------------------------------------ ----------------- ---- ------------------ --------------------------- -------------- ---------- ---------------------------------------------------\s
                      InputSourceARN                                                                 ConfidenceLevel   id   name               email                       phone          RecordId   MatchID                                           \s
                     ------------------------------------------------------------------------------ ----------------- ---- ------------------ --------------------------- -------------- ---------- ---------------------------------------------------\s
                      arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable                      7    Jane E. Doe        jane_doe@company.com        111-222-3333   7          036298535ed6471ebfc358fc76e1f51200006472446402560 \s
                      arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable    0.90523           2    Bob Smith Jr.      bob.smith@example.com       987-654-3210   2          6ae2d360d6594089837eafc31b20f31600003506806140928 \s
                      arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/jsongluetable   0.90523           2    Bob Smith          bob.smith@example.com                      2          6ae2d360d6594089837eafc31b20f31600003506806140928 \s
                      arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable    0.89398956        1    Alice B. Johnson   alice.johnson@example.com   746-876-9846   1          34a5075b289247efa1847ab292ed677400009137438953472 \s
                      arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/jsongluetable   0.89398956        1    Alice Johnson      alice.johnson@example.com                  1          34a5075b289247efa1847ab292ed677400009137438953472 \s
                      arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/csvgluetable    0.605295          3    Charlie Black      charlie.black@company.com   345-567-1234   3          92c8ef3f68b34948a3af998d700ed02700002146028888064 \s
                      arn:aws:glue:region:xxxxxxxxxxxx:table/entity_resolution_db/jsongluetable   0.605295          3    Charlie Black      charlie.black@example.com                  3          92c8ef3f68b34948a3af998d700ed02700002146028888064 \s
                    
                    Note that each of the last 3 pairs of records are considered a match even though the 'name' or 'email' differ between the records;
                    For example 'Bob Smith Jr.' compared to 'Bob Smith'.
                    The confidence level is a value between 0 and 1, where 1 indicates a perfect match. In the last pair of matched records, 
                    the confidence level is lower for the differing email addresses.
                    
                    """);

            logger.info("Do you want to delete the resources, including workflow?");
            String delAns = scanner.nextLine().trim();
            if (delAns.equalsIgnoreCase("y")) {
                try {
                    actions.deleteMatchingWorkflowAsync(workflowName).join();
                    logger.info("Workflow deleted successfully!");
                } catch (CompletionException ce) {
                    Throwable cause = ce.getCause();
                    logger.info("Failed to delete workflow: " + (cause != null ? cause.getMessage() : ce.getMessage()));
                }
                waitForInputToContinue(scanner);
                logger.info(DASHES);
                logger.info("""
                Now we delete the CloudFormation stack, which deletes 
                the resources that were created at the beginning
                """);
                waitForInputToContinue(scanner);
                logger.info(DASHES);
                try {
                    deleteResources();
                } catch (CompletionException ce) {
                    Throwable cause = ce.getCause();
                    logger.error("Failed to delete Glue Table: {}", cause != null ? cause.getMessage() : ce.getMessage());
                }

            } else {
                logger.info("You can delete the Workflow later in the AWS Management console.");
            }
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("This concludes the AWS Entity Resolution scenario.");
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

    public static void countdownWithWorkflowCheck(EntityResActions actions, int totalSeconds, String jobId, String workflowName) throws InterruptedException {
        int secondsElapsed = 0;

        while (true) {
            // Calculate display minutes and seconds
            int remainingTime = totalSeconds - secondsElapsed;
            int displayMinutes = remainingTime / 60;
            int displaySeconds = remainingTime % 60;

            // Print the countdown
            System.out.printf("\r%02d:%02d", displayMinutes, displaySeconds);
            Thread.sleep(1000); // Wait for 1 second
            secondsElapsed++;

            // Check workflow status every 60 seconds
            if (secondsElapsed % 60 == 0 || remainingTime <= 0) {
                if (actions.checkWorkflowStatusCompleteAsync(jobId, workflowName).join()) {
                    logger.info(""); // Move to the next line after countdown.
                    logger.info("Countdown complete: Workflow is in SUCCEEDED state!");
                    break;
                }
            }

            // If countdown reaches zero, reset it for continuous countdown.
            if (remainingTime <= 0) {
                secondsElapsed = 0;
            }
        }
    }
    private static void deleteResources(){
        CloudFormationHelper.emptyS3Bucket(glueBucketName);
        CloudFormationHelper.destroyCloudFormationStack(STACK_NAME);
        logger.info("Resources deleted successfully!");
    }
}
// snippet-end:[entityres.java2_scenario.main]