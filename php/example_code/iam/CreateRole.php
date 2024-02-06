<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/iam-examples-working-with-policies.html
 *
 *
 *
 */
// snippet-start:[iam.php.create_role.complete]
// snippet-start:[iam.php.create_role.import]

require 'vendor/autoload.php';

use Aws\Exception\AwsException;
use Aws\Iam\IamClient;

// snippet-end:[iam.php.create_role.import]

/**
 * Creates a new managed policy for your AWS account.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */

//Create an IAM Client
// snippet-start:[iam.php.create_role.main]
$client = new IamClient([
    'profile' => 'default',
    'region' => 'us-west-2',
    'version' => '2010-05-08'
]);

$roleName = 'AmazonCSM';

$description = 'An Instance role that has permission for  Amazon EC2 Systems Manager  and SDK Metric Monitoring.';

$AmazonCSMPolicy =
'{
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

$rolePolicy =
'{
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
            var_dump($result);
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

// snippet-end:[iam.php.create_role.main]
// snippet-end:[iam.php.create_role.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
