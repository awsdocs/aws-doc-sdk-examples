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
 * See the
 *
 * Purpose
 *
 * This example performs the following tasks:
 *
 * 1. Upload the job script to the S3 bucket.
 * 2. Create a database.
 * 3. Create a crawler.
 * 4. Get a crawler.
 * 5. Start a crawler.
 * 6. Get a database.
 * 7. Get tables.
 * 8. Create a job.
 * 9. Start a job run.
 * 10. List all jobs.
 * 11. Get job runs.
 * 12. Delete a job.
 * 13. Delete a database.
 * 14. Delete a crawler.
 * 15. Delete the job script and run data from the S3 bucket.
 *
 */

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/cloudformation/CloudFormationClient.h>
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
#include <aws/glue/model/GetJobRunsRequest.h>
#include <aws/glue/model/GetTablesRequest.h>
#include <aws/glue/model/ListJobsRequest.h>
#include <aws/glue/model/StartCrawlerRequest.h>
#include <aws/glue/model/StartJobRunRequest.h>
#include <aws/iam/IAMClient.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteObjectsRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/s3/model/ListObjectsRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <vector>
#include <fstream>
#include "glue_samples.h"


namespace AwsDoc {
    namespace Glue {
        static const Aws::String BUCKET_NAME_KEY("BucketName");
        static const Aws::String ROLE_NAME_KEY("RoleName");
        static const Aws::String CRAWLER_DATABASE_NAME("doc-example-database");
        static const Aws::String CRAWLER_DATABASE_PREFIX("doc-example-");
        static const Aws::String CRAWLER_NAME("doc_example_crawler");
        static const Aws::String STACK_NAME("doc-example-glue-scenario-stack");
        static const Aws::String JOB_NAME("doc-example-job");
        static const Aws::String JOB_COMMAND_NAME("glueetl");
        static const Aws::String JOB_PYTHON_VERSION("3");
        static const Aws::String GLUE_VERSION("3.0");
        static const Aws::String CLOUD_FORMATION_TEMPLATE_FILE(
                SOURCE_DIR "/setup_scenario_getting_started.yaml");
        static const Aws::String PYTHON_SCRIPT("flight_etl_job_script.py");
        static const Aws::String PYTHON_SCRIPT_PATH(
                SOURCE_DIR "/flight_etl_job_script.py");
        static const int LINES_OF_RUN_FILE_TO_DISPLAY = 20;

        static bool uploadFile(const Aws::String &bucketName, const Aws::String &filePath,
                        const Aws::String &fileName,
                        const Aws::Client::ClientConfiguration &clientConfig);

        static bool deleteAllObjectsInS3Bucket(const Aws::String &bucketName,
                                        const Aws::Client::ClientConfiguration &clientConfig);

        static bool getObjectFromBucket(const Aws::String &bucketName,
                                 const Aws::String &objectKey,
                                 std::ostream &objectStream,
                                 const Aws::Client::ClientConfiguration &clientConfig);

        static bool deleteAssets(const Aws::String &crawler, const Aws::String &database,
                          const Aws::String &job, const Aws::String &bucketName,
                          const Aws::Client::ClientConfiguration &clientConfig);
    } // Glue
} // AwsDoc

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;

        bool cdkBootstrapCreated = false;
        Aws::String roleArn;
        Aws::String bucketName;
        if (argc == 1) {
            Aws::String answer = AwsDoc::Glue::askQuestion(
                    "Create the resources using Aws Cloud Formation? (y/n) ");

            if (answer == "y") {
                std::cout << "Creating the resources. This may take a while."
                          << std::endl;

                if (!AwsDoc::Glue::bootstrapCDK(cdkBootstrapCreated, clientConfig)) {
                    std::cerr << "Error creating CDK bootstrap" << std::endl;
                    return 1;
                }
                 std::vector<Aws::CloudFormation::Model::Output> outputs;
                bool result = AwsDoc::Glue::createCloudFormationResource(
                        AwsDoc::Glue::STACK_NAME,
                        AwsDoc::Glue::CLOUD_FORMATION_TEMPLATE_FILE, outputs,
                        clientConfig);

                if (result) {
                    for (auto &output: outputs) {
                        if (output.GetOutputKey() == AwsDoc::Glue::BUCKET_NAME_KEY) {
                            bucketName = output.GetOutputValue();
                        }
                        else if (output.GetOutputKey() == AwsDoc::Glue::ROLE_NAME_KEY) {
                            AwsDoc::Glue::getRoleArn(output.GetOutputValue(), roleArn,
                                                     clientConfig);
                        }
                    }

                    std::cout << "Created resources\nBucket name '" <<
                              bucketName << "'.\nRole arn '" << roleArn << "'."
                              << std::endl;
                }
                else {
                    std::cerr << "Error in resource creation." << std::endl;
                    return 1;
                }
            }
            else {
                std::cout
                        << "Resources with the correct role name and bucket name must "
                        << "be created to run this example." << std::endl;
                return 1;
            }

        }
        else if (argc == 3) {
            roleArn = argv[1];
            bucketName = argv[2];
        }

        if (!bucketName.empty() && !roleArn.empty()) {
            AwsDoc::Glue::runGettingStartedWithGlueScenario(bucketName, roleArn,
                                                            clientConfig);
        }
        else {
            std::cerr
                    << "Could not run scenario because missing bucket name or role name."
                    << std::endl;
        }

        Aws::String answer = AwsDoc::Glue::askQuestion(
                "Delete the role and the bucket created with CloudFormation and used to run this example? (y/n) ");
        if (answer == "y") {
            AwsDoc::Glue::deleteCloudFormationResource(AwsDoc::Glue::STACK_NAME,
                                                       clientConfig);
        }

        if (cdkBootstrapCreated) {
            answer = AwsDoc::Glue::askQuestion(
                    "A cloud formation CDK bootstrap stack was created. "
                    "Retaining this may incur charges. Delete this stack? (y/n) ");

            if (answer == "y") {
                AwsDoc::Glue::deleteCloudFormationResource(
                        AwsDoc::Glue::CDK_TOOLKIT_STACK_NAME,
                        clientConfig);
            }
        }
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


bool AwsDoc::Glue::runGettingStartedWithGlueScenario(const Aws::String &bucketName,
                                                     const Aws::String &roleName,
                                                     const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::Glue::GlueClient client(clientConfig);

    // 1. Upload the job script to the S3 bucket.
    {
        std::cout << "Uploading the job script '"
                  << AwsDoc::Glue::PYTHON_SCRIPT
                  << "'." << std::endl;

        if (!AwsDoc::Glue::uploadFile(bucketName,
                                      AwsDoc::Glue::PYTHON_SCRIPT_PATH,
                                      AwsDoc::Glue::PYTHON_SCRIPT,
                                      clientConfig)) {
            std::cerr << "Error uploading the job file." << std::endl;
            return false;
        }

    }

    // 2. Create a database.
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

    // 3. Create a crawler.
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
            deleteAssets("", CRAWLER_DATABASE_NAME, "", bucketName, clientConfig);
            return false;
        }
    }

    // 4. Get a crawler.
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
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }

    }

    // 5. Start a crawler.
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
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
     }

    // 6. Get a database.
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
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
    }
    
    // 7. Get tables.
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

            if (!tables.empty()) {
                int tableIndex = askQuestionForIntRange(
                        "Enter an index to display the database detail ",
                        1, static_cast<int>(tables.size()));
                std::cout << tables[tableIndex - 1].Jsonize().View().WriteReadable() << std::endl;
            }
        }
        else{
            std::cerr << "Error:  " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
    }

    // 8. Create a job
    {
        Aws::Glue::Model::CreateJobRequest request;
        request.SetName(JOB_NAME);
        request.SetRole(roleName);
        request.SetGlueVersion(GLUE_VERSION);

        Aws::Glue::Model::JobCommand command;
        command.SetName(JOB_COMMAND_NAME);
        command.SetPythonVersion(JOB_PYTHON_VERSION);
        command.SetScriptLocation(
                Aws::String("s3://") + bucketName + "/" + PYTHON_SCRIPT);
        request.SetCommand(command);

        Aws::Glue::Model::CreateJobOutcome outcome = client.CreateJob(request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully created the job." << std::endl;
        }
        else {
            std::cerr << "Error creating the job. " << outcome.GetError().GetMessage()
                      << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
    }

    // 9. Start a job run.
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

            int iterator = 0;
            bool canContinue = true;
            while (canContinue) {
                ++iterator;
                std::this_thread::sleep_for(std::chrono::seconds(1));
                Aws::Glue::Model::GetJobRunRequest jobRunRequest;
                jobRunRequest.SetJobName(JOB_NAME);
                jobRunRequest.SetRunId(jobRunId);

                Aws::Glue::Model::GetJobRunOutcome jobRunOutcome = client.GetJobRun(
                        jobRunRequest);

                if (jobRunOutcome.IsSuccess()) {
                    const Aws::Glue::Model::JobRun &jobRun = jobRunOutcome.GetResult().GetJobRun();
                    Aws::Glue::Model::JobRunState jobRunState = jobRun.GetJobRunState();
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
                        case Aws::Glue::Model::JobRunState::RUNNING:
                            statusString = "RUNNING";
                            break;
                        default:
                            statusString = std::to_string(
                                    static_cast<int>(jobRunState));
                            break;

                    }

                    if ((iterator % 10) == 0) {
                        std::cout << "Job run status " << statusString << ". "
                                  << iterator <<
                                  " seconds elapsed." << std::endl;
                    }

                    if (!canContinue) {
                        std::cout << "Job run state " << statusString << std::endl;

                        if (jobRunState != Aws::Glue::Model::JobRunState::SUCCEEDED) {
                            std::cerr << "Error running job. "
                                      << jobRun.GetErrorMessage()
                                      << std::endl;
                            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME,
                                         bucketName,
                                         clientConfig);
                            return false;
                        }
                    }
                }
                else {
                    std::cerr << "Error starting the job. "
                              << jobRunOutcome.GetError().GetMessage()
                              << std::endl;
                    deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME,
                                 bucketName, clientConfig);
                    return false;
                }
            }
        }
        else {
            std::cerr << "Error . " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, bucketName,
                         clientConfig);
            return false;
        }
    }

    // 10. List all jobs.
    {
        Aws::S3::S3Client s3Client;
        Aws::S3::Model::ListObjectsRequest request;
        request.SetBucket(bucketName);
        request.SetPrefix("run-");

        Aws::S3::Model::ListObjectsOutcome outcome = s3Client.ListObjects(request);

        if (outcome.IsSuccess()) {
            const std::vector<Aws::S3::Model::Object> &objects = outcome.GetResult().GetContents();
            std::cout << "Data from your job is in " << objects.size() <<
                      " files in the S3 bucket, " << bucketName << "." << std::endl;

            for (size_t i = 0; i < objects.size(); ++i) {
                std::cout << "    " << i + 1 << ". " << objects[i].GetKey()
                          << std::endl;
            }

            int objectIndex = askQuestionForIntRange(
                    std::string(
                            "Enter the number of a block to download it and see the first ") +
                    std::to_string(LINES_OF_RUN_FILE_TO_DISPLAY) +
                    " lines of JSON output in the block: ", 1,
                    static_cast<int>(objects.size()));

            Aws::String objectKey = objects[objectIndex - 1].GetKey();

            std::stringstream stringStream;
            if (getObjectFromBucket(bucketName, objectKey, stringStream,
                                    clientConfig)) {
                for (int i = 0; i < LINES_OF_RUN_FILE_TO_DISPLAY && stringStream; ++i) {
                    std::string line;
                    std::getline(stringStream, line);
                    std::cout << "    " << line << std::endl;
                }
            }
            else {
                deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, bucketName,
                             clientConfig);
                return false;
            }
        }
        else {
            std::cerr << "Error . " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, bucketName,
                         clientConfig);
            return false;
        }
    }

    // 11. Get job runs.
    {
        Aws::Glue::Model::ListJobsRequest listJobsRequest;

        Aws::Glue::Model::ListJobsOutcome listRunsOutcome = client.ListJobs(
                listJobsRequest);

        if (listRunsOutcome.IsSuccess()) {
            const std::vector<Aws::String> &jobNames = listRunsOutcome.GetResult().GetJobNames();
            std::cout << "Your account has " << jobNames.size() << " jobs."
                      << std::endl;
            for (size_t i = 0; i < jobNames.size(); ++i) {
                std::cout << "   " << i + 1 << ". " << jobNames[i] << std::endl;
            }

            int jobIndex = askQuestionForIntRange(
                    Aws::String("Enter a number between 1 and ") +
                    std::to_string(jobNames.size()) +
                    " to see the list of runs for a job: ",
                    1, static_cast<int>(jobNames.size()));
            {
                Aws::Glue::Model::GetJobRunsRequest getJobRunsRequest;
                Aws::String jobName = jobNames[jobIndex - 1];
                getJobRunsRequest.SetJobName(jobName);

                Aws::Glue::Model::GetJobRunsOutcome jobRunsOutcome = client.GetJobRuns(
                        getJobRunsRequest);

                if (jobRunsOutcome.IsSuccess()) {
                    std::vector<Aws::Glue::Model::JobRun> jobRuns = jobRunsOutcome.GetResult().GetJobRuns();
                    std::cout << "There are " << jobRuns.size() << " runs in the job '"
                              <<
                              jobName << "'." << std::endl;

                    for (size_t i = 0; i < jobRuns.size(); ++i) {
                        std::cout << "   " << i + 1 << ". " << jobRuns[i].GetJobName()
                                  << std::endl;
                    }

                    int runIndex = askQuestionForIntRange(
                            Aws::String("Enter a number between 1 and ") +
                            std::to_string(jobRuns.size()) +
                            " to see details for a run: ",
                            1, static_cast<int>(jobRuns.size()));

                    std::cout << jobRuns[runIndex - 1].Jsonize().View().WriteReadable()
                              << std::endl;
                }
                else {
                    std::cerr << "Error getting job runs. "
                              << jobRunsOutcome.GetError().GetMessage()
                              << std::endl;
                    deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME,
                                 bucketName,
                                 clientConfig);
                    return false;
                }
            }
        }
        else {
            std::cerr << "Error . " << listRunsOutcome.GetError().GetMessage()
                      << std::endl;
        }

    }

    return deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, bucketName,
                        clientConfig);
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

bool AwsDoc::Glue::deleteAllObjectsInS3Bucket(const Aws::String &bucketName,
                                              const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::ListObjectsRequest listObjectsRequest;
    listObjectsRequest.SetBucket(bucketName);


    Aws::S3::Model::ListObjectsOutcome listObjectsOutcome = client.ListObjects(
            listObjectsRequest);

    bool result = false;
    if (listObjectsOutcome.IsSuccess()) {
        const std::vector<Aws::S3::Model::Object> &objects = listObjectsOutcome.GetResult().GetContents();
        if (!objects.empty()) {
            Aws::S3::Model::DeleteObjectsRequest deleteObjectsRequest;
            deleteObjectsRequest.SetBucket(bucketName);

            std::vector<Aws::S3::Model::ObjectIdentifier> objectIdentifiers;
            for (const Aws::S3::Model::Object &object: objects) {
                objectIdentifiers.push_back(
                        Aws::S3::Model::ObjectIdentifier().WithKey(object.GetKey()));
            }
            Aws::S3::Model::Delete objectsDelete;
            objectsDelete.SetObjects(objectIdentifiers);
            objectsDelete.SetQuiet(true);
            deleteObjectsRequest.SetDelete(objectsDelete);

            Aws::S3::Model::DeleteObjectsOutcome deleteObjectsOutcome =
                    client.DeleteObjects(deleteObjectsRequest);

            if (!deleteObjectsOutcome.IsSuccess()) {
                std::cerr << "Error deleting objects. " <<
                          deleteObjectsOutcome.GetError().GetMessage() << std::endl;
            }
            else {
                std::cout << "Successfully deleted the objects." << std::endl;
                result = true;
            }
        }
        else {
            std::cout << "No objects to delete in '" << bucketName << "'." << std::endl;
        }
    }
    else {
        std::cerr << "Error listing objects. "
                  << listObjectsOutcome.GetError().GetMessage() << std::endl;
    }

    return result;
}

bool AwsDoc::Glue::deleteAssets(const Aws::String &crawler, const Aws::String &database,
                                const Aws::String &job, const Aws::String &bucketName,
                                const Aws::Client::ClientConfiguration &clientConfig) {
    const Aws::Glue::GlueClient client(clientConfig);
    bool result = true;

    // 12. Delete a job.
    if (!job.empty()) {
        Aws::Glue::Model::DeleteJobRequest request;
        request.SetJobName(job);

        Aws::Glue::Model::DeleteJobOutcome outcome = client.DeleteJob(request);


        if (outcome.IsSuccess()) {
            std::cout << "Successfully deleted the job." << std::endl;
        }
        else {
            std::cerr << "Error deleting the job. " << outcome.GetError().GetMessage()
                      << std::endl;
            result = false;
        }

    }

    // 13. Delete a database.
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
            result = false;
        }
    }

    // 14. Delete a crawler.
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

    // 15. Delete the job script and run data from the S3 bucket.
    result &= AwsDoc::Glue::deleteAllObjectsInS3Bucket(bucketName,
                                                      clientConfig);
    return result;
}
bool AwsDoc::Glue::getObjectFromBucket(const Aws::String &bucketName,
                                       const Aws::String &objectKey,
                                       std::ostream &objectStream,
                                       const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::GetObjectRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3::Model::GetObjectOutcome outcome = client.GetObject(request);


    if (outcome.IsSuccess()) {
        std::cout << "Successfully retrieved '" << objectKey << "'." << std::endl;
        auto &body = outcome.GetResult().GetBody();
        objectStream << body.rdbuf();
    }
    else {
        std::cerr << "Error retrieving object. " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
