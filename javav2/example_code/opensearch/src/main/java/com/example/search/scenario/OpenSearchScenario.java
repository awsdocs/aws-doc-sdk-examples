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
    static Scanner scanner = new Scanner(System.in);

    static OpenSearchActions openSearchActions = new OpenSearchActions();
    public static void main(String[]args) throws Throwable {
        logger.info("""
            Welcome to the Amazon OpenSearch Service Basics Scenario.
                      
            Use the Amazon OpenSearch Service API to create, configure, and manage OpenSearch Service domains. 
             
          The operations exposed by the AWS OpenSearch Service client are focused on managing the OpenSearch Service domains 
            and their configurations, not the data within the domains (such as indexing or querying documents). 
            For document management, you typically interact directly with the OpenSearch REST API or use other libraries, 
          such as the OpenSearch Java client (https://opensearch.org/docs/latest/clients/java/).
                      
            Lets get started...
              """);
        waitForInputToContinue(scanner);

        try {
            runScenario();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    private static void runScenario() throws Throwable {
        String currentTimestamp = String.valueOf(System.currentTimeMillis());
        String domainName = "test-domain-" + currentTimestamp;
        logger.info(DASHES);
        logger.info("1. Create an Amazon OpenSearch domain");
        logger.info("""
           An Amazon OpenSearch domain is a managed instance of the OpenSearch engine, 
           which is an open-source search and analytics engine derived from Elasticsearch. 
           An OpenSearch domain is essentially a cluster of compute resources and storage that hosts 
           one or more OpenSearch indexes, enabling you to perform full-text searches, data analysis, and 
           visualizations.
           
           In this step, we'll initiate the creation of the domain. We'll check on the progress in a later
           step.\s
           """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<String> future = openSearchActions.createNewDomainAsync(domainName);

            String domainId = future.join();
            logger.info("Domain successfully created with ID: {}", domainId);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause != null) {
                if (cause instanceof OpenSearchException openSearchEx) {
                    logger.error("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
                } else {
                    logger.error("An unexpected error occurred: " + cause.getMessage(), cause);
                }
            } else {
                logger.error("An unexpected error occurred: " + rt.getMessage());
            }
            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("2. Describe the Amazon OpenSearch domain ");
        logger.info("In this step, we get back the Domain ARN which is used in an upcoming step.");
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
            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("3. List the domains in your account ");
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
            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("4. Wait until the domain's change status reaches a completed state");
        logger.info("""
        In this step, we check on the change status of the domain that we initiated in Step 1.
        Until we reach a COMPLETED state, we stay in a loop by sending a DescribeDomainChangeProgressRequest.
        
        The time it takes for a change to an OpenSearch domain to reach a completed state can range 
        from a few minutes to several hours. In this case the change is creating a new domain that we initiated in Step 1.
        The time varies depending on the complexity of the change and the current load on 
        the OpenSearch service. In general, simple changes, such as scaling the number of data nodes or 
        updating the OpenSearch version, may take 10-30 minutes,
        """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.domainChangeProgressAsync(domainName);
            future.join();
            logger.info("Domain change progress completed successfully.");
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException ex) {
                logger.info("An OpenSearch error occurred: Error message: " +ex.getMessage());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("5. Modify the domain ");
        logger.info("""
           You can change your OpenSearch domain's settings, like the number of instances, without starting over from scratch. 
           This makes it easy to adjust your domain as your needs change, allowing you to scale up or 
           down quickly without recreating everything.
           
           We modify the domain in this step by changing the number of instances.
           """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<UpdateDomainConfigResponse> future = openSearchActions.updateSpecificDomainAsync(domainName);
            UpdateDomainConfigResponse updateResponse = future.join();  // Wait for the task to complete
            logger.info("Domain update status: " + updateResponse.domainConfig().changeProgressDetails().configChangeStatusAsString());

        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException openSearchEx) {
                logger.info("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("6. Wait until the domain's change status reaches a completed state");
        logger.info("""
        In this step, we poll the status until the domain's change status reaches a completed state.
        """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.domainChangeProgressAsync(domainName);
            future.join();  // Wait for the task to complete
            logger.info("Domain change progress completed successfully.");
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException ex) {
                logger.info("EC2 error occurred: Error message: " +ex.getMessage());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("7. Tag the Domain ");
        logger.info("""
            Tags let you assign arbitrary information to an Amazon OpenSearch Service domain so you can 
            categorize and filter on that information. A tag is a key-value pair that you define and 
            associate with an OpenSearch Service domain. You can use these tags to track costs by grouping 
            expenses for similarly tagged resources.
            
            In this scenario, we create tags with keys "service" and "instances".
            """);
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.addDomainTagsAsync(arn);
            future.join();
            logger.info("Domain tags added successfully.");
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause != null) {
                if (cause instanceof OpenSearchException) {
                    logger.error("OpenSearch error occurred: Error message: " + cause.getMessage(), cause);
                } else {
                    logger.error("An unexpected error occurred: " + cause.getMessage(), cause);
                }
            } else {
                logger.error("An unexpected error occurred: " + rt.getMessage(), rt);
            }
            throw cause;
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("8. List Domain tags ");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.listDomainTagsAsync(arn);
            future.join();
            logger.info("Domain tags listed successfully.");
        } catch (RuntimeException e) {
            logger.info("Error occurred while listing domain tags:{} ", e.getMessage());
        }
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("9. Delete the Amazon OpenSearch ");
        waitForInputToContinue(scanner);
        try {
            CompletableFuture<Void> future = openSearchActions.deleteSpecificDomainAsync(domainName);
            future.join();
            logger.info("Request to delete {} was successfully sent.", domainName);
        } catch (RuntimeException rt) {
            Throwable cause = rt.getCause();
            if (cause instanceof OpenSearchException openSearchEx) {
                logger.info("OpenSearch error occurred: Error message: {}, Error code {}", openSearchEx.awsErrorDetails().errorMessage(), openSearchEx.awsErrorDetails().errorCode());
            } else {
                logger.info("An unexpected error occurred: " + rt.getMessage());
            }
            throw cause;
        }
        logger.info("""
                As mentioned earlier, it can take several minutes for the domain to be deleted.
                We can complete the scenario while the deletion proceeds.
                """);
        waitForInputToContinue(scanner);
        logger.info(DASHES);

        logger.info(DASHES);
        logger.info("This concludes the AWS OpenSearch Scenario");
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
                logger.info("Invalid input. Please try again.");
            }
        }
    }
}
// snippet-end:[opensearch.java2.scenario.main]
