<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 * Shows how to use the AWS SDK for PHP with Glue to
 **/

# snippet-start:[php.example_code.glue.basics.scenario]
namespace Glue;

use Aws\Glue\GlueClient;
use Aws\S3\S3Client;
use AwsUtilities\AWSServiceClass;
use GuzzleHttp\Psr7\Stream;
use Iam\IamService;

class GettingStartedWithGlue
{
    public function run()
    {
        echo("--------------------------------------\n");
        print("Welcome to the Amazon Glue getting started demo using PHP!\n");
        echo("--------------------------------------\n");

        $clientArgs = [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $uniqid = uniqid();

        $glueClient = new GlueClient($clientArgs);
        $glueService = new GlueService($glueClient);
        $iamService = new IamService();
        #snippet-start:[php.example_code.glue.basics.crawlerName]
        $crawlerName = "example-crawler-test-" . $uniqid;
        #snippet-end:[php.example_code.glue.basics.crawlerName]

        AWSServiceClass::$waitTime = 5;
        AWSServiceClass::$maxWaitAttempts = 20;

        #snippet-start:[php.example_code.glue.basics.getRole]
        $role = $iamService->getRole("AWSGlueServiceRole-DocExample");
        #snippet-end:[php.example_code.glue.basics.getRole]

        #snippet-start:[php.example_code.glue.basics.databaseName]
        $databaseName = "doc-example-database-$uniqid";
        #snippet-end:[php.example_code.glue.basics.databaseName]
        #snippet-start:[php.example_code.glue.basics.createCrawler]
        $path = 's3://crawler-public-us-east-1/flight/2016/csv';
        $glueService->createCrawler($crawlerName, $role['Role']['Arn'], $databaseName, $path);
        #snippet-end:[php.example_code.glue.basics.createCrawler]
        #snippet-start:[php.example_code.glue.basics.startCrawler]
        $glueService->startCrawler($crawlerName);
        #snippet-end:[php.example_code.glue.basics.startCrawler]

        #snippet-start:[php.example_code.glue.basics.getCrawler]
        echo "Waiting for crawler";
        do {
            $crawler = $glueService->getCrawler($crawlerName);
            echo ".";
            sleep(10);
        } while ($crawler['Crawler']['State'] != "READY");
        echo "\n";
        #snippet-end:[php.example_code.glue.basics.getCrawler]

        #snippet-start:[php.example_code.glue.basics.getDatabase]
        $database = $glueService->getDatabase($databaseName);
        echo "Found a database named " . $database['Database']['Name'] . "\n";
        #snippet-end:[php.example_code.glue.basics.getDatabase]

        //Upload job script
        $s3client = new S3Client($clientArgs);
        $bucketName = "test-glue-bucket-" . $uniqid;
        $s3client->createBucket([
            'Bucket' => $bucketName,
            'CreateBucketConfiguration' => ['LocationConstraint' => 'us-west-2'],
        ]);

        $s3client->putObject([
            'Bucket' => $bucketName,
            'Key' => 'run_job.py',
            'SourceFile' => 'glue/flight_etl_job_script.py'
        ]);
        $s3client->putObject([
            'Bucket' => $bucketName,
            'Key' => 'setup_scenario_getting_started.yaml',
            'SourceFile' => 'glue/setup_scenario_getting_started.yaml'
        ]);

        #snippet-start:[php.example_code.glue.basics.getTables]
        $tables = $glueService->getTables($databaseName);
        #snippet-end:[php.example_code.glue.basics.getTables]

        #snippet-start:[php.example_code.glue.basics.jobName]
        $jobName = 'test-job-' . $uniqid;
        #snippet-end:[php.example_code.glue.basics.jobName]
        #snippet-start:[php.example_code.glue.basics.createJob]
        $scriptLocation = "s3://$bucketName/run_job.py";
        $job = $glueService->createJob($jobName, $role['Role']['Arn'], $scriptLocation);
        #snippet-end:[php.example_code.glue.basics.createJob]

        #snippet-start:[php.example_code.glue.basics.startJobRun]
        $outputBucketUrl = "s3://$bucketName";
        $runId = $glueService->startJobRun($jobName, $databaseName, $tables, $outputBucketUrl)['JobRunId'];
        #snippet-end:[php.example_code.glue.basics.startJobRun]

        #snippet-start:[php.example_code.glue.basics.getJobRun]
        echo "waiting for job";
        do {
            $jobRun = $glueService->getJobRun($jobName, $runId);
            echo ".";
            sleep(10);
        } while (!array_intersect([$jobRun['JobRun']['JobRunState']], ['SUCCEEDED', 'STOPPED', 'FAILED', 'TIMEOUT']));
        echo "\n";
        #snippet-end:[php.example_code.glue.basics.getJobRun]

        #snippet-start:[php.example_code.glue.basics.getJobRuns]
        $jobRuns = $glueService->getJobRuns($jobName);
        #snippet-end:[php.example_code.glue.basics.getJobRuns]

        $objects = $s3client->listObjects([
            'Bucket' => $bucketName,
        ])['Contents'];

        foreach ($objects as $object) {
            echo $object['Key'] . "\n";
        }

        echo "Downloading " . $objects[2]['Key'] . "\n";
        /** @var Stream $downloadObject */
        $downloadObject = $s3client->getObject([
            'Bucket' => $bucketName,
            'Key' => $objects[2]['Key'],
        ])['Body']->getContents();
        echo "Here is the first 1000 characters in the object.";
        echo substr($downloadObject, 0, 1000);

        #snippet-start:[php.example_code.glue.basics.listJobs]
        $jobs = $glueService->listJobs();
        echo "Current jobs:\n";
        foreach ($jobs['JobNames'] as $jobsName) {
            echo "{$jobsName}\n";
        }
        #snippet-end:[php.example_code.glue.basics.listJobs]

        #snippet-start:[php.example_code.glue.basics.deleteJob]
        echo "Delete the job.\n";
        $glueClient->deleteJob([
            'JobName' => $job['Name'],
        ]);
        #snippet-end:[php.example_code.glue.basics.deleteJob]

        #snippet-start:[php.example_code.glue.basics.deleteTable]
        echo "Delete the tables.\n";
        foreach ($tables['TableList'] as $table) {
            $glueService->deleteTable($table['Name'], $databaseName);
        }
        #snippet-end:[php.example_code.glue.basics.deleteTable]

        #snippet-start:[php.example_code.glue.basics.deleteDatabase]
        echo "Delete the databases.\n";
        $glueClient->deleteDatabase([
            'Name' => $databaseName,
        ]);
        #snippet-end:[php.example_code.glue.basics.deleteDatabase]

        #snippet-start:[php.example_code.glue.basics.deleteCrawler]
        echo "Delete the crawler.\n";
        $glueClient->deleteCrawler([
            'Name' => $crawlerName,
        ]);
        #snippet-end:[php.example_code.glue.basics.deleteCrawler]

        $deleteObjects = $s3client->listObjectsV2([
            'Bucket' => $bucketName,
        ]);
        echo "Delete all objects in the bucket.\n";
        $deleteObjects = $s3client->deleteObjects([
            'Bucket' => $bucketName,
            'Delete' => [
                'Objects' => $deleteObjects['Contents'],
            ]
        ]);
        echo "Delete the bucket.\n";
        $s3client->deleteBucket(['Bucket' => $bucketName]);

        echo "This job was brought to you by the number $uniqid\n";
    }
}
# snippet-end:[php.example_code.glue.basics.scenario]
