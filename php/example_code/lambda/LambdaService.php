<?php

#snippet-start:[php.example_code.lambda.service]
namespace Lambda;

use Aws\Lambda\LambdaClient;

class LambdaService extends \AwsUtilities\AWSServiceClass
{
    protected LambdaClient $lambdaClient;

    public function __construct(
        $client = null,
        $region = 'us-west-2',
        $version = 'latest',
        $profile = 'default'
    ) {
        if (gettype($client) == LambdaClient::class) {
            $this->lambdaClient = $client;
            return;
        }
        $this->lambdaClient = new LambdaClient([
            'region' => $region,
            'version' => $version,
            'profile' => $profile,
        ]);
    }

    #snippet-start:[php.example_code.lambda.service.createFunction]
    public function createFunction($functionName, $role, $bucketName, $handler)
    {
        //This assumes the Lambda function is in an S3 bucket.
        return $this->customWaiter(function () use ($functionName, $role, $bucketName, $handler) {
            return $this->lambdaClient->createFunction([
                'Code' => [
                    'S3Bucket' => $bucketName,
                    'S3Key' => $functionName,
                ],
                'FunctionName' => $functionName,
                'Role' => $role['Arn'],
                'Runtime' => 'python3.9',
                'Handler' => "$handler.lambda_handler",
            ]);
        });
    }
    #snippet-end:[php.example_code.lambda.service.createdFunction]

    #snippet-start:[php.example_code.lambda.service.getFunction]
    public function getFunction($functionName)
    {
        return $this->lambdaClient->getFunction([
            'FunctionName' => $functionName,
        ]);
    }
    #snippet-end:[php.example_code.lambda.service.getFunction]

    #snippet-start:[php.example_code.lambda.service.listFunctions]
    public function listFunctions($maxItems = 50, $marker = null)
    {
        if (is_null($marker)) {
            return $this->lambdaClient->listFunctions([
                'MaxItems' => $maxItems,
            ]);
        }

        return $this->lambdaClient->listFunctions([
            'Marker' => $marker,
            'MaxItems' => $maxItems,
        ]);
    }
    #snippet-end:[php.example_code.lambda.service.listFunctions]

    #snippet-start:[php.example_code.lambda.service.invoke]
    public function invoke($functionName, $params, $logType = 'None')
    {
        return $this->lambdaClient->invoke([
            'FunctionName' => $functionName,
            'Payload' => json_encode($params),
            'LogType' => $logType,
        ]);
    }
    #snippet-end:[php.example_code.lambda.service.invoke]

    #snippet-start:[php.example_code.lambda.service.updateFunctionCode]
    public function updateFunctionCode($functionName, $s3Bucket, $s3Key)
    {
        return $this->lambdaClient->updateFunctionCode([
            'FunctionName' => $functionName,
            'S3Bucket' => $s3Bucket,
            'S3Key' => $s3Key,
        ]);
    }
    #snippet-end:[php.example_code.lambda.service.updateFunctionCode]

    #snippet-start:[php.example_code.lambda.service.updateFunctionConfiguration]
    public function updateFunctionConfiguration($functionName, $handler, $environment = '')
    {
        return $this->lambdaClient->updateFunctionConfiguration([
            'FunctionName' => $functionName,
            'Handler' => "$handler.lambda_handler",
            'Environment' => $environment,
        ]);
    }
    #snippet-end:[php.example_code.lambda.service.updateFunctionConfiguration]

    #snippet-start:[php.example_code.lambda.service.deleteFunction]
    public function deleteFunction($functionName)
    {
        return $this->lambdaClient->deleteFunction([
            'FunctionName' => $functionName,
        ]);
    }
    #snippet-end:[php.example_code.lambda.service.deleteFunction]
}
#snippet-end:[php.example_code.lambda.service]
