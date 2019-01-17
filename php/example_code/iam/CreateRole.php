<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/iam-examples-working-with-policies.html
 *
 */
require 'vendor/autoload.php';

use Aws\Iam\IamClient;
use Aws\Exception\AwsException;

/**
 * Creates a new managed policy for your AWS account.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

$client = new IamClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-05-08'
]);

$roleName = 'AmazonCSM';

$description = 'An Instance role that has permission for  Amazon EC2 Systems Manager  and SDK Metric Monitoring.';

$AmazonCSMPolicy = '{
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Action": [
                    "sdkmetrics-beta:*"
                ],
                "Resource": "*"
            },
            {   
                "Effect": "Allow",
                "Action": [
                    "ssm:GetParameter"
                ],
                "Resource": "arn:aws:ssm:*:*:parameter/AmazonCSM*"
            }
        ]
    }';

$rolePolicy = '{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ec2.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}';


try {
    $iamPolicy = $client->createPolicy([
        'PolicyName' => $roleName . 'policy',
        'PolicyDocument' => $AmazonCSMPolicy
    ]);
    if ($iamPolicy['@metadata']['statusCode'] == 200) {
        $policyArn = $iamPolicy['Policy']['Arn'];
        echo('<p> Your IAM Policy has been created. Arn -  ');
        echo($policyArn);
        echo('<p>');
        $role = $client->createRole([
            'RoleName' => $roleName,
            'Description' => $description,
            'AssumeRolePolicyDocument' => $rolePolicy,
        ]);
        echo('<p> Your IAM User Role has been created. Arn: ');
        echo($role['Role']['Arn']);
        echo('<p>');
        if ($role['@metadata']['statusCode'] == 200) {
            $result = $client->attachRolePolicy([
                'PolicyArn' => $policyArn,
                'RoleName' => $roleName,
            ]);
            var_dump($result)
        } else {
            echo('<p> There was an error creating your IAM User Role </p>');
            var_dump($role);
        }
    } else {
        echo('<p> There was an error creating your IAM Policy </p>');
        var_dump($iamPolicy);
        
    }
} catch (AwsException $e) {
    // output error message if fails
    echo $e;
    error_log($e->getMessage());
    
}





//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourcedescription:[CreateRole.php demonstrates how to create an IAM instance role that has permission for Amazon EC2 Systems Manager and SDK Metric Monitoring.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Identity and Access Management (IAM)]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-10-30]
//snippet-sourceauthor:[jschwarzwalder (AWS)]