<?php

namespace Glue;

use Aws\Glue\GlueClient;
use Aws\Result;

use function PHPUnit\Framework\isEmpty;

class GlueService extends \AwsUtilities\AWSServiceClass
{
    protected GlueClient $glueClient;

    public function __construct($glueClient)
    {
        $this->glueClient = $glueClient;
    }

    #snippet-start:[php.example_code.glue.service.getCrawler]
    public function getCrawler($crawlerName)
    {
        return $this->customWaiter(function () use ($crawlerName) {
            return $this->glueClient->getCrawler([
                'Name' => $crawlerName,
            ]);
        });
    }
    #snippet-end:[php.example_code.glue.service.getCrawler]

    #snippet-start:[php.example_code.glue.service.createCrawler]
    public function createCrawler($crawlerName, $role, $databaseName, $path): Result
    {
        return $this->customWaiter(function () use ($crawlerName, $role, $databaseName, $path) {
            return $this->glueClient->createCrawler([
                'Name' => $crawlerName,
                'Role' => $role,
                'DatabaseName' => $databaseName,
                'Targets' => [
                    'S3Targets' =>
                        [[
                            'Path' => $path,
                        ]]
                ],
            ]);
        });
    }
    #snippet-end:[php.example_code.glue.service.createCrawler]

    #snippet-start:[php.example_code.glue.service.startCrawler]
    public function startCrawler($crawlerName): Result
    {
        return $this->glueClient->startCrawler([
            'Name' => $crawlerName,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.startCrawler]

    #snippet-start:[php.example_code.glue.service.getDatabase]
    public function getDatabase(string $databaseName): Result
    {
        return $this->customWaiter(function () use ($databaseName) {
            return $this->glueClient->getDatabase([
                'Name' => $databaseName,
            ]);
        });
    }
    #snippet-end:[php.example_code.glue.service.getDatabase]

    #snippet-start:[php.example_code.glue.service.getTables]
    public function getTables($databaseName): Result
    {
        return $this->glueClient->getTables([
            'DatabaseName' => $databaseName,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.getTables]

    #snippet-start:[php.example_code.glue.service.createJob]
    public function createJob($jobName, $role, $scriptLocation, $pythonVersion = '3', $glueVersion = '3.0'): Result
    {
        return $this->glueClient->createJob([
            'Name' => $jobName,
            'Role' => $role,
            'Command' => [
                'Name' => 'glueetl',
                'ScriptLocation' => $scriptLocation,
                'PythonVersion' => $pythonVersion,
            ],
            'GlueVersion' => $glueVersion,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.createJob]

    #snippet-start:[php.example_code.glue.service.startJobRun]
    public function startJobRun($jobName, $databaseName, $tables, $outputBucketUrl): Result
    {
        return $this->glueClient->startJobRun([
            'JobName' => $jobName,
            'Arguments' => [
                'input_database' => $databaseName,
                'input_table' => $tables['TableList'][0]['Name'],
                'output_bucket_url' => $outputBucketUrl,
                '--input_database' => $databaseName,
                '--input_table' => $tables['TableList'][0]['Name'],
                '--output_bucket_url' => $outputBucketUrl,
            ],
        ]);
    }
    #snippet-end:[php.example_code.glue.service.startJobRun]

    #snippet-start:[php.example_code.glue.service.listJobs]
    public function listJobs($maxResults = null, $nextToken = null, $tags = []): Result
    {
        $arguments = [];
        if ($maxResults) {
            $arguments['MaxResults'] = $maxResults;
        }
        if ($nextToken) {
            $arguments['NextToken'] = $nextToken;
        }
        if (!isEmpty($tags)) {
            $arguments['Tags'] = $tags;
        }
        return $this->glueClient->listJobs($arguments);
    }
    #snippet-end:[php.example_code.glue.service.listJobs]

    #snippet-start:[php.example_code.glue.service.getJobRuns]
    public function getJobRuns($jobName, $maxResults = 0, $nextToken = ''): Result
    {
        $arguments = ['JobName' => $jobName];
        if ($maxResults) {
            $arguments['MaxResults'] = $maxResults;
        }
        if ($nextToken) {
            $arguments['NextToken'] = $nextToken;
        }
        return $this->glueClient->getJobRuns($arguments);
    }
    #snippet-end:[php.example_code.glue.service.getJobRuns]

    #snippet-start:[php.example_code.glue.service.getJobRun]
    public function getJobRun($jobName, $runId, $predecessorsIncluded = false): Result
    {
        return $this->glueClient->getJobRun([
            'JobName' => $jobName,
            'RunId' => $runId,
            'PredecessorsIncluded' => $predecessorsIncluded,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.getJobRun]

    #snippet-start:[php.example_code.glue.service.deleteJob]
    public function deleteJob($jobName)
    {
        return $this->glueClient->deleteJob([
            'JobName' => $jobName,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.deleteJob]

    #snippet-start:[php.example_code.glue.service.deleteTable]
    public function deleteTable($tableName, $databaseName)
    {
        return $this->glueClient->deleteTable([
            'DatabaseName' => $databaseName,
            'Name' => $tableName,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.deleteTable]

    #snippet-start:[php.example_code.glue.service.deleteDatabase]
    public function deleteDatabase($databaseName)
    {
        return $this->glueClient->deleteDatabase([
            'Name' => $databaseName,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.deleteDatabase]

    #snippet-start:[php.example_code.glue.service.deleteCrawler]
    public function deleteCrawler($crawlerName)
    {
        return $this->glueClient->deleteCrawler([
            'Name' => $crawlerName,
        ]);
    }
    #snippet-end:[php.example_code.glue.service.deleteCrawler]
}
