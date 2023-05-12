<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace lambda\tests;

use Aws\Lambda\LambdaClient;
use Aws\S3\S3Client;
use Iam\IAMService;
use Lambda\LambdaService;
use PHPUnit\Framework\TestCase;

/**
 * @group integ
 */
class LambdaTest extends TestCase
{
    protected array $clientArgs;
    protected LambdaClient $lambdaClient;
    protected LambdaService $lambdaService;
    protected IAMService $iamService;
    protected S3Client $s3client;

    public function setup(): void
    {
        echo "constructor";
        $this->clientArgs = [
            'region' => 'us-west-2',
            'version' => 'latest',
            'profile' => 'default',
        ];
        $this->lambdaClient = new LambdaClient($this->clientArgs);
        $this->lambdaService = new LambdaService();
        $this->iamService = new IAMService();
        $this->s3client = new S3Client($this->clientArgs);
    }

    public function testSingleActionCalls()
    {
        echo "start single action tests\n";
        $uniqid = uniqid();
        $code = __DIR__ . "/../lambda_handler_calculator.zip";
        $functionName = "calculator-$uniqid";
        $lambda_assume_role_policy = "{
        \"Version\": \"2012-10-17\",
            \"Statement\": [
                {
                    \"Effect\": \"Allow\",
                    \"Principal\": {
                        \"Service\": \"lambda.amazonaws.com\"
                    },
                    \"Action\": \"sts:AssumeRole\"
                }
            ]
        }";
        $bucketName = "test-example-bucket-$uniqid";
        $this->s3client->createBucket([
            'Bucket' => $bucketName,
        ]);
        $file = file_get_contents($code);
        $this->s3client->putObject([
            'Bucket' => $bucketName,
            'Key' => $functionName,
            'Body' => $file,
        ]);
        $handler = "lambda_handler_calculator";
        $roleName = "test-lambda-role-$uniqid";
        $role = $this->iamService->createRole($roleName, $lambda_assume_role_policy);
        $this->iamService->attachRolePolicy(
            $role['RoleName'],
            "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
        );
        $this->lambdaService->createFunction($functionName, $role, $bucketName, $handler);

        $functionsList = $this->lambdaService->listFunctions();

        foreach ($functionsList['Functions'] as $functionList) {
            echo "{$functionList['FunctionName']}\n";
        }

        do {
            $function = $this->lambdaService->getFunction($functionName);
        } while ($function['Configuration']['State'] == 'Pending');

        $params = [
            'action' => 'plus',
            'x' => 5,
            'y' => 4,
        ];

        $result = $this->lambdaService->invoke($functionName, $params);

        //Clean up resources
        $this->lambdaService->deleteFunction($functionName);
        $this->iamService->deleteRole($roleName);
        $deleteObjects = $this->s3client->listObjectsV2([
            'Bucket' => $bucketName,
        ]);
        $deleteObjects = $this->s3client->deleteObjects([
            'Bucket' => $bucketName,
            'Delete' => [
                'Objects' => $deleteObjects['Contents'],
            ]
        ]);
        $this->s3client->deleteBucket(['Bucket' => $bucketName]);

        self::assertTrue(true);
    }
}
