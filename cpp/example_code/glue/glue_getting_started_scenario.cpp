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
#include <aws/glue/model/CreateJobRequest.h>
#include <aws/glue/model/DeleteCrawlerRequest.h>
#include <aws/glue/model/DeleteDatabaseRequest.h>
#include <aws/glue/model/DeleteJobRequest.h>
#include <aws/glue/model/GetCrawlerRequest.h>
#include <aws/glue/model/GetDatabaseRequest.h>
#include <aws/glue/model/GetJobRunRequest.h>
#include <aws/glue/model/GetTablesRequest.h>
#include <aws/glue/model/StartCrawlerRequest.h>
#include <aws/glue/model/StartJobRunRequest.h>
#include <aws/iam/IAMClient.h>
#include <aws/iam/Model/GetRoleRequest.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <vector>
#include <fstream>


namespace AwsDoc {
    namespace Glue {
        static const Aws::String BUCKET_NAME_KEY("BucketName");
        static const Aws::String ROLE_NAME_KEY("RoleName");
        static const Aws::String CRAWLER_DATABASE_NAME("doc-example-database");
        static const Aws::String CRAWLER_DATABASE_PREFIX("doc-example-");
        static const Aws::String CRAWLER_NAME("doc_example_crawler");
        static const Aws::String CDK_TOOLKIT_STACK_NAME("CDKToolkit");
        static const Aws::String STACK_NAME("doc-example-glue-scenario-stack");
        static const Aws::String JOB_NAME("doc-example-job");
        static const Aws::String JOB_COMMAND_NAME("glueetl");
        static const Aws::String JOB_PYTHON_VERSION("3");
        static const Aws::String GLUE_VERSION("3.0");
        static const Aws::String CLOUD_FORMATION_TEMPLATE_FILE(SOURCE_DIR "/setup_scenario_getting_started.yaml");
        static const Aws::String CDK_TOOLKIT_TEMPLATE(SOURCE_DIR "/bootstrap-template.yaml");
        static const Aws::String PYTHON_SCRIPT("flight_etl_job_script.py");
        static const Aws::String PYTHON_SCRIPT_PATH(SOURCE_DIR "/flight_etl_job_script.py");

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        static Aws::String askQuestion(const Aws::String &string,
                                       const std::function<bool(
                                               Aws::String)> &test = [](
                                               const Aws::String &) -> bool { return true; });

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \sa askQuestionForIntRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        int askQuestionForIntRange(const Aws::String &string, int low,
                                   int high);


        bool
        createCloudFormationResource(const Aws::String &stackName,
                                     const Aws::String &templateFilePath,
                                     std::vector<Aws::CloudFormation::Model::Output> &outputs,
                                     Aws::String &arnPrefix,
                                     const Aws::Client::ClientConfiguration &clientConfig);

        bool deleteCloudFormationResource(const Aws::String &stackName,
                                          const Aws::Client::ClientConfiguration &clientConfig);

        Aws::CloudFormation::Model::Stack
            getStackDescription(const Aws::String &stackName,
                            const Aws::Client::ClientConfiguration &clientConfig);

        bool runGettingStartedWithGlueScenario(const Aws::String& bucketName,
                                               const Aws::String& roleName,
                                               const Aws::Client::ClientConfiguration &clientConfig);


        bool uploadFile(const Aws::String &bucketName, const Aws::String &filePath,
                        const Aws::String &fileName,
                        const Aws::Client::ClientConfiguration &clientConfig);

        bool deleteS3Object(const Aws::String &bucketName,
        const Aws::String &objectName,
        const Aws::Client::ClientConfiguration &clientConfig);

        bool bootstrapCDK(bool& cdkBootstrapCreated,
                          const Aws::Client::ClientConfiguration &clientConfig);

        bool deleteAssets(const Aws::String &crawler, const Aws::String &database,
                          const Aws::String &job,
                          const Aws::Glue::GlueClient &client);

        bool getRoleArn(const Aws::String& roleName,
                        Aws::String &roleArn,
                        const Aws::Client::ClientConfiguration &clientConfig);
    } // Glue
} // AwsDoc



#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;

        bool cdkBootstrapCreated= false;
        Aws::String roleArn;
        Aws::String bucketName;
          if (argc == 1) {
            Aws::String answer = AwsDoc::Glue::askQuestion(
                    "Create the resources using Aws Cloud Formation? (y/n) ");

            if (answer == "y") {
                std::cout << "Creating the resources. This may take a while." << std::endl;

                if (!AwsDoc::Glue::bootstrapCDK(cdkBootstrapCreated, clientConfig))
                {
                    std::cerr << "Error creating CDK bootstrap"  << std::endl;
                    return 1;
                }
                Aws::String arnPrefix;
                std::vector<Aws::CloudFormation::Model::Output> outputs;
                bool result = AwsDoc::Glue::createCloudFormationResource(
                        AwsDoc::Glue::STACK_NAME,
                        AwsDoc::Glue::CLOUD_FORMATION_TEMPLATE_FILE, outputs, arnPrefix,
                        clientConfig); // defined in CMakeLists.txt

                if (result) {
                    for (auto &output: outputs) {
                        if (output.GetOutputKey() == AwsDoc::Glue::BUCKET_NAME_KEY) {
                            bucketName = output.GetOutputValue();
                        }
                        else if (output.GetOutputKey() == AwsDoc::Glue::ROLE_NAME_KEY)
                        {
                            AwsDoc::Glue::getRoleArn(output.GetOutputValue(), roleArn, clientConfig);
                        }
                     }

                    std::cout << "Created resources\nBucket name '" <<
                              bucketName << "'.\nRole arn '" << roleArn << "'." << std::endl;

                    std::cout <<"Uploading the job script '" << AwsDoc::Glue::PYTHON_SCRIPT
                    << "'." << std::endl;

                    if (!AwsDoc::Glue::uploadFile(bucketName, AwsDoc::Glue::PYTHON_SCRIPT_PATH,
                                                  AwsDoc::Glue::PYTHON_SCRIPT, clientConfig))
                    {
                        std::cerr << "Error uploading the job file." << std::endl;
                    }
                 }
                else{
                    std::cerr << "Error in resource creation." << std::endl;
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
             roleArn = argv[1];
             bucketName = argv[2];
         }

        if (!bucketName.empty() && !roleArn.empty()) {
             AwsDoc::Glue::runGettingStartedWithGlueScenario(bucketName, roleArn,
                                                             clientConfig);
         }
         else {
             std::cerr << "Could not run scenario because missing bucket name or role name."  << std::endl;
         }

        Aws::String answer = AwsDoc::Glue::askQuestion(
                "Delete the CloudFormation resources used in this example? (y/n) ");
        if (answer == "y") {
            AwsDoc::Glue::deleteS3Object(bucketName, AwsDoc::Glue::PYTHON_SCRIPT, clientConfig);
            AwsDoc::Glue::deleteCloudFormationResource(AwsDoc::Glue::STACK_NAME,
                                                       clientConfig);
        }

        if (cdkBootstrapCreated)
        {
            answer = AwsDoc::Glue::askQuestion(
                    "A cloud formation CDK bootstrap stack was created. "
                    "Retaining this may incur charges. Delete this stack? (y/n) ");

            if (answer == "y") {
                 AwsDoc::Glue::deleteCloudFormationResource(AwsDoc::Glue::CDK_TOOLKIT_STACK_NAME,
                                                           clientConfig);
            }
        }
   }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


Aws::String AwsDoc::Glue::askQuestion(const Aws::String &string,
                                          const std::function<bool(
                                                  Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
        if (!test(result)) {
            result.clear();
        }
    } while (result.empty());

    return result;
}

int AwsDoc::Glue::askQuestionForIntRange(const Aws::String &string, int low,
                                             int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result)
                {
                    std::cout << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cout << "\nNot a valid number." << std::endl;
                return false;
            }
    });
    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}


bool
AwsDoc::Glue::createCloudFormationResource(const Aws::String &stackName,
                                           const Aws::String &templateFilePath,
                                           std::vector<Aws::CloudFormation::Model::Output> &outputs,
                                           Aws::String &arnPrefix,
                                           const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::CloudFormationClient client(clientConfig);

    Aws::CloudFormation::Model::CreateStackRequest request;

    std::ifstream ifstream(templateFilePath);
    if (!ifstream) {
        std::cerr << "Could not load file '" << templateFilePath << "'" << std::endl;
        return false;
    }
    std::ostringstream templateStream;
    templateStream << ifstream.rdbuf();
    request.SetTemplateBody(templateStream.str());
    request.SetStackName(stackName);
    request.SetCapabilities(
            {Aws::CloudFormation::Model::Capability::CAPABILITY_NAMED_IAM});

    Aws::CloudFormation::Model::CreateStackOutcome outcome = client.CreateStack(
            request);

    bool result = false;
    if (outcome.IsSuccess() || outcome.GetError().GetErrorType() ==
                               Aws::CloudFormation::CloudFormationErrors::ALREADY_EXISTS) {
        Aws::CloudFormation::Model::DescribeStacksRequest waitRequest;
        waitRequest.SetStackName(stackName);

        Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS;
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
                            break;
                        case Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE:
                            std::cout << "CREATE_COMPLETE";
                            break;
                        default:
                            std::cout << "stack status reason ." << stack.GetStackStatusReason() << "' status id ";
                            std::cout << static_cast<int>(stack.GetStackStatus());
                            break;

                    }
                    std::cout << " after " << iterations << " seconds." << std::endl;
                }
                stackStatus = stack.GetStackStatus();
                if (Aws::CloudFormation::Model::StackStatus::CREATE_COMPLETE ==
                    stackStatus) {
                    outputs = stack.GetOutputs();
                    arnPrefix = "arn:aws:iam::123502194722:";
                    result = true;
                }
            }
            else {
                break;
            }
            if (iterations > 900) {
                stackStatus = Aws::CloudFormation::Model::StackStatus::CREATE_FAILED;
            }
        } while ((Aws::CloudFormation::Model::StackStatus::CREATE_IN_PROGRESS ==
                 stackStatus) || (Aws::CloudFormation::Model::StackStatus::ROLLBACK_IN_PROGRESS ==
                 stackStatus));
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
        Aws::CloudFormation::Model::StackStatus stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS;
        int iterations = 0;
        do {
            ++iterations;
            Aws::CloudFormation::Model::Stack stack = getStackDescription(stackName,
                                                                          clientConfig);
            if (!stack.GetStackName().empty()) {
                if (stack.GetStackStatus() != stackStatus || ((iterations % 10) == 0)) {
                    std::cout << "Stack " << stackName << " status is '";
                    switch (stack.GetStackStatus()) {
                        case Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS:
                            std::cout << "DELETE_IN_PROGRESS";
                            break;
                        case Aws::CloudFormation::Model::StackStatus::DELETE_FAILED:
                            std::cout << "DELETE_FAILED";
                            break;
                        case Aws::CloudFormation::Model::StackStatus::DELETE_COMPLETE:
                            std::cout << "DELETE_COMPLETE";
                            break;
                        default:
                            std::cout << static_cast<int>(stack.GetStackStatus());
                            break;

                    }
                    std::cout << "' after " << iterations << " seconds." << std::endl;
                }
                stackStatus = stack.GetStackStatus();

                if (stackStatus == Aws::CloudFormation::Model::StackStatus::DELETE_FAILED)
                {
                    std::cerr << "Delete of stack failed. " << stack.GetStackStatusReason() <<
                    std::endl;
                }

            }
            else {
                break;
            }
            if (iterations > 300) {
                stackStatus = Aws::CloudFormation::Model::StackStatus::DELETE_FAILED;
            }
        } while (Aws::CloudFormation::Model::StackStatus::DELETE_IN_PROGRESS ==
                 stackStatus);
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

#if 0
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
            return false;
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
            deleteAssets("", CRAWLER_DATABASE_NAME, "", client);
            return false;
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
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", client);
            return false;
        }

    }

    // 4. Start a crawler.
    {
        Aws::Glue::Model::StartCrawlerRequest request;
        request.SetName(CRAWLER_NAME);

        Aws::Glue::Model::StartCrawlerOutcome outcome = client.StartCrawler(request);


        if (outcome.IsSuccess())
        {
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
                    std::cerr << "Error getting crawler.  " << getCrawlerOutcome.GetError().GetMessage() << std::endl;
                    break;
                }
            }

            if (Aws::Glue::Model::CrawlerState::READY == crawlerState)
            {
                std:: cout << "Crawler running after " << iterations << " seconds." << std::endl;
            }
        }
        else{
            std::cerr << "Error starting crawler.  " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", client);
            return false;
        }
     }

    // 5. Get a database.
    {
        Aws::Glue::Model::GetDatabaseRequest request;
        request.SetName(CRAWLER_DATABASE_NAME);

        Aws::Glue::Model::GetDatabaseOutcome outcome = client.GetDatabase(request);

        if (outcome.IsSuccess())
        {
            const Aws::Glue::Model::Database &database = outcome.GetResult().GetDatabase();
            
            std::cout << "Successfully retrieve database with description '" << 
            database.GetDescription() << "'." << std::endl;
        }
        else{
            std::cerr << "Error getting the database.  " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", client);
            return false;
        }
    }
    
    // 6. Get tables.
    Aws::String tableName;
    {
        Aws::Glue::Model::GetTablesRequest request;
        request.SetDatabaseName(CRAWLER_DATABASE_NAME);

        Aws::Glue::Model::GetTablesOutcome outcome = client.GetTables(request);


        if (outcome.IsSuccess())
        {
            const std::vector<Aws::Glue::Model::Table>& tables = outcome.GetResult().GetTableList();
            std::cout << "The database contains " << tables.size() << (tables.size() == 1 ?
                " table." : "tables.") << std::endl;
            std::cout << "Here is a list of the tables in the database.";
            for (size_t index = 0; index < tables.size(); ++index)
            {
                std::cout << "    " << index + 1 << ":  " << tables[index].GetName() << std::endl;
            }
#if 0
            if (tables.size() > 0) {
                tableName = tables[0].GetName();
                int tableIndex = askQuestionForIntRange(
                        "Enter an index to display the database detail ",
                        1, static_cast<int>(tables.size()));
                std::cout << tables[tableIndex - 1].Jsonize().View().WriteReadable() << std::endl;
            }
#endif
        }
        else{
            std::cerr << "Error:  " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", client);
            return false;
        }

    }
#endif

    // 7. Create a job

    {

        Aws::Glue::Model::JobCommand command;
        command.SetName(JOB_COMMAND_NAME);
        command.SetPythonVersion(JOB_PYTHON_VERSION);
        command.SetScriptLocation(Aws::String("s3://") + bucketName + "/" + PYTHON_SCRIPT);
        Aws::Glue::Model::CreateJobRequest request;
        request.SetName(JOB_NAME);
        request.SetRole(roleName);
        request.SetGlueVersion("3.0");
        request.SetCommand(command);

        Aws::Glue::Model::CreateJobOutcome outcome = client.CreateJob(request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully created the job." << std::endl;
        }
        else {
            std::cerr << "Error creating the job. " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", client);
            return false;
        }

    }

    // 8. Start a job run.
    {
        Aws::Glue::Model::StartJobRunRequest request;
        request.SetJobName(JOB_NAME);
        Aws::Map<Aws::String, Aws::String> arguments;
        arguments["--input_database"] = CRAWLER_DATABASE_NAME;
        arguments["--input_table"] = "doc-example-csv"; // TODO remove tableName;
        arguments["--output_bucket_url"] = Aws::String("s3://") + bucketName + "/";
        request.SetArguments(arguments);

        Aws::Glue::Model::StartJobRunOutcome outcome = client.StartJobRun(request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully started the job." << std::endl;

            Aws::String jobRunId = outcome.GetResult().GetJobRunId();

            Aws::Glue::Model::JobRunState jobRunState = Aws::Glue::Model::JobRunState::NOT_SET;
            int iterator = 0;
            bool canContinue = true;
            while (canContinue)
            {
                ++iterator;
                std::this_thread::sleep_for(std::chrono::seconds(1));
                Aws::Glue::Model::GetJobRunRequest request;
                request.SetJobName(JOB_NAME);
                request.SetRunId(jobRunId);

                Aws::Glue::Model::GetJobRunOutcome outcome = client.GetJobRun(request);

                if (outcome.IsSuccess()) {
                    const Aws::Glue::Model::JobRun &jobRun = outcome.GetResult().GetJobRun();
                    jobRunState = jobRun.GetJobRunState();
                    Aws::String statusString;
                    switch (jobRunState) {
                        case Aws::Glue::Model::JobRunState::SUCCEEDED:
                            statusString = "SUCCEEDED";
                            canContinue = false;
                            break;
                        case Aws::Glue::Model::JobRunState::STOPPED:
                            statusString = "STOPPED";
                            canContinue = false;
                            break;
                        case Aws::Glue::Model::JobRunState::FAILED:
                            statusString = "FAILED";
                            canContinue = false;
                            break;
                        case Aws::Glue::Model::JobRunState::TIMEOUT:
                            statusString = "TIMEOUT";
                            canContinue = false;
                            break;
                        default:
                            statusString = std::to_string(static_cast<int>(jobRunState));
                            break;

                    }
                    if ((iterator % 10) == 0)
                    {
                        std::cout << "Check job run status. "  << iterator <<
                        " seconds elapsed." << std::endl;
                    }

                    if (!canContinue)
                    {
                        std::cout << "Job run state " << statusString << std::endl;

                        if (jobRunState != Aws::Glue::Model::JobRunState::SUCCEEDED)
                        {
                            std::cerr << "Error running job. " << jobRun.GetErrorMessage()
                             << std::endl;
                            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, client);
                            return false;
                        }
                    }
                }
                else {
                    std::cerr << "Error starting the job. " << outcome.GetError().GetMessage()
                              << std::endl;
                    deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, client);
                    return false;
                }

            }
        }
        else {
            std::cerr << "Error . " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, client);
            return false;
        }
    }

    // 9. List all jobs.
    {
        Aws::S3::S3Client s3Client;
        Aws::S3::Model::ListObjectsRequest request;
        request.SetBucket(bucketName);
        request.SetPrefix("run-");

        Aws::S3::Model::ListObjectsOutcome outcome = s3Client.ListObjects(request);

        if (outcome.IsSuccess()) {
            const std::vector<Aws::S3::Model::Object>& objects = outcome.GetResult().GetContents();
            std::cout << "Data from your job is in " << objects.size() <<
            " files in the S3 bucket, " << bucketName << "." << std::endl;

            int objectIndex = askQuestionForIntRange("Enter the number of a block to download it and see the first 20 "
                                                     "lines of JSON output in the block: ", 1,
                                                     static_cast<int>(objects.size()));

            Aws::String objectName = objects[objectIndex - 1].GetKey();

            
        }
        else {
            std::cerr << "Error . " << outcome.GetError().GetMessage() << std::endl;
        }

    }

    return deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, client);
}

bool
AwsDoc::Glue::uploadFile(const Aws::String &bucketName, const Aws::String &filePath,
                         const Aws::String &fileName,
                         const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
    //We are using the name of the file as the key for the object in the bucket.
    //However, this is just a string and can be set according to your retrieval needs.
    request.SetKey(fileName);

    std::shared_ptr<Aws::IOStream> inputData =
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                          filePath.c_str(),
                                          std::ios_base::in | std::ios_base::binary);

    if (!*inputData) {
        std::cerr << "Error unable to read file " << filePath << std::endl;
        return false;
    }

    request.SetBody(inputData);

    Aws::S3::Model::PutObjectOutcome outcome =
            s3_client.PutObject(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error: PutObject: " <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    else {
        std::cout << "Added object '" << filePath << "' to bucket '"
                  << bucketName << "'." << std::endl;
    }

    return outcome.IsSuccess();
}

bool AwsDoc::Glue::deleteS3Object(const Aws::String &bucketName,
                                  const Aws::String &objectName,
                                  const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::DeleteObjectRequest request;

    request.WithKey(objectName)
            .WithBucket(bucketName);

    Aws::S3::Model::DeleteObjectOutcome outcome =
            client.DeleteObject(request);

    if (!outcome.IsSuccess()) {
        auto err = outcome.GetError();
        std::cerr << "Error: DeleteObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully deleted the object." << std::endl;
    }

    return outcome.IsSuccess();

}

bool AwsDoc::Glue::bootstrapCDK(bool &cdkBootstrapCreated,
                                const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::CloudFormation::Model::Stack stack = getStackDescription(CDK_TOOLKIT_STACK_NAME,
                                                                  clientConfig);

    cdkBootstrapCreated = false;
    bool result = true;
    if (stack.GetStackName().empty())
    {
        std::cout << "Creating CDK toolkit stack." << std::endl;

        std::vector<Aws::CloudFormation::Model::Output> outputs;
        Aws::String roleArn;
        result = createCloudFormationResource(CDK_TOOLKIT_STACK_NAME,
                                              CDK_TOOLKIT_TEMPLATE,
                                              outputs, roleArn, clientConfig);
        if (result)
        {
            cdkBootstrapCreated = true;
        }
    }

    return result;
}

bool AwsDoc::Glue::deleteAssets(const Aws::String &crawler, const Aws::String &database,
                                const Aws::String &job,
                                const Aws::Glue::GlueClient &client) {

    bool result = true;
    // 11. Delete a job.
    if (!job.empty())
    {
        Aws::Glue::Model::DeleteJobRequest request;
        request.SetJobName(job);

        Aws::Glue::Model::DeleteJobOutcome outcome = client.DeleteJob(request);


        if (outcome.IsSuccess()) {
            std::cout << "Successfully deleted the job." << std::endl;
        }
        else {
            std::cerr << "Error deleting the job. " << outcome.GetError().GetMessage() << std::endl;
            result = false;
        }

    }
#if 0
    // 12. Delete a database.
    if (!database.empty())
    {
        Aws::Glue::Model::DeleteDatabaseRequest request;
        request.SetName(database);

        Aws::Glue::Model::DeleteDatabaseOutcome outcome = client.DeleteDatabase(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Successfully deleted the database." << std::endl;
        }
        else{
            std::cerr << "Error deleting database. " << outcome.GetError().GetMessage() << std::endl;
            result = true;
        }
    }

    // 13. Delete a crawler.
    if (!crawler.empty())
    {
        Aws::Glue::Model::DeleteCrawlerRequest request;
        request.SetName(crawler);

        Aws::Glue::Model::DeleteCrawlerOutcome outcome = client.DeleteCrawler(request);

        if (outcome.IsSuccess())
        {
            std::cout << "Successfully deleted the crawler." << std::endl;
        }
        else{
            std::cerr << "Error deleting the crawler. " << outcome.GetError().GetMessage() << std::endl;
            result = false;
        }
    }
#endif
    return result;
}

bool AwsDoc::Glue::getRoleArn(const Aws::String &roleName, Aws::String &roleArn,
                              const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::IAM::IAMClient client(clientConfig);

    Aws::IAM::Model::GetRoleRequest request;
    request.SetRoleName(roleName);

    Aws::IAM::Model::GetRoleOutcome outcome = client.GetRole(request);


    if (outcome.IsSuccess()) {
        std::cout << "Successfully retrieved role." << std::endl;
        roleArn = outcome.GetResult().GetRole().GetArn();
    }
    else {
        std::cerr << "Error retrieving role. " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
