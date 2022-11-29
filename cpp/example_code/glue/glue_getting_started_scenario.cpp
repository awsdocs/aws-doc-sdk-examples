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
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 * To create the resources required by this example, see the "Prerequisites" section in the README.
 *
 * Purpose
 *
 * This example performs the following tasks:
 *
 * 1. Upload the job script to the S3 bucket.
 * 2. Create a crawler.
 * 3. Get a crawler.
 * 4. Start a crawler.
 * 5. Get a database.
 * 6. Get tables.
 * 7. Create a job.
 * 8. Start a job run.
 * 9. List the output data stored in the S3 bucket.
 * 10. List all the jobs.
 * 11  Get the job runs for a job.
 * 12. Get a single job run.
 * 13. Delete a job.
 * 14. Delete a database.
 * 15. Delete a crawler.
 * 16. Delete the job script and run data from the S3 bucket.
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
        static const Aws::String CRAWLER_DATABASE_NAME("doc-example-database");
        static const Aws::String CRAWLER_DATABASE_PREFIX("doc-example-");
        static const Aws::String CRAWLER_NAME("doc_example_crawler");
        static const Aws::String JOB_NAME("doc-example-job");
        static const Aws::String JOB_COMMAND_NAME("glueetl");
        static const Aws::String JOB_PYTHON_VERSION("3");
        static const Aws::String GLUE_VERSION("3.0");
        static const Aws::String PYTHON_SCRIPT("flight_etl_job_script.py");
        static const Aws::String PYTHON_SCRIPT_PATH(
                SOURCE_DIR "/flight_etl_job_script.py");
        static const Aws::String OUTPUT_FILE_PREFIX("run-");
        static const int LINES_OF_RUN_FILE_TO_DISPLAY = 20;

        //! Routine which uploads a file to an Amazon Simple Storage Service (Amazon S3) bucket.
        /*!
         \\sa uploadFile()
         \param bucketName: An Amazon S3 bucket created in the setup.
         \param filePath: The path of the file to upload.
         \param fileName The name for the uploaded file.
         \param clientConfig Aws client configuration.
         \return bool: Successful completion.
         */
        static bool uploadFile(const Aws::String &bucketName,
                               const Aws::String &filePath,
                               const Aws::String &fileName,
                               const Aws::Client::ClientConfiguration &clientConfig);

        //! Routine which deletes all objects in an Amazon S3 bucket.
        /*!
         \\sa deleteAllObjectsInS3Bucket()
         \param bucketName: The Amazon S3 bucket name.
         \param clientConfig Aws client configuration.
         \return bool: Successful completion.
         */
        static bool deleteAllObjectsInS3Bucket(const Aws::String &bucketName,
                                               const Aws::Client::ClientConfiguration &clientConfig);

        //! Routine which retrieves an object from an Amazon S3 bucket.
        /*!
         \\sa getObjectFromBucket()
         \param bucketName: The Amazon S3 bucket name.
         \param objectKey: The object's name.
         \param objectStream: A stream to receive the retrieved data.
         \param clientConfig Aws client configuration.
         \return bool: Successful completion.
         */
        static bool getObjectFromBucket(const Aws::String &bucketName,
                                        const Aws::String &objectKey,
                                        std::ostream &objectStream,
                                        const Aws::Client::ClientConfiguration &clientConfig);


        //! Cleanup routine to delete created assets.
        /*!
         \\sa deleteAssets()
         \param crawler: Name of an AWS Glue crawler.
         \param database: The name of an AWS Glue database.
         \param job: The name of an AWS Glue job.
         \param bucketName: The name of an Amazon S3 bucket.
         \param clientConfig Aws client configuration.
         \return bool: Successful completion.
         */
        static bool
        deleteAssets(const Aws::String &crawler,
                     const Aws::String &database,
                     const Aws::String &job,
                     const Aws::String &bucketName,
                     const Aws::Client::ClientConfiguration &clientConfig);
    } // Glue
} // AwsDoc

// snippet-start:[cpp.example_code.glue.glue_getting_started_scenario]
//! Scenario which demonstrates using AWS Glue to add a crawler and run a job.
/*!
 \\sa runGettingStartedWithGlueScenario()
 \param bucketName: An Amazon Simple Storage Service (Amazon S3) bucket created in the setup.
 \param roleName: An AWS Identity and Access Management (IAM) role created in the setup.
 \param clientConfig Aws client configuration.
 \return bool: Successful completion.
 */

bool AwsDoc::Glue::runGettingStartedWithGlueScenario(const Aws::String &bucketName,
                                                     const Aws::String &roleName,
                                                     const Aws::Client::ClientConfiguration &clientConfig) {
// snippet-start:[cpp.example_code.glue.glue_client]
    Aws::Glue::GlueClient client(clientConfig);
// snippet-end:[cpp.example_code.glue.glue_client]

    Aws::String roleArn;
    if (!getRoleArn(roleName, roleArn, clientConfig)) {
        std::cerr << "Error getting role ARN for role." << std::endl;
        return false;
    }

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

    // 2. Create a crawler.
    {
// snippet-start:[cpp.example_code.glue.create_crawler]
        Aws::Glue::Model::S3Target s3Target;
        s3Target.SetPath("s3://crawler-public-us-east-1/flight/2016/csv");
        Aws::Glue::Model::CrawlerTargets crawlerTargets;
        crawlerTargets.AddS3Targets(s3Target);

        Aws::Glue::Model::CreateCrawlerRequest request;
        request.SetTargets(crawlerTargets);
        request.SetName(CRAWLER_NAME);
        request.SetDatabaseName(CRAWLER_DATABASE_NAME);
        request.SetTablePrefix(CRAWLER_DATABASE_PREFIX);
        request.SetRole(roleArn);

        Aws::Glue::Model::CreateCrawlerOutcome outcome = client.CreateCrawler(request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully created the crawler." << std::endl;
        }
        else {
            std::cerr << "Error creating a crawler. " << outcome.GetError().GetMessage()
                      << std::endl;
            deleteAssets("", CRAWLER_DATABASE_NAME, "", bucketName, clientConfig);
            return false;
        }
// snippet-end:[cpp.example_code.glue.create_crawler]
    }

    // 3. Get a crawler.
    {
// snippet-start:[cpp.example_code.glue.get_crawler]
        Aws::Glue::Model::GetCrawlerRequest request;
        request.SetName(CRAWLER_NAME);

        Aws::Glue::Model::GetCrawlerOutcome outcome = client.GetCrawler(request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully retrieved crawler." << std::endl;
        }
        else {
            std::cerr << "Error retrieving a crawler.  "
                      << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
// snippet-end:[cpp.example_code.glue.get_crawler]
    }

    // 4. Start a crawler.
    {
// snippet-start:[cpp.example_code.glue.start_crawler]
        Aws::Glue::Model::StartCrawlerRequest request;
        request.SetName(CRAWLER_NAME);

        Aws::Glue::Model::StartCrawlerOutcome outcome = client.StartCrawler(request);


        if (outcome.IsSuccess()) {
            std::cout << "Starting crawler. This may take awhile." << std::endl;

            Aws::Glue::Model::CrawlerState crawlerState = Aws::Glue::Model::CrawlerState::NOT_SET;
            int iterations = 0;
            while (Aws::Glue::Model::CrawlerState::READY != crawlerState) {
                std::this_thread::sleep_for(std::chrono::seconds(1));
                ++iterations;
                if ((iterations % 10) == 0) {
                    std::cout << "Checking crawler status. " << iterations
                              << " seconds elapsed."
                              << std::endl;
                }
                Aws::Glue::Model::GetCrawlerRequest getCrawlerRequest;
                getCrawlerRequest.SetName(CRAWLER_NAME);

                Aws::Glue::Model::GetCrawlerOutcome getCrawlerOutcome = client.GetCrawler(
                        getCrawlerRequest);

                if (getCrawlerOutcome.IsSuccess()) {
                    crawlerState = getCrawlerOutcome.GetResult().GetCrawler().GetState();
                }
                else {
                    std::cerr << "Error getting crawler.  "
                              << getCrawlerOutcome.GetError().GetMessage() << std::endl;
                    break;
                }
            }

            if (Aws::Glue::Model::CrawlerState::READY == crawlerState) {
                std::cout << "Crawler running after " << iterations << " seconds."
                          << std::endl;
            }
        }
        else {
            std::cerr << "Error starting a crawler.  " << outcome.GetError().GetMessage()
                      << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
// snippet-end:[cpp.example_code.glue.start_crawler]
    }

    // 5. Get a database.
    {
// snippet-start:[cpp.example_code.glue.get_database]
        Aws::Glue::Model::GetDatabaseRequest request;
        request.SetName(CRAWLER_DATABASE_NAME);

        Aws::Glue::Model::GetDatabaseOutcome outcome = client.GetDatabase(request);

        if (outcome.IsSuccess()) {
            const Aws::Glue::Model::Database &database = outcome.GetResult().GetDatabase();

            std::cout << "Successfully retrieve the database\n" <<
                      database.Jsonize().View().WriteReadable() << "'." << std::endl;
        }
        else {
            std::cerr << "Error getting the database.  "
                      << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
// snippet-end:[cpp.example_code.glue.get_database]
    }

    // 6. Get tables.
    Aws::String tableName;
    {
// snippet-start:[cpp.example_code.glue.get_tables]
        Aws::Glue::Model::GetTablesRequest request;
        request.SetDatabaseName(CRAWLER_DATABASE_NAME);

        Aws::Glue::Model::GetTablesOutcome outcome = client.GetTables(request);

        if (outcome.IsSuccess()) {
            const std::vector<Aws::Glue::Model::Table> &tables = outcome.GetResult().GetTableList();
            std::cout << "The database contains " << tables.size()
                      << (tables.size() == 1 ?
                          " table." : "tables.") << std::endl;
            std::cout << "Here is a list of the tables in the database.";
            for (size_t index = 0; index < tables.size(); ++index) {
                std::cout << "    " << index + 1 << ":  " << tables[index].GetName()
                          << std::endl;
            }

            if (!tables.empty()) {
                int tableIndex = askQuestionForIntRange(
                        "Enter an index to display the database detail ",
                        1, static_cast<int>(tables.size()));
                std::cout << tables[tableIndex - 1].Jsonize().View().WriteReadable()
                          << std::endl;
            }
        }
        else {
            std::cerr << "Error getting the tables. " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, "", bucketName,
                         clientConfig);
            return false;
        }
// snippet-end:[cpp.example_code.glue.get_tables]
    }

    // 7. Create a job
    {
// snippet-start:[cpp.example_code.glue.create_job]
        Aws::Glue::Model::CreateJobRequest request;
        request.SetName(JOB_NAME);
        request.SetRole(roleArn);
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
// snippet-end:[cpp.example_code.glue.create_job]
    }

    // 8. Start a job run.
    {
// snippet-start:[cpp.example_code.glue.start_job_run]
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
                    std::cerr << "Error retrieving job run state. "
                              << jobRunOutcome.GetError().GetMessage()
                              << std::endl;
                    deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME,
                                 bucketName, clientConfig);
                    return false;
                }
            }
        }
        else {
            std::cerr << "Error starting a job. " << outcome.GetError().GetMessage() << std::endl;
            deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, bucketName,
                         clientConfig);
            return false;
        }
// snippet-end:[cpp.example_code.glue.start_job_run]
    }

    // 9. List the output data stored in the S3 bucket.
    {
        Aws::S3::S3Client s3Client;
        Aws::S3::Model::ListObjectsRequest request;
        request.SetBucket(bucketName);
        request.SetPrefix(OUTPUT_FILE_PREFIX);

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
            std::cerr << "Error listing objects. " << outcome.GetError().GetMessage() << std::endl;
        }
    }

    // 10. List all the jobs.
    Aws::String jobName;
    {
// snippet-start:[cpp.example_code.glue.list_jobs]
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

            jobName = jobNames[jobIndex - 1];
        }
        else {
            std::cerr << "Error listing jobs. " << listRunsOutcome.GetError().GetMessage()
                      << std::endl;
        }
// snippet-end:[cpp.example_code.glue.list_jobs]
    }

    // 11. Get the job runs for a job.
    Aws::String jobRunID;
    if (!jobName.empty()) {
// snippet-start:[cpp.example_code.glue.get_job_runs]
        Aws::Glue::Model::GetJobRunsRequest getJobRunsRequest;
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
            jobRunID = jobRuns[runIndex - 1].GetId();
        }
        else {
            std::cerr << "Error getting job runs. "
                      << jobRunsOutcome.GetError().GetMessage()
                      << std::endl;
        }
// snippet-end:[cpp.example_code.glue.get_job_runs]
    }

    // 12. Get a single job run.
    if (!jobRunID.empty()) {
// snippet-start:[cpp.example_code.glue.get_job_run]
        Aws::Glue::Model::GetJobRunRequest jobRunRequest;
        jobRunRequest.SetJobName(jobName);
        jobRunRequest.SetRunId(jobRunID);

        Aws::Glue::Model::GetJobRunOutcome jobRunOutcome = client.GetJobRun(
                jobRunRequest);

        if (jobRunOutcome.IsSuccess()) {
            std::cout << "Displaying the job run JSON description." << std::endl;
            std::cout
                    << jobRunOutcome.GetResult().GetJobRun().Jsonize().View().WriteReadable()
                    << std::endl;
        }
        else {
            std::cerr << "Error get a job run. " << jobRunOutcome.GetError().GetMessage()
                      << std::endl;
        }
// snippet-end:[cpp.example_code.glue.get_job_run]
    }

    return deleteAssets(CRAWLER_NAME, CRAWLER_DATABASE_NAME, JOB_NAME, bucketName,
                        clientConfig);
}

//! Cleanup routine to delete created assets.
/*!
 \\sa deleteAssets()
 \param crawler: Name of an AWS Glue crawler.
 \param database: The name of an AWS Glue database.
 \param job: The name of an AWS Glue job.
 \param bucketName: The name of an Amazon S3 bucket.
 \param clientConfig Aws client configuration.
 \return bool: Successful completion.
 */
bool AwsDoc::Glue::deleteAssets(const Aws::String &crawler, const Aws::String &database,
                                const Aws::String &job, const Aws::String &bucketName,
                                const Aws::Client::ClientConfiguration &clientConfig) {
    const Aws::Glue::GlueClient client(clientConfig);
    bool result = true;

    // 13. Delete a job.
    if (!job.empty()) {
// snippet-start:[cpp.example_code.glue.delete_job]
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
// snippet-end:[cpp.example_code.glue.delete_job]
    }

    // 14. Delete a database.
    if (!database.empty()) {
// snippet-start:[cpp.example_code.glue.delete_database]
        Aws::Glue::Model::DeleteDatabaseRequest request;
        request.SetName(database);

        Aws::Glue::Model::DeleteDatabaseOutcome outcome = client.DeleteDatabase(
                request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully deleted the database." << std::endl;
        }
        else {
            std::cerr << "Error deleting database. " << outcome.GetError().GetMessage()
                      << std::endl;
            result = false;
        }
// snippet-end:[cpp.example_code.glue.delete_database]
    }

    // 15. Delete a crawler.
    if (!crawler.empty()) {
// snippet-start:[cpp.example_code.glue.delete_crawler]
        Aws::Glue::Model::DeleteCrawlerRequest request;
        request.SetName(crawler);

        Aws::Glue::Model::DeleteCrawlerOutcome outcome = client.DeleteCrawler(request);

        if (outcome.IsSuccess()) {
            std::cout << "Successfully deleted the crawler." << std::endl;
        }
        else {
            std::cerr << "Error deleting the crawler. "
                      << outcome.GetError().GetMessage() << std::endl;
            result = false;
        }
// snippet-end:[cpp.example_code.glue.delete_crawler]
    }

    // 16. Delete the job script and run data from the S3 bucket.
    result &= AwsDoc::Glue::deleteAllObjectsInS3Bucket(bucketName,
                                                       clientConfig);
    return result;
}

//! Routine which uploads a file to an Amazon S3 bucket.
/*!
 \\sa uploadFile()
 \param bucketName: An Amazon S3 bucket created in the setup.
 \param filePath: The path of the file to upload.
 \param fileName The name for the uploaded file.
 \param clientConfig Aws client configuration.
 \return bool: Successful completion.
 */
bool
AwsDoc::Glue::uploadFile(const Aws::String &bucketName,
                         const Aws::String &filePath,
                         const Aws::String &fileName,
                         const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucketName);
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

//! Routine which deletes all objects in an Amazon S3 bucket.
/*!
 \\sa deleteAllObjectsInS3Bucket()
 \param bucketName: The Amazon S3 bucket name.
 \param clientConfig Aws client configuration.
 \return bool: Successful completion.
 */
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

//! Routine which retrieves an object from an Amazon S3 bucket.
/*!
 \\sa getObjectFromBucket()
 \param bucketName: The Amazon S3 bucket name.
 \param objectKey: The object's name.
 \param objectStream: A stream to receive the retrieved data.
 \param clientConfig Aws client configuration.
 \return bool: Successful completion.
 */
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

// snippet-end:[cpp.example_code.glue.glue_getting_started_scenario]

/*
 *
 *  main function
 *
 * Prerequisites: An IAM role and an Amazon S3 bucket.
 *
 * To create the resources required by this example, see the "Prerequisites" section in the README.
 *
 * Usage: 'run_glue_getting_started_scenario.cpp <role_name> <bucket_name'
 *
  */

#ifndef TESTING_BUILD

int main(int argc, const char *argv[]) {
    if (argc != 3) {
        std::cout
                << "Usage: run_glue_getting_started_scenario.cpp <role_name> <bucket_name"
                << std::endl;
        std::cout
                << "To create the resources required by this example, see the \"Prerequisites\" section in the README."
                << std::endl;
        return 1;
    }
    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::String roleArn = argv[1];
        Aws::String bucketName = argv[2];

// snippet-start:[cpp.example_code.glue.client_configuration]
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
// snippet-end:[cpp.example_code.glue.client_configuration]

        AwsDoc::Glue::runGettingStartedWithGlueScenario(bucketName, roleArn,
                                                        clientConfig);
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD


