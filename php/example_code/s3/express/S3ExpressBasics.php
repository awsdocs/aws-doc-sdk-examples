<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Purpose
 *
 * Shows how to use AWS SDK for PHP v3 to get started using Amazon S3 Express One Zone.
 * Create a Directory bucket, move objects into and out of it, and delete all
 * resources at the end of the demo. This demo will showcase topics specific to S3
 * Express One Zone such as authentication sessions.
 *
 * You will need to run the following command to install dependencies:
 * composer install
 *
 * Then run the example either directly with:
 * php S3ExpressBasics.php
 *
 * or as a PHPUnit test:
 * vendor/bin/phpunit S3ExpressBasicsTests.php
 **/

namespace S3\express;
use Aws\CloudFormation\CloudFormationClient;
use Aws\CloudFormation\Exception\CloudFormationException;
use Aws\Credentials\Credentials;
use Aws\Ec2\Ec2Client;
use Aws\Iam\IamClient;
use Aws\S3\S3Client;
use Aws\Sts\StsClient;
use AwsUtilities\RunnableExample;
use Ec2\EC2Service;
use Iam\IAMService;
use S3\S3Service;
use function AwsUtilities\pressEnter;
use function AwsUtilities\testable_readline;

class S3ExpressBasics implements RunnableExample
{

    // This is created to do clean up at the end.
    protected S3Service $s3Service;

    // These services are also needed during this demo.
    protected IAMService $iamService;
    protected EC2Service $ec2Service;
    protected StsClient $stsClient;
    protected CloudFormationClient $cloudFormationClient;

    // This will keep track of all created resources so that we can clean them up at the end.
    protected array $resources = [];

    public function runExample()
    {
        // snippet-start:[php.example_code.s3.ExpressBasics]
        echo "\n";
        echo "--------------------------------------\n";
        echo "Welcome to the Amazon S3 Express Basics demo using PHP!\n";
        echo "--------------------------------------\n";

        // Change these both of these values to use a different region/availability zone.
        $region = "us-west-2";
        $az = "usw2-az1";

        $this->s3Service = new S3Service(new S3Client(['region' => $region]));
        $this->iamService = new IAMService(new IamClient(['region' => $region]));

        $uuid = uniqid();

        echo <<<INTRO
Let's get started! First, please note that S3 Express One Zone works best when working within the AWS infrastructure,
specifically when working in the same Availability Zone. To see the best results in this example, and when you implement
Directory buckets into your infrastructure, it is best to put your Compute resources in the same AZ as your Directory
bucket.\n
INTRO;
        pressEnter();
        // 1. Configure a gateway VPC endpoint. This is the recommended method to allow S3 Express One Zone traffic without
        // the need to pass through an internet gateway or NAT device.
        echo "\n";
        echo "1. First, we'll set up a new VPC and VPC Endpoint if this program is running in an EC2 instance in the same AZ as your Directory buckets will be.\n";
        $ec2Choice = testable_readline("Are you running this in an EC2 instance located in the same AZ as your intended Directory buckets? Enter Y/y to setup a VPC Endpoint, or N/n/blank to skip this section.");
        if($ec2Choice == "Y" || $ec2Choice == "y") {
            echo "Great! Let's set up a VPC, retrieve the Route Table from it, and create a VPC Endpoint to connect the S3 Client to.\n";
            pressEnter();
            $this->ec2Service = new EC2Service(new Ec2Client(['region' => $region]));
            $cidr = "10.0.0.0/16";
            $vpc = $this->ec2Service->createVpc($cidr);
            $this->resources['vpcId'] = $vpc['VpcId'];

            $this->ec2Service->waitForVpcAvailable($vpc['VpcId']);

            $routeTable = $this->ec2Service->describeRouteTables([], [
                [
                    'Name' => "vpc-id",
                    'Values' => [$vpc['VpcId']],
                ],
            ]);

            $serviceName = "com.amazonaws." . $this->ec2Service->getRegion() . ".s3express";
            $vpcEndpoint = $this->ec2Service->createVpcEndpoint($serviceName, $vpc['VpcId'], [$routeTable[0]]);
            $this->resources['vpcEndpointId'] = $vpcEndpoint['VpcEndpointId'];
        }else{
            echo "Skipping the VPC setup. Don't forget to use this in production!\n";
        }

        // 2. Policies, user, and roles with CDK.
        echo "\n";
        echo "2. Policies, users, and roles with CDK.\n";
        echo "Now, we'll set up some policies, roles, and a user. This user will only have permissions to do S3 Express One Zone actions.\n";
        pressEnter();

        $this->cloudFormationClient = new CloudFormationClient([]);
        $stackName = "cfn-stack-s3-express-basics-" . uniqid();
        $file = file_get_contents(__DIR__ . "/../../../../resources/cfn/s3_express_basics/s3_express_template.yml");
        $result = $this->cloudFormationClient->createStack([
            'StackName' => $stackName,
            'TemplateBody' => $file,
            'Capabilities' => ['CAPABILITY_IAM'],
        ]);
        $waiter = $this->cloudFormationClient->getWaiter("StackCreateComplete", ['StackName' => $stackName]);
        try {
            $waiter->promise()->wait();
        }catch(CloudFormationException $caught){
            echo "Error waiting for the CloudFormation stack to create: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
        $this->resources['stackName'] = $stackName;
        $stackInfo = $this->cloudFormationClient->describeStacks([
            'StackName' => $result['StackId'],
        ]);

        $expressUserName = "";
        $regularUserName = "";
        foreach($stackInfo['Stacks'][0]['Outputs'] as $output) {
            if ($output['OutputKey'] == "RegularUser") {
                $regularUserName = $output['OutputValue'];
            }
            if ($output['OutputKey'] == "ExpressUser") {
                $expressUserName = $output['OutputValue'];
            }
        }
        $regularKey = $this->iamService->createAccessKey($regularUserName);
        $regularCredentials = new Credentials($regularKey['AccessKeyId'], $regularKey['SecretAccessKey']);
        $expressKey = $this->iamService->createAccessKey($expressUserName);
        $expressCredentials = new Credentials($expressKey['AccessKeyId'], $expressKey['SecretAccessKey']);

        // 3. Create an additional client using the credentials with S3 Express permissions.
        echo "\n";
        echo "3. Create an additional client using the credentials with S3 Express permissions.\n";
        echo "This client is created with the credentials associated with the user account with the S3 Express policy attached, so it can perform S3 Express operations.\n";
        pressEnter();
        $s3RegularClient = new S3Client([
            'Region' => $region,
            'Credentials' => $regularCredentials,
        ]);
        $s3RegularService = new S3Service($s3RegularClient);
        $s3ExpressClient = new S3Client([
            'Region' => $region,
            'Credentials' => $expressCredentials,
        ]);
        $s3ExpressService = new S3Service($s3ExpressClient);
        echo "All the roles and policies were created an attached to the user. Then, a new S3 Client and Service were created using that user's credentials.\n";
        echo "We can now use this client to make calls to S3 Express operations. Keeping permissions in mind (and adhering to least-privilege) is crucial to S3 Express.\n";
        pressEnter();

        // 4. Create two buckets.
        echo "\n";
        echo "3. Create two buckets.\n";
        echo "Now we will create a Directory bucket, which is the linchpin of the S3 Express One Zone service.\n";
        echo "Directory buckets behave in different ways from regular S3 buckets, which we will explore here.\n";
        echo "We'll also create a normal bucket, put an object into the normal bucket, and copy it over to the Directory bucket.\n";
        pressEnter();

        // Create a directory bucket. These are different from normal S3 buckets in subtle ways.
        $directoryBucketName = "s3-express-demo-directory-bucket-$uuid--$az--x-s3";
        echo "Now, let's create the actual Directory bucket, as well as a regular bucket.\n";
        pressEnter();
        $s3ExpressService->createBucket($directoryBucketName, [
            'CreateBucketConfiguration' => [
                'Bucket' => [
                    'Type' => "Directory", // This is what causes S3 to create a Directory bucket as opposed to a normal bucket.
                    'DataRedundancy' => "SingleAvailabilityZone",
                ],
                'Location' => [
                    'Name' => $az,
                    'Type' => "AvailabilityZone",
                ],
            ],
        ]);
        $this->resources['directoryBucketName'] = $directoryBucketName;

        // Create a normal bucket.
        $normalBucketName = "normal-bucket-$uuid";
        $s3RegularService->createBucket($normalBucketName);
        $this->resources['normalBucketName'] = $normalBucketName;
        echo "Great! Both buckets were created.\n";
        pressEnter();

        // 5. Create an object and copy it over.
        echo "\n";
        echo "5. Create an object and copy it over.\n";
        echo "We'll create a basic object consisting of some text and upload it to the normal bucket.\n";
        echo "Next, we'll copy the object into the Directory bucket using the regular client.\n";
        echo "This works fine, because Copy operations are not restricted for Directory buckets.\n";
        pressEnter();

        $objectKey = "basic-text-object";
        $s3RegularService->putObject($normalBucketName, $objectKey, $args = ['Body' => "Look Ma, I'm a bucket!"]);
        $this->resources['objectKey'] = $objectKey;

        // Create a session to access the directory bucket. The SDK Client will automatically refresh this as needed.
        $s3ExpressService->createSession($directoryBucketName);
        $s3ExpressService->copyObject($directoryBucketName, $objectKey, "$normalBucketName/$objectKey");

        echo "It worked! It's important to remember the user permissions when interacting with Directory buckets.\n";
        echo "Instead of validating permissions on every call as normal buckets do, Directory buckets utilize the user credentials and session token to validate.\n";
        echo "This allows for much faster connection speeds on every call. For single calls, this is low, but for many concurrent calls, this adds up to a lot of time saved.\n";
        pressEnter();

        // 6. Demonstrate performance difference.
        echo "\n";
        echo "6. Demonstrate performance difference.\n";
        $downloads = 1000;
        echo "Now, let's do a performance test. We'll download the same object from each bucket $downloads times and compare the total time needed. Note: the performance difference will be much more pronounced if this example is run in an EC2 instance in the same AZ as the bucket.\n";
        $downloadChoice = testable_readline("If you would like to download each object $downloads times, press enter. Otherwise, enter a custom amount and press enter.");
        if($downloadChoice && is_numeric($downloadChoice) && $downloadChoice < 1000000){ // A million is enough. I promise.
            $downloads = $downloadChoice;
        }

        // Download the object $downloads times from each bucket and time it to demonstrate the speed difference.
        $directoryStartTime = hrtime(true);
        for($i = 0; $i < $downloads; ++$i){
            $s3ExpressService->getObject($directoryBucketName, $objectKey);
        }
        $directoryEndTime = hrtime(true);
        $directoryTimeDiff = $directoryEndTime - $directoryStartTime;

        $normalStartTime = hrtime(true);
        for($i = 0; $i < $downloads; ++$i){
            $s3RegularService->getObject($normalBucketName, $objectKey);
        }
        $normalEndTime = hrtime(true);
        $normalTimeDiff = $normalEndTime - $normalStartTime;

        echo "The directory bucket took $directoryTimeDiff nanoseconds, while the normal bucket took $normalTimeDiff.\n";
        echo "That's a difference of " . ($normalTimeDiff - $directoryTimeDiff) . " nanoseconds, or " . (($normalTimeDiff - $directoryTimeDiff)/1000000000) . " seconds.\n";
        pressEnter();

        // 7. Populate the buckets to show the lexicographical difference.
        echo "\n";
        echo "7. Populate the buckets to show the lexicographical difference.\n";
        echo "Now let's explore how Directory buckets store objects in a different manner to regular buckets.\n";
        echo "The key is in the name \"Directory!\"\n";
        echo "Where regular buckets store their key/value pairs in a flat manner, Directory buckets use actual directories/folders.\n";
        echo "This allows for more rapid indexing, traversing, and therefore retrieval times!\n";
        echo "The more segmented your bucket is, with lots of directories, sub-directories, and objects, the more efficient it becomes.\n";
        echo "This structural difference also causes ListObjects to behave differently, which can cause unexpected results.\n";
        echo "Let's add a few more objects with layered directories as see how the output of ListObjects changes.\n";
        pressEnter();

        // Populate a few more files in each bucket so that we can use ListObjects and show the difference.
        $otherObject = "other/$objectKey";
        $altObject = "alt/$objectKey";
        $otherAltObject = "other/alt/$objectKey";
        $s3ExpressService->putObject($directoryBucketName, $otherObject);
        $s3RegularService->putObject($normalBucketName, $otherObject);
        $this->resources['otherObject'] = $otherObject;
        $s3ExpressService->putObject($directoryBucketName, $altObject);
        $s3RegularService->putObject($normalBucketName, $altObject);
        $this->resources['altObject'] = $altObject;
        $s3ExpressService->putObject($directoryBucketName, $otherAltObject);
        $s3RegularService->putObject($normalBucketName, $otherAltObject);
        $this->resources['otherAltObject'] = $otherAltObject;

        $listDirectoryBucket = $s3ExpressService->listObjects($directoryBucketName);
        $listNormalBucket = $s3RegularService->listObjects($normalBucketName);

        // Directory bucket content
        echo "Directory bucket content\n";
        foreach($listDirectoryBucket['Contents'] as $result){
            echo $result['Key'] . "\n";
        }

        // Normal bucket content
        echo "\nNormal bucket content\n";
        foreach($listNormalBucket['Contents'] as $result){
            echo $result['Key'] . "\n";
        }

        echo "Notice how the normal bucket lists objects in lexicographical order, while the directory bucket does not. This is because the normal bucket considers the whole \"key\" to be the object identifies, while the directory bucket actually creates directories and uses the object \"key\" as a path to the object.\n";
        pressEnter();

        echo "\n";
        echo "That's it for our tour of the basic operations for S3 Express One Zone.\n";
        $cleanUp = testable_readline("Would you like to delete all the resources created during this demo? Enter Y/y to delete all the resources.");
        if($cleanUp){
            $this->cleanUp();
        }

        // snippet-end:[php.example_code.s3.ExpressBasics]
    }

    public function helloService()
    {
        return; // There is no value in an S3 Express One Zone Hello Service, but the interface requires a definition.
    }

    public function cleanUp()
    {
        if(isset($this->ec2Service)) {
            // delete VpcEndpoint
            $this->ec2Service->deleteVpcEndpoint($this->resources['vpcEndpointId']);
            unset($this->resources['vpcEndpointId']);

            // delete Vpc
            $this->ec2Service->deleteVpc($this->resources['vpcId']);
            unset($this->resources['vpcId']);
        }

        //delete the stack
        if(isset($this->resources['stackName'])){
            $this->cloudFormationClient->deleteStack([
                'StackName' => $this->resources['stackName'],
            ]);
            unset($this->resources['stackName']);
        }

        // delete all the objects in both buckets
        if(isset($this->resources['objectKey'])){
            $this->s3Service->deleteObject($this->resources['directoryBucketName'], $this->resources['objectKey']);
            $this->s3Service->deleteObject($this->resources['normalBucketName'], $this->resources['objectKey']);
            unset($this->resources['objectKey']);
        }
        if(isset($this->resources['otherObject'])){
            $this->s3Service->deleteObject($this->resources['directoryBucketName'], $this->resources['otherObject']);
            $this->s3Service->deleteObject($this->resources['normalBucketName'], $this->resources['otherObject']);
            unset($this->resources['otherObject']);
        }
        if(isset($this->resources['altObject'])){
            $this->s3Service->deleteObject($this->resources['directoryBucketName'], $this->resources['altObject']);
            $this->s3Service->deleteObject($this->resources['normalBucketName'], $this->resources['altObject']);
            unset($this->resources['altObject']);
        }
        if(isset($this->resources['otherAltObject'])){
            $this->s3Service->deleteObject($this->resources['directoryBucketName'], $this->resources['otherAltObject']);
            $this->s3Service->deleteObject($this->resources['normalBucketName'], $this->resources['otherAltObject']);
            unset($this->resources['otherAltObject']);
        }

        // delete the directory bucket
        if(isset($this->resources['directoryBucketName'])){
            $this->s3Service->deleteBucket($this->resources['directoryBucketName']);
            unset($this->resources['directoryBucketName']);
        }

        // delete the normal bucket
        if(isset($this->resources['normalBucketName'])){
            $this->s3Service->deleteBucket($this->resources['normalBucketName']);
            unset($this->resources['normalBucketName']);
        }

        if(count($this->resources) > 0){
            echo "There was a problem deleting some resources. The following keys are still set: \n";
            var_dump($this->resources);
        }

    }
}
