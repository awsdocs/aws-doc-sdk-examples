// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.entity.scenario;


import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingResponse;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletionException;

public class EntityResScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");
    private static final String ROLES_STACK = "EntityResolutionCdkStack";

    public static void main(String[] args) throws InterruptedException {

        final String usage = """

            Usage:
                <workflowName> <schemaName> <roleARN> <dataS3bucket> <outputBucket> <inputGlueTableArn>

            Where:
                workflowName - A unique identifier for the matching workflow, used in the entity resolution process.
                schemaName - The name of the schema, which defines the structure and attributes for the data being processed.
                roleARN: The ARN of the IAM role, that grants permissions for the entity resolution workflow (this resource is created using the CDK script. See the Readme).
                dataS3bucket: The S3 bucket,that stores the input data for the entity resolution process (this resource is created using the CDK script. See the Readme)..
                outputBucket: The S3 bucket URL where the results of the entity resolution workflow are stored (this resource is created using the CDK script. See the Readme)..
                inputGlueTableArn: The ARN of the AWS Glue table which provides the input data for the entity resolution process (this resource is created using the CDK script. See the Readme)..
            """;
        String workflowName = "MyMatchingWorkflow451";
        String schemaName = "schema451";

        // Use the AWS CDK to create this AWS resources. See the Readme file.
        String roleARN = "arn:aws:iam::814548047983:role/EntityResolutionCdkStack-EntityResolutionRoleB51A51-TSzkkBfrkbfm";
        String dataS3bucket = "glue-5ffb912c3d534e8493bac675c2a3196d";
        String outputBucket = "s3://entity-resolution-output-entityresolutioncdkstack";
        String inputGlueTableArn = "arn:aws:glue:us-east-1:814548047983:table/entity_resolution_db/entity_resolution";

        EntityResActions actions = new EntityResActions();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the AWS Entity Resolution Scenario. ");
        System.out.println("""
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
        System.out.println(DASHES);

        System.out.println(DASHES);

        /*
         This JSON is a valid input for the AWS Entity Resolution service.
         The JSON represents an array of three objects, each containing an "id", "name", and "email"
         property. This format aligns with the expected input structure for the
         Entity Resolution service.
         */
        String json = """
            [
              {
                "id": "1",
                "name": "Alice Johnson",
                "email": "alice.johnson@example.com"
              },
              {
                "id": "2",
                "name": "Bob Smith",
                "email": "bob.smith@example.com"
              },
              {
                "id": "3",
                "name": "Charlie Black",
                "email": "charlie.black@example.com"
              }
            ]
            """;
        System.out.println("Upload the JSON to the " + dataS3bucket + " S3 bucket if it does not exist");
        System.out.println(json);
        waitForInputToContinue(scanner);
        if (!actions.doesObjectExist(dataS3bucket)) {
            actions.uploadLocalFileAsync(dataS3bucket, json);
        } else {
            System.out.println("The JSON exists in " + dataS3bucket);
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("1. Create Schema Mapping");
        System.out.println("""
            Entity Resolution Schema Mapping aligns and integrates data from 
            multiple sources by identifying and matching corresponding entities 
            like customers or products. It unifies schemas, resolves conflicts, 
            and uses machine learning to link related entities, enabling a 
            consolidated, accurate view for improved data quality and decision-making.
                        
            In this example, the schema mapping lines up with the fields in the JSON. That is, 
            it contains these fields: id, name, and email. 
            """);
        waitForInputToContinue(scanner);
        String mappingARN = null; 
        try {
            CreateSchemaMappingResponse response = actions.createSchemaMappingAsync(schemaName).join();
            mappingARN = response.schemaArn();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            System.err.println("Failed to create schema mapping: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }

        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Create an AWS Entity Resolution Workflow. ");
        System.out.println("""
            An Entity Resolution matching workflow identifies and links records 
            across datasets that represent the same real-world entity, such as 
            customers or products. Using techniques like schema mapping, 
            data profiling, and machine learning algorithms, 
            it evaluates attributes like names or emails to detect duplicates
            or relationships, even with variations or inconsistencies. 
            The workflow outputs consolidated, de-duplicated data,\s
            """);
        waitForInputToContinue(scanner);
        try {
            String workflowArn = actions.createMatchingWorkflowAsync(roleARN, workflowName, outputBucket, inputGlueTableArn, schemaName).join();
            System.out.println("The workflow ARN is: " + workflowArn);
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            System.err.println("Failed to create workflow: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }
        waitForInputToContinue(scanner);

        System.out.println(DASHES);
        System.out.println("3. Start the matching job of the " + workflowName + " workflow.");
        waitForInputToContinue(scanner);
        String jobId = null;
        try {
            jobId = actions.startMatchingJobAsync(workflowName).join();
            System.out.println("The matching job was successfully started.");
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            System.err.println("Failed to start matching job: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Get details for job "+jobId);
        waitForInputToContinue(scanner);
        try {
            actions.getMatchingJobAsync(jobId, workflowName).join();
        } catch (CompletionException ce) {
            Throwable cause = ce.getCause();
            System.err.println("Failed to start matching job: " + (cause != null ? cause.getMessage() : ce.getMessage()));
        }
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Get Schema Mapping.");
        waitForInputToContinue(scanner);
        try {
            actions.getSchemaMappingAsync(schemaName).join();
            System.out.println("Schema mapping retrieval completed.");
        } catch (CompletionException ce) {
            System.err.println("Error retrieving schema mapping: " + ce.getCause().getMessage());
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. List Schema Mappings.");
        actions.ListSchemaMappings();
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Tag the "+schemaName +"resource.");
        System.out.println("""
            Tags can help you organize and categorize your Entity Resolution resources. 
            You can also use them to scope user permissions by granting a user permission 
            to access or change only resources with certain tag values. 
            In Entity Resolution, SchemaMapping and MatchingWorkflow can be tagged. For this example, 
            the SchemaMapping is tagged.
                """);
        actions.tagEntityResource(mappingARN).join();
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. Delete the AWS Entity Resolution Workflow.");
        System.out.println("""
            You cannot delete a workflow that is in a running state.  
            Would you like to wait for the workflow to complete. 
            This can take up to 30 mins (y/n).
            """);
        String delAns = scanner.nextLine().trim();
        if (delAns.equalsIgnoreCase("y")) {
            System.out.println("You selected to delete Entity Resolution Workflow.");
            waitForInputToContinue(scanner);
            countdownWithWorkflowCheck(actions, 1800, jobId, workflowName);
            try {
                actions.deleteMatchingWorkflowAsync(workflowName).join();
                System.out.println("Workflow deleted successfully!");
            } catch (CompletionException ce) {
                Throwable cause = ce.getCause();
                System.err.println("Failed to delete workflow: " + (cause != null ? cause.getMessage() : ce.getMessage()));
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("This concludes the AWS Entity Resolution scenario.");
        System.out.println(DASHES);


    }

    private static void waitForInputToContinue(Scanner scanner) {
        while (true) {
            System.out.println("");
            System.out.println("Enter 'c' followed by <ENTER> to continue:");
            String input = scanner.nextLine();

            if (input.trim().equalsIgnoreCase("c")) {
                System.out.println("Continuing with the program...");
                System.out.println("");
                break;
            } else {
                // Handle invalid input.
                System.out.println("Invalid input. Please try again.");
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
                    System.out.println(); // Move to the next line after countdown
                    System.out.println("Countdown complete: Workflow is in SUCCEEDED state!");
                    break;
                }
            }

            // If countdown reaches zero, reset it for continuous countdown
            if (remainingTime <= 0) {
                secondsElapsed = 0;
            }
        }
    }

}