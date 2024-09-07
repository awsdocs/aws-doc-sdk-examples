// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.search.scenario;
// snippet-start:[opensearch.java2.scenario.main]
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.opensearch.model.DomainInfo;
import software.amazon.awssdk.services.opensearch.model.OpenSearchException;
import software.amazon.awssdk.services.opensearch.model.UpdateDomainConfigResponse;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class OpenSearchScenario {
    public static final String DASHES = new String(new char[80]).replace("\0", "-");

    private static final Logger logger = LoggerFactory.getLogger(OpenSearchScenario.class);
    public static void main(String[]args){
        Scanner scanner = new Scanner(System.in);
        OpenSearchActions openSearchActions = new OpenSearchActions();
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        String domainName = "test-domain-" + currentTimestamp;

        logger.info("""
          Welcome to the Amazon OpenSearch Service Basics Scenario.
          
          Use the Amazon OpenSearch Service API to create, configure, and manage OpenSearch Service domains. 
           
          These operations exposed by the OpenSearch Service client is focused on managing the OpenSearch Service domains 
          and their configurations, not the data within the domains (such as indexing or querying documents). 
          For document management, you typically interact directly with the OpenSearch REST API or use other libraries, 
          such as the OpenSearch Java client.
          
          Lets get started...
            """);
        waitForInputToContinue(scanner);

        logger.info(DASHES);
        logger.info("1. Create an Amazon OpenSearch domain");
        logger.info("""
           An Amazon OpenSearch domain is a managed instance of the OpenSearch engine, 
           which is an open-source search and analytics engine derived from Elasticsearch. 
           An OpenSearch domain is essentially a cluster of compute resources and storage that hosts 
           one or more OpenSearch indexes, enabling you to perform full-text searches, data analysis, and 
           visualizations.
           """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = openSearchActions.createNewDomainAsync(domainName);
            String domainId = future.join();
            logger.info("Domain successfully created with ID: " + domainId);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException openSearchEx) {
                logger.info("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("2. Describe the Amazon OpenSearch domain ");
        System.out.println("In this step, we get back the Domain ARN which is used in upcoming steps.");
        waitForInputToContinue(scanner);
        String arn = "";
        try {
            CompletableFuture<String> future = openSearchActions.describeDomainAsync(domainName);
            arn = future.join();
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException openSearchEx) {
                logger.info("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("3. List the domains in your account ");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<List<DomainInfo>> future = openSearchActions.listAllDomainsAsync();
            List<DomainInfo> domainInfoList = future.join();
            for (DomainInfo domain : domainInfoList) {
                logger.info("Domain name is: " + domain.domainName());
            }
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException openSearchEx) {
                logger.info("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("4. Wait until the domain's change status reaches a completed state");
        System.out.println("""
        In the following method call, the clock counts up until the domain's change status reaches a completed state.
        
        Roughly, the time it takes for an OpenSearch domain's change status to reach a completed state can range 
        from a few minutes to several hours, depending on the complexity of the change and the current load on 
        the OpenSearch service. In general, simple changes, such as scaling the number of data nodes or 
        updating the OpenSearch version, may take 10-30 minutes,
        """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.domainChangeProgressAsync(domainName);
            future.join();
            System.out.println("Domain change progress completed successfully.");
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException ex) {
                System.out.println("An OpenSearch error occurred: Error message: " +ex.getMessage());
            } else {
                System.out.println("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("5. Modify the domain ");
        System.out.println("""
           You can modify the cluster configuration, such as the instance count, without having to manually 
           manage the domain's settings. This flexibility is particularly useful when your data or usage patterns 
           change over time, as you can easily scale the OpenSearch domain to meet the new requirements without 
           having to recreate the entire domain.
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<UpdateDomainConfigResponse> future = openSearchActions.updateSpecificDomainAsync(domainName);
            UpdateDomainConfigResponse updateResponse = future.join();  // Wait for the task to complete
            logger.info("Domain update response from Amazon OpenSearch Service: " + updateResponse.toString());

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException openSearchEx) {
                logger.info("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("6. Wait until the domain's change status reaches a completed state");
        System.out.println("""
        In the following method call, the clock counts up until the domain's change status reaches a completed state.
               
        """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.domainChangeProgressAsync(domainName);
            future.join();  // Wait for the task to complete
            System.out.println("Domain change progress completed successfully.");
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException ex) {
                System.out.println("EC2 error occurred: Error message: " +ex.getMessage());
            } else {
                System.out.println("An unexpected error occurred: " + rt.getMessage());
            }
        }
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("7. Tag the Domain ");
        System.out.println("""
            Tags let you assign arbitrary information to an Amazon OpenSearch Service domain so you can 
            categorize and filter on that information. A tag is a key-value pair that you define and 
            associate with an OpenSearch Service domain. You can use these tags to track costs by grouping 
            expenses for similarly tagged resources.\s
            """);
        waitForInputToContinue(scanner);
        openSearchActions.addDomainTags(arn);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("8. List Domain tags ");
        waitForInputToContinue(scanner);
        openSearchActions.listDomainTags(arn);
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("9. Delete the Amazon OpenSearch ");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.deleteSpecificDomainAsync(domainName);
            future.join();
            logger.info(domainName + " was successfully deleted.");
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException openSearchEx) {
                logger.info("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
        }
        System.out.println(domainName +" has been deleted.");
        waitForInputToContinue(scanner);
        System.out.println(DASHES);

        System.out.println(DASHES);
        System.out.println("This concludes the AWS OpenSearch Scenario");
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
}
// snippet-end:[opensearch.java2.scenario.main]
