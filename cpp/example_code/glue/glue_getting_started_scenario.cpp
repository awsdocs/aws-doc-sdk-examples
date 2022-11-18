/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * This example performs the following tasks:
 *
 * 1. Create a database.
 * 2. Create a crawler.
 * 3. Get a crawler.
 * 4. Start a crawler.
 * 5. Get a database.
 * 6. Get tables.
 * 7. Create a job.
 * 8. Start a job run.
 * 9. List all jobs.
 * 10. Get job runs.
 * 11. Delete a job.
 * 12. Delete a database.
 * 13. Delete a crawler.
 *
 */

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/cloudformation/CloudFormationClient.h>
#include <aws/cloudformation/model/CreateStackRequest.h>
#include <aws/cloudformation/model/DeleteStackRequest.h>
#include <aws/cloudformation/model/DescribeStacksRequest.h>
#include <aws/glue/GlueClient.h>
#include <aws/glue/model/CreateDatabaseRequest.h>
#include <aws/glue/model/CreateCrawlerRequest.h>
#include <aws/glue/model/DeleteCrawlerRequest.h>
#include <aws/glue/model/DeleteDatabaseRequest.h>
#include <aws/glue/model/GetCrawlerRequest.h>
#include <aws/glue/model/StartCrawlerRequest.h>
#include <vector>
#include <fstream>

namespace AwsDoc {
    namespace Glue {
        static const Aws::String BUCKET_NAME_KEY("BucketName");
        static const Aws::String ROLE_NAME_KEY("RoleName");
        static const Aws::String CRAWLER_DATABASE_NAME("doc-example-database");
        static const Aws::String CRAWLER_DATABASE_PREFIX("doc-example-");
        static const Aws::String CRAWLER_NAME("doc_example_crawler");

        static const Aws::String STACK_NAME("doc-example-glue-scenario-stack");

        Aws::String askQuestion(const Aws::String &string);

        std::map<Aws::String, Aws::String>
        createCloudFormationResource(const Aws::String &stackName,
                                     const Aws::String &templateFilePath,
                                     const Aws::Client::ClientConfiguration &clientConfig);

        bool deleteCloudFormationResource(const Aws::String &stackName,
                                          const Aws::Client::ClientConfiguration &clientConfig);

        Aws::CloudFormation::Model::Stack
        getStackDescription(const Aws::String &stackName,
                            const Aws::Client::ClientConfiguration &clientConfig);

        bool runGettingStartedWithGlueScenario(const Aws::String& bucketName,
                                               const Aws::String& roleName,
                                               const Aws::Client::ClientConfiguration &clientConfig);
    } // Glue
} // AwsDoc



#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
    Aws::SDKOptions options;

    Aws::InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;

        Aws::String roleName;
        Aws::String bucketName;
          if (argc == 1) {
            Aws::String answer = AwsDoc::Glue::askQuestion(
                    "Create the resources using Aws Cloud Formation? (y/n) ");

            if (answer == "y") {
                std::cout << "Creating the resources. This may take a while." << std::endl;
                std::map<Aws::String, Aws::String> result = AwsDoc::Glue::createCloudFormationResource(
                        AwsDoc::Glue::STACK_NAME,
                        CLOUD_FORMATION_TEMPLATE_FILE,
                        clientConfig); // defined in CMakeLists.txt

                if (!result.empty()) {
                    bucketName = result[AwsDoc::Glue::BUCKET_NAME_KEY];
                    roleName = result[AwsDoc::Glue::ROLE_NAME_KEY];

                    std::cout << "Created resources\nBucket name '" <<
                    bucketName <<"'.\nRole name '" << roleName << "'." << std::endl;
                 }
                else{
                    std::cout << "Error in resource creation" << std::endl;
                    return 1;
                }
            }
            else{
                std::cout << "Resources with the correct role name and bucket name must "
                << "be created to run this example." << std::endl;
                return 1;
            }

        }
         else if (argc == 3)
         {
             roleName = argv[1];
             bucketName = argv[2];
         }

        AwsDoc::Glue::runGettingStartedWithGlueScenario(bucketName, roleName, clientConfig);

        if (argc == 1) {
            Aws::String answer = AwsDoc::Glue::askQuestion(
                    "Delete the resources you created? (y/n) ");
            if (answer == "y") {
                AwsDoc::Glue::deleteCloudFormationResource(AwsDoc::Glue::STACK_NAME,
                                                           clientConfig);
            }
        }
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


Aws::String AwsDoc::Glue::askQuestion(const Aws::String &string) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
    } while (result.empty());

    return result;
}

std::map<Aws::String, Aws::String>
AwsDoc::Glue::createCloudFormationResource(const Aws::String &stackName,
                                           const Aws::String &templateFilePath,
                                           const Aws::Client::ClientConfiguration &clientConfig) {
    std::map<Aws::String, Aws::String> result;
    Aws::CloudFormation::CloudFormationClient client(clientConfig);

    Aws::CloudFormation::Model::CreateStackRequest request;

    std::ifstream ifstream(templateFilePath);
    if (!ifstream) {
        std::cerr << "Could not load file '" << templateFilePath << "'" << std::endl;
        return result;
    }
    std::ostringstream templateStream;
    templateStream << ifstream.rdbuf();
    request.SetTemplateBody(templateStream.str());
    request.SetStackName(stackName);
    request.SetCapabilities(
            {Aws::CloudFormation::Model::Capability::CAPABILITY_NAMED_IAM});

    Aws::CloudFormation::Model::CreateStackOutcome outcome = client.CreateStack(
            request);

    if (outcome.IsSuccess() || outcome.GetError().GetErrorType() ==
                               Aws::CloudFormation::CloudFormationErrors::ALREADY_EXISTS) {
        Aws::CloudFormation::Model::DescribeStacksRequest waitRequest;
        waitRequest.SetStackName(stackName);

        Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS;
        Aws::String bucketName;
        Aws::String roleName;
        int iterations = 0;
        do {
            ++iterations;
            Aws::CloudFormation::Model::Stack stack = getStackDescription(stackName,
                                                                          clientConfig);
            if (!stack.GetStackName().empty()) {
                if (stack.GetStackStatus() != stackStatus || ((iterations % 10) == 0)) {
                    std::cout << "Stack " << stackName << " status ";
                    switch (stack.GetStackStatus()) {
                        case Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS:
                            std::cout << "CREATE_IN_PROGRESS";
                            break;
                        case Aws::CloudFormation::Model::StackStatus::CREATE_FAILED:
                            std::cout << "CREATE_FAILED";
                        case Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE:
                            std::cout << "CREATE_COMPLETE";
                            break;
                        default:
                            std::cout << static_cast<int>(stack.GetStackStatus());
                            break;

                    }
                    std::cout << " after " << iterations << " seconds." << std::endl;
                }
                stackStatus = stack.GetStackStatus();
                if (Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE ==
                    stackStatus) {
                    for (auto &output: stack.GetOutputs()) {
                        if (output.GetOutputKey() == BUCKET_NAME_KEY) {
                            bucketName = output.GetOutputValue();
                        }
                        else if (output.GetOutputKey() == ROLE_NAME_KEY) {
                            roleName = output.GetOutputValue();
                        }
                    }
                }
            }
            else {
                break;
            }
            if (iterations > 300) {
                stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_FAILED;
            }
        } while (Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS ==
                 stackStatus);

        if (!roleName.empty() && !bucketName.empty()) {
            result[BUCKET_NAME_KEY] = bucketName;
            result[ROLE_NAME_KEY] = roleName;
        }
    }
    else {
        std::cerr << "Create stack failed " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return result;
}

Aws::CloudFormation::Model::Stack
AwsDoc::Glue::getStackDescription(const Aws::String &stackName,
                                  const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::Model::Stack result;

    Aws::CloudFormation::CloudFormationClient client(clientConfig);

    Aws::CloudFormation::Model::DescribeStacksRequest request;
    request.SetStackName(stackName);
    Aws::CloudFormation::Model::DescribeStacksOutcome outcome = client.DescribeStacks(
            request);
    if (outcome.IsSuccess()) {
        auto stacks = outcome.GetResult().GetStacks();
        for (auto &stack: stacks) {
            if (stack.GetStackName() == stackName) {
                result = stack;
                break;
            }
        }
    }
    else {
        std::cerr << "DescribeStacks failed " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return result;
}

bool AwsDoc::Glue::deleteCloudFormationResource(const Aws::String &stackName,
                                                const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::CloudFormationClient client(clientConfig);
    Aws::CloudFormation::Model::DeleteStackRequest request;
    request.SetStackName(stackName);

    Aws::CloudFormation::Model::DeleteStackOutcome outcome = client.DeleteStack(
            request);

    if (outcome.IsSuccess()) {
        std::cout << "Stack deleted " << std::endl;
    }
    else {
        std::cerr << "Delete stack failed "
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDoc::Glue::runGettingStartedWithGlueScenario(const Aws::String &bucketName,
                                                     const Aws::String &roleName,
                                                     const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::Glue::GlueClient client(clientConfig);

    // 1. Create a database.
    {
        Aws::Glue::Model::DatabaseInput input;
        input.SetName(CRAWLER_DATABASE_NAME);
        Aws::Glue::Model::CreateDatabaseRequest request;
        request.SetDatabaseInput(input);

        Aws::Glue::Model::CreateDatabaseOutcome outcome = client.CreateDatabase(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Successfully created the database." << std::endl;
        }
        else{
            std::cerr << "Error creating a database " << outcome.GetError().GetMessage() << std::endl;
        }
    }

    // 2. Create a crawler.
    {
        Aws::Glue::Model::S3Target s3Target;
        s3Target.SetPath("s3://crawler-public-us-east-1/flight/2016/csv");
        Aws::Glue::Model::CrawlerTargets crawlerTargets;
        crawlerTargets.AddS3Targets(s3Target);

        Aws::Glue::Model::CreateCrawlerRequest request;
        request.SetTargets(crawlerTargets);
        request.SetName(CRAWLER_NAME);
        request.SetDatabaseName(CRAWLER_DATABASE_NAME);
        request.SetTablePrefix(CRAWLER_DATABASE_PREFIX);
        request.SetRole(roleName);

        Aws::Glue::Model::CreateCrawlerOutcome outcome = client.CreateCrawler(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Successfully created the crawler." << std::endl;
        }
        else{
            std::cerr << "Error creating a crawler. " << outcome.GetError().GetMessage() << std::endl;
        }
    }

    // 3. Get a crawler.
    {
        Aws::Glue::Model::GetCrawlerRequest request;
        request.SetName(CRAWLER_NAME);

        Aws::Glue::Model::GetCrawlerOutcome outcome = client.GetCrawler(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Successfully retrieved crawler." << std::endl;
        }
        else{
            std::cerr << "Error retrieving crawler.  " << outcome.GetError().GetMessage() << std::endl;
        }

    }

    // 4. Start a crawler.
    {
        Aws::Glue::Model::StartCrawlerRequest request;
        request.SetName(CRAWLER_NAME);

        Aws::Glue::Model::StartCrawlerOutcome outcome = client.StartCrawler(request);


        if (outcome.IsSuccess())
        {
            std::cout << "Successfully ." << std::endl;
        }
        else{
            std::cerr << "Error:  " << outcome.GetError().GetMessage() << std::endl;
        }

        std::cout << "Starting crawler. This may take awhile." << std::endl;

        Aws::Glue::Model::CrawlerState crawlerState = Aws::Glue::Model::CrawlerState::NOT_SET;
        int iterations = 0;
        while (Aws::Glue::Model::CrawlerState::READY != crawlerState) {
            std::this_thread::sleep_for(std::chrono::seconds(1));
            ++iterations;
            if ((iterations % 10) == 0)
            {
                std::cout << "Checking crawler status. " << iterations << " seconds elapsed."
                << std::endl;
            }
            Aws::Glue::Model::GetCrawlerRequest getCrawlerRequest;
            getCrawlerRequest.SetName(CRAWLER_NAME);

            Aws::Glue::Model::GetCrawlerOutcome getCrawlerOutcome= client.GetCrawler(getCrawlerRequest);

            if (getCrawlerOutcome.IsSuccess()) {
                crawlerState = getCrawlerOutcome.GetResult().GetCrawler().GetState();
            }
            else {
                std::cerr << "Error:  " << getCrawlerOutcome.GetError().GetMessage() << std::endl;
                break;
            }
        }

        if (Aws::Glue::Model::CrawlerState::READY == crawlerState)
        {
            std:: cout << "Crawler running after " << iterations << " seconds." << std::endl;
        }
    }


    // 12. Delete a database.
    {
        Aws::Glue::Model::DeleteDatabaseRequest request;
        request.SetName(CRAWLER_DATABASE_NAME);

        Aws::Glue::Model::DeleteDatabaseOutcome outcome = client.DeleteDatabase(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Successfully deleted the database." << std::endl;
        }
        else{
            std::cerr << "Error deleting database. " << outcome.GetError().GetMessage() << std::endl;
        }
    }

    // 13. Delete a crawler.
    {
        Aws::Glue::Model::DeleteCrawlerRequest request;
        request.SetName(CRAWLER_NAME);

        Aws::Glue::Model::DeleteCrawlerOutcome outcome = client.DeleteCrawler(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Successfully deleted the crawler." << std::endl;
        }
        else{
            std::cerr << "Error deleting the crawler. " << outcome.GetError().GetMessage() << std::endl;
        }
    }

    return true;
}
