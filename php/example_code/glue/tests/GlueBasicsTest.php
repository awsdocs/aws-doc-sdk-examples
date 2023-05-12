<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

#
# Integration test runner for AWS Glue files.
#

namespace glue\tests;

use Aws\CloudFormation\CloudFormationClient;
use Aws\Glue\GlueClient;
use Aws\Iam\IamClient;
use Aws\S3\S3Client;
use Glue\GettingStartedWithGlue;
use Glue\GlueService;
use Iam\IAMService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class GlueBasicsTest extends TestCase
{
    /**
     * @var string[]
     */
    private array $clientArgs;
    private S3Client $s3Client;

    public function setup(): void
    {
        $this->clientArgs = [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $this->s3Client = new S3Client($this->clientArgs);
    }

    public function testItRunsWithoutThrowingAnException()
    {
        $start = new GettingStartedWithGlue();
        $start->run();
        self::assertTrue(true); //This asserts we made it to this line with no exceptions thrown.
    }

    public function testSingleActionCalls()
    {
        $clientArgs = [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $glueClient = new GlueClient($clientArgs);
        $s3Client = new S3Client($clientArgs);
        $iamClient = new IamClient($clientArgs);

        $glueService = new GlueService($glueClient);
        $iamService = new IAMService($iamClient);
        $uniqid = uniqid();

        $template = file_get_contents(__DIR__ . '/../setup_scenario_getting_started.yaml');

        $cloudFormationClient = new CloudFormationClient($clientArgs);
        $cloudFormationClient->createStack([
            'StackName' => 'test-stack-' . $uniqid,
            'TemplateBody' => $template,
            'Capabilities' => ['CAPABILITY_NAMED_IAM'],
        ]);

        $databaseName = 'doc-example-database-' . $uniqid;
        $role = $iamService->getRole("AWSGlueServiceRole-DocExample");

        $path = 's3://crawler-public-us-east-1/flight/2016/csv';
        $crawlerName = "test-crawler-name-" . $uniqid;

        $glueService->createCrawler($crawlerName, $role['Role']['Arn'], $databaseName, $path);

        $glueService->startCrawler($crawlerName);

        do {
            $crawler = $glueService->getCrawler($crawlerName);
            sleep(10);
        } while ($crawler['Crawler']['State'] != "READY");

        self::assertArrayHasKey('Crawler', $crawler);
        self::assertArrayHasKey('Name', $crawler['Crawler']);
        self::assertEquals($crawlerName, $crawler['Crawler']['Name']);
        self::assertEquals($databaseName, $crawler['Crawler']['DatabaseName']);

        $database = $glueService->getDatabase($databaseName);
        self::assertArrayHasKey('Database', $database);
        self::assertEquals($databaseName, $database['Database']['Name']);

        $tables = $glueService->getTables($databaseName);
        self::assertArrayHasKey('TableList', $tables);
        self::assertEquals($databaseName, $tables['TableList'][0]['DatabaseName']);
        self::assertEquals('csv', $tables['TableList'][0]['Name']);

        $bucketName = "test-glue-bucket-" . $uniqid;
        $this->uploadJobInfo($s3Client, $bucketName);

        $jobName = 'test-job-' . $uniqid;
        $scriptLocation = "s3://$bucketName/run_job.py";
        $job = $glueService->createJob($jobName, $role['Role']['Arn'], $scriptLocation);
        self::assertEquals($jobName, $job['Name']);

        $outputBucketUrl = "s3://$bucketName";
        $jobRun = $glueService->startJobRun($jobName, $databaseName, $tables, $outputBucketUrl);
        $jobRunId = $jobRun['JobRunId'];
        do {
            $jobRun = $glueService->getJobRun($jobName, $jobRunId);
            sleep(10);
        } while (!array_intersect([$jobRun['JobRun']['JobRunState']], ['SUCCEEDED', 'STOPPED', 'FAILED', 'TIMEOUT']));
        self::assertEquals($jobRunId, $jobRun['JobRun']['Id']);

        $jobs = $glueService->listJobs();
        self::assertTrue(in_array($jobName, $jobs['JobNames']));

        $jobRuns = $glueService->getJobRuns($jobName);
        self::assertEquals($jobName, $jobRuns['JobRuns'][0]['JobName']);

        $deleteJob = $glueService->deleteJob($jobName);
        self::assertNotNull($deleteJob);

        $deleteTable = $glueService->deleteTable($tables['TableList'][0]['Name'], $databaseName);
        self::assertNotNull($deleteTable);

        $deleteDatabase = $glueService->deleteDatabase($databaseName);
        self::assertNotNull($deleteDatabase);

        $deleteCrawler = $glueService->deleteCrawler($crawlerName);
        self::assertNotNull($deleteCrawler);

        $deleteObjects = $s3Client->listObjectsV2([
            'Bucket' => $bucketName,
        ]);
        $deleteObjects = $s3Client->deleteObjects([
            'Bucket' => $bucketName,
            'Delete' => [
                'Objects' => $deleteObjects['Contents'],
            ]
        ]);
        $s3Client->deleteBucket(['Bucket' => $bucketName]);
    }

    private function uploadJobInfo($s3client, $bucketName)
    {
        $s3client->createBucket([
            'Bucket' => $bucketName,
            'CreateBucketConfiguration' => ['LocationConstraint' => 'us-west-2'],
        ]);
        $s3client->putObject([
            'Bucket' => $bucketName,
            'Key' => 'run_job.py',
            'SourceFile' => __DIR__ . '/../flight_etl_job_script.py'
        ]);
        $s3client->putObject([
            'Bucket' => $bucketName,
            'Key' => 'setup_scenario_getting_started.yaml',
            'SourceFile' => __DIR__ . '/../setup_scenario_getting_started.yaml'
        ]);
    }
}
